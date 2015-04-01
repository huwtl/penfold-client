package org.huwtl.penfold.client.domain.services;

import org.huwtl.penfold.client.domain.model.Result;
import org.huwtl.penfold.client.domain.model.Task;

public interface ConsumerFunction
{
    Result execute(Task task);
}
