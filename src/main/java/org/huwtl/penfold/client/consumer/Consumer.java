package org.huwtl.penfold.client.consumer;

import com.github.rholder.retry.RetryerBuilder;
import com.google.common.collect.ImmutableList;
import org.huwtl.penfold.client.DateTimeSource;
import org.huwtl.penfold.client.QueueId;
import org.huwtl.penfold.client.Result;
import org.huwtl.penfold.client.Task;
import org.huwtl.penfold.client.TaskId;
import org.huwtl.penfold.client.TaskQueryService;
import org.huwtl.penfold.client.TaskStatus;
import org.huwtl.penfold.client.TaskStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.github.rholder.retry.StopStrategies.stopAfterAttempt;
import static com.github.rholder.retry.WaitStrategies.fixedWait;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.huwtl.penfold.client.Result.FAILURE;
import static org.huwtl.penfold.client.Result.RETRY;
import static org.huwtl.penfold.client.Result.SUCCESS;

public class Consumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private static final Void VOID = null;

    private static final String DEFAULT_FAILURE_REASON = "FAIL";

    private static final RetryerBuilder<Void> RETRY_BUILDER = RetryerBuilder.<Void>newBuilder() //
            .retryIfException() //
            .withWaitStrategy(fixedWait(10, SECONDS)) //
            .withStopStrategy(stopAfterAttempt(100));

    private final QueueId queue;

    private final ConsumerFunction function;

    private final Optional<Interval> retryDelay;

    private final TaskQueryService taskQueryService;

    private final TaskStoreService taskStoreService;

    private final DateTimeSource dateTimeSource;

    public Consumer(final QueueId queue, final ConsumerFunction function, final Optional<Interval> retryDelay, final TaskQueryService taskQueryService, final TaskStoreService taskStoreService,
                    final DateTimeSource dateTimeSource)
    {
        this.queue = queue;
        this.function = function;
        this.retryDelay = retryDelay;
        this.taskQueryService = taskQueryService;
        this.taskStoreService = taskStoreService;
        this.dateTimeSource = dateTimeSource;
    }

    public void consume()
    {
        final Iterator<Task> tasks = taskQueryService.find(queue, TaskStatus.READY, ImmutableList.of());

        while (tasks.hasNext())
        {
            final Task task = taskStoreService.start(tasks.next());

            final Result result = executeFunction(task);

            applyResultWithRetries(task.id, result);
        }
    }

    private void applyResultWithRetries(final TaskId taskId, final Result result)
    {
        retry(taskId, () -> applyResult(taskId, result));
    }

    private Void applyResult(final TaskId taskId, final Result result)
    {
        final Optional<Task> updatedVersionOfTask = taskQueryService.find(taskId);

        final boolean isTaskStillStarted = updatedVersionOfTask.isPresent() && updatedVersionOfTask.get().status.isStarted();

        if (isTaskStillStarted)
        {
            if (result == SUCCESS)
            {
                success(updatedVersionOfTask);
            }
            else if (result == FAILURE)
            {
                failure(updatedVersionOfTask);
            }
            else
            {
                retry(updatedVersionOfTask);
            }
        }
        else
        {
            LOGGER.info("task {} already closed or rescheduled - doing nothing", taskId);
        }

        return VOID;
    }

    private void success(final Optional<Task> updatedVersionOfTask)
    {
        taskStoreService.close(updatedVersionOfTask.get(), Optional.empty());
    }

    private void failure(final Optional<Task> updatedVersionOfTask)
    {
        closeWithFailure(updatedVersionOfTask);
    }

    private void closeWithFailure(final Optional<Task> updatedVersionOfTask)
    {
        taskStoreService.close(updatedVersionOfTask.get(), Optional.of(DEFAULT_FAILURE_REASON));
    }

    private void retry(final Optional<Task> updatedVersionOfTask)
    {
        if (retryDelay.isPresent())
        {
            taskStoreService.reschedule(updatedVersionOfTask.get(), dateTimeSource.now().plusMinutes(retryDelay.get().seconds()), Optional.of(DEFAULT_FAILURE_REASON));
        }
        else
        {
            taskStoreService.requeue(updatedVersionOfTask.get(), Optional.of(DEFAULT_FAILURE_REASON));
        }
    }

    private void retry(final TaskId taskId, final Callable<Void> callable)
    {
        try
        {
            RETRY_BUILDER.build().call(callable);
        }
        catch (final Exception e)
        {
            LOGGER.error(String.format("task %s processed ok, but could not be closed/rescheduled", taskId), e);
            throw new RuntimeException(e);
        }
    }

    private Result executeFunction(final Task task)
    {
        try
        {
            return function.execute(task);
        }
        catch (Exception e)
        {
            return RETRY;
        }
    }
}
