package org.huwtl.penfold.client.consumer;

import org.huwtl.penfold.client.Result;
import org.huwtl.penfold.client.Task;

public interface TaskConsumerFunction
{
    Result execute(Task task);
}
