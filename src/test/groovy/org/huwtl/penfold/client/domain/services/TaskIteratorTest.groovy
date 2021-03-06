package org.huwtl.penfold.client.domain.services

import com.google.common.collect.ImmutableList
import org.huwtl.penfold.client.app.TaskQueryServiceImpl
import org.huwtl.penfold.client.domain.model.*
import spock.lang.Specification

import java.time.LocalDateTime

import static java.util.Optional.empty
import static org.huwtl.penfold.client.domain.model.TaskStatus.READY

class TaskIteratorTest extends Specification {

    final taskQueryService = Mock(TaskQueryServiceImpl)

    def "should paginate through all pages of stored tasks"()
    {
        given:
        final task1 = createTask("1")
        final task2 = createTask("2")
        final task3 = createTask("3")
        final task4 = createTask("4")
        final task5 = createTask("5")
        final task6 = createTask("6")
        setupTasksPage(empty(), [task1, task2, task3], Optional.of(new PageReference("2")))
        setupTasksPage(Optional.of(new PageReference("2")), [task4, task5], Optional.of(new PageReference("3")))
        setupTasksPage(Optional.of(new PageReference("3")), [task6], empty())

        when:
        final readyTaskIterator = new TaskIterator([], taskQueryService)

        then:
        ImmutableList.copyOf(readyTaskIterator) == [task1, task2, task3, task4, task5, task6]
    }

    private static def Task createTask(final String id)
    {
        return new Task(new TaskId(id), "1", new QueueId("q1"), READY, LocalDateTime.of(2014, 2, 25, 12, 0, 0), 1, new Payload([type: "type1"]))
    }

    private def setupTasksPage(final Optional<PageReference> currentPage, final List<Task> currentPageTasks, final Optional<PageReference> nextPage)
    {
        taskQueryService.retrieve([], currentPage) >> new TasksPage(currentPageTasks, empty(), nextPage)
    }
}
