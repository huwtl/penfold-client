package org.huwtl.penfold.client.commands;

import org.huwtl.penfold.client.NewTask;
import org.huwtl.penfold.client.QueueId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class CreateTaskCommand
{
    public final QueueId queue;

    public final Map<String, Object> payload;

    public final Optional<LocalDateTime> triggerDate;

    public CreateTaskCommand(final NewTask task)
    {
        this.queue = task.queue;
        this.triggerDate = task.triggerDate;
        this.payload = task.payload.getAsMap();
    }
}
