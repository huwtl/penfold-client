package org.huwtl.penfold.client.domain.services

import com.github.rholder.retry.RetryerBuilder
import org.huwtl.penfold.client.app.support.Interval
import org.huwtl.penfold.client.app.support.LocalDateTimeSource
import org.huwtl.penfold.client.domain.model.*
import spock.lang.Specification

import java.time.LocalDateTime

import static com.github.rholder.retry.StopStrategies.stopAfterAttempt
import static java.util.Optional.empty
import static java.util.concurrent.TimeUnit.HOURS
import static org.huwtl.penfold.client.domain.model.TaskStatus.*

class ConsumerTest extends Specification {

    static final queueId = new QueueId("q1")

    static final retryDelay = new Interval(2, HOURS)

    static final retryDelayInSeconds = 120

    static final failureReason = Optional.of("err")

    static final readyTask1 = new Task(new TaskId("t1"), "1", queueId, READY, LocalDateTime.now(), 0, new Payload([:]))

    static final readyTask2 = new Task(new TaskId("t2"), "1", queueId, READY, LocalDateTime.now(), 0, new Payload([:]))

    static final startedTask1 = readyTask1.builder().withStatus(STARTED).build()

    static final startedTask2 = readyTask2.builder().withStatus(STARTED).build()

    final consumerFunction = Mock(ConsumerFunction)

    final taskQueryService = Mock(TaskQueryService)

    final taskStoreService = Mock(TaskStoreService)

    final dateTimeSource = Mock(LocalDateTimeSource)

    def now = LocalDateTime.now()

    final consumer = new Consumer(queueId, consumerFunction, Optional.of(retryDelay), taskQueryService, taskStoreService, dateTimeSource)

    def setup()
    {
        dateTimeSource.now() >> now
        taskStoreService.start(readyTask1) >> startedTask1
        taskStoreService.start(readyTask2) >> startedTask2
    }

    def "should consume tasks successfully"()
    {
        given:
        taskQueryService.find(queueId, READY, []) >> [readyTask1, readyTask2].iterator()
        consumerFunction.execute(startedTask1) >> Result.success()
        consumerFunction.execute(startedTask2) >> Result.success()
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1)
        taskQueryService.find(startedTask2.id) >> Optional.of(startedTask2)

        when:
        consumer.consume()

        then:
        1 * taskStoreService.close(startedTask1, Optional.of(CloseResultType.success), empty())
        1 * taskStoreService.close(startedTask2, Optional.of(CloseResultType.success), empty())
    }

    def "should close task on consume failure"()
    {
        given:
        taskQueryService.find(queueId, READY, []) >> [readyTask1, readyTask2].iterator()
        consumerFunction.execute(startedTask1) >> Result.fail(failureReason)
        consumerFunction.execute(startedTask2) >> Result.success()
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1)
        taskQueryService.find(startedTask2.id) >> Optional.of(startedTask2)

        when:
        consumer.consume()

        then:
        1 * taskStoreService.close(startedTask1, Optional.of(CloseResultType.failure), failureReason)
        1 * taskStoreService.close(startedTask2, Optional.of(CloseResultType.success), empty())
    }

    def "should reschedule task on consume failure when delayed retry applicable"()
    {
        given:
        taskQueryService.find(queueId, READY, []) >> [readyTask1, readyTask2].iterator()
        consumerFunction.execute(startedTask1) >> Result.retry(failureReason)
        consumerFunction.execute(startedTask2) >> Result.success()
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1)
        taskQueryService.find(startedTask2.id) >> Optional.of(startedTask2)

        when:
        consumer.consume()

        then:
        1 * taskStoreService.reschedule(startedTask1, now.plusSeconds(retryDelayInSeconds), failureReason)
        1 * taskStoreService.close(startedTask2, Optional.of(CloseResultType.success), empty())
    }

    def "should ignore tasks where consumer function updates task status"()
    {
        given:
        taskQueryService.find(queueId, READY, []) >> [readyTask1].iterator()
        consumerFunction.execute(startedTask1) >> Result.success()
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1.builder().withStatus(CLOSED).build())

        when:
        consumer.consume()

        then:
        0 * taskStoreService.close(_ as Task, _ as Optional<CloseResultType>, _ as Optional<String>)
        0 * taskStoreService.reschedule(_ as Task, _ as LocalDateTime, _ as Optional<String>)
        0 * taskStoreService.requeue(_ as Task, _ as Optional<String>)
    }

    def "should requeue task on consume failure when delayed retry applicable"()
    {
        given:
        final consumer = new Consumer(queueId, consumerFunction, empty(), taskQueryService, taskStoreService, dateTimeSource)
        taskQueryService.find(queueId, READY, []) >> [readyTask1].iterator()
        consumerFunction.execute(startedTask1) >> Result.retry(failureReason)
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1)

        when:
        consumer.consume()

        then:
        1 * taskStoreService.requeue(startedTask1, failureReason)
    }

    def "should retry when attempt to close task fails"()
    {
        given:
        final retryBuilder = RetryerBuilder.<Void>newBuilder().retryIfException().withStopStrategy(stopAfterAttempt(2))
        final consumer = new Consumer(queueId, consumerFunction, Optional.of(retryDelay), taskQueryService, taskStoreService, dateTimeSource, retryBuilder)
        taskQueryService.find(queueId, READY, []) >> [readyTask1].iterator()
        consumerFunction.execute(startedTask1) >> Result.fail(failureReason)
        taskQueryService.find(startedTask1.id) >> Optional.of(startedTask1)

        when:
        consumer.consume()

        then:
        2 * taskStoreService.close(startedTask1, Optional.of(CloseResultType.failure), failureReason) >> {throw new RuntimeException()}
        thrown(RuntimeException)
    }
}
