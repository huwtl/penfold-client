package org.huwtl.penfold.client.domain.services;

import org.huwtl.penfold.client.app.commands.filter.Filter;
import org.huwtl.penfold.client.domain.model.PageReference;
import org.huwtl.penfold.client.domain.model.QueueId;
import org.huwtl.penfold.client.domain.model.TaskStatus;
import org.huwtl.penfold.client.domain.model.TasksPage;

import java.util.List;
import java.util.Optional;

public interface PageAwareTaskQueryService
{
    TasksPage retrieve(QueueId queue, TaskStatus status, List<Filter> filters, Optional<PageReference> pageRequest);

    TasksPage retrieve(List<Filter> filters, Optional<PageReference> pageRequest);
}
