package org.huwtl.penfold.client.consumer;

import org.huwtl.penfold.client.Result;
import org.huwtl.penfold.client.Task;

public interface ConsumerFunction
{
    Result execute(Task task);
}
