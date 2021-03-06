package org.huwtl.penfold.client.domain.services;

import org.huwtl.penfold.client.app.commands.filter.Filter;
import org.huwtl.penfold.client.domain.model.PageReference;
import org.huwtl.penfold.client.domain.model.TasksPage;

import java.util.List;

public class TaskIterator extends AbstractTaskIterator
{
    public final List<Filter> filters;

    public TaskIterator(final List<Filter> filters, final PageAwareTaskQueryService taskQueryService)
    {
        super(taskQueryService);
        this.filters = filters;
    }

    protected TasksPage loadPageOfTasks(final java.util.Optional<PageReference> pageReference)
    {
        return taskQueryService.retrieve(filters, pageReference);
    }
}
