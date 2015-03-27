package org.huwtl.penfold.client;

import org.huwtl.penfold.client.filter.Filter;

import java.util.List;

public class TaskIterator extends AbstractTaskIterator
{
    public final List<Filter> filters;

    public TaskIterator(final List<Filter> filters, TaskQueryServiceImpl taskQueryService)
    {
        super(taskQueryService);
        this.filters = filters;
    }

    protected TasksPage loadPageOfTasks(final java.util.Optional<PageReference> pageReference)
    {
        return taskQueryService.retrieve(filters, pageReference);
    }
}
