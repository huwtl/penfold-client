package org.huwtl.penfold.client;

public class TaskConsumer
{
    public Result consume(final TaskConsumerFunction function)
    {
        return function.execute(null);
    }
}
