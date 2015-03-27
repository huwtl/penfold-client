package org.huwtl.penfold.client.consumer;

import com.github.rholder.retry.RetryerBuilder;
import com.google.common.collect.ImmutableList;
import org.huwtl.penfold.client.DateTimeSource;
import org.huwtl.penfold.client.QueueId;
import org.huwtl.penfold.client.Result;
import org.huwtl.penfold.client.Task;
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

public class TaskConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskConsumer.class);

    private static final String DEFAULT_FAILURE_REASON = "FAIL";

    private static final RetryerBuilder<Task> RETRY_BUILDER = RetryerBuilder.<Task>newBuilder() //
            .retryIfException() //
            .withWaitStrategy(fixedWait(10, SECONDS)) //
            .withStopStrategy(stopAfterAttempt(100));

    private static final int RETRY_DELAY_IN_MINUTES = 20;

    private final TaskQueryService taskQueryService;

    private final TaskStoreService taskStoreService;

    private final QueueId queue;

    private final TaskConsumerFunction function;

    private final DateTimeSource dateTimeSource;

    public TaskConsumer(final QueueId queue, final TaskConsumerFunction function, final TaskQueryService taskQueryService, final TaskStoreService taskStoreService,
                        final DateTimeSource dateTimeSource)
    {
        this.taskQueryService = taskQueryService;
        this.taskStoreService = taskStoreService;
        this.queue = queue;
        this.function = function;
        this.dateTimeSource = dateTimeSource;
    }

    public void consume()
    {
        final Iterator<Task> tasks = taskQueryService.find(queue, TaskStatus.READY, ImmutableList.of());

        while (tasks.hasNext())
        {
            final Task task = taskStoreService.start(tasks.next());

            final Result result = executeFunction(task);

            applyResult(task, result);
        }
    }

    private void applyResult(final Task task, final Result result)
    {
        if (result == SUCCESS)
        {
            retry(task, () -> taskStoreService.close(task, Optional.empty()));
        }
        else if (result == FAILURE)
        {
            retry(task, () -> taskStoreService.close(task, Optional.of(DEFAULT_FAILURE_REASON)));
        }
        else if (result == RETRY)
        {
            retry(task, () -> taskStoreService.reschedule(task, dateTimeSource.now().plusMinutes(RETRY_DELAY_IN_MINUTES), Optional.of(DEFAULT_FAILURE_REASON)));
        }
        else
        {
            throw new RuntimeException(String.format("unrecognised result type %s when processing task %s", result, task.id));
        }
    }

    private void retry(final Task task, final Callable<Task> callable)
    {
        try
        {
            RETRY_BUILDER.build().call(callable);
        }
        catch (final Exception e)
        {
            LOGGER.error(String.format("task processed ok, but task %s could not be closed/rescheduled", task.id), e);
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
