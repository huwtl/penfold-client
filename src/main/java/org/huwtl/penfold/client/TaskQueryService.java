package org.huwtl.penfold.client;

import org.huwtl.penfold.client.filter.Filter;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface TaskQueryService
{
    Optional<Task> find(TaskId id);

    Iterator<Task> find(QueueId queue, TaskStatus status, List<Filter> filters);

    Iterator<Task> find(List<Filter> filters);
}
