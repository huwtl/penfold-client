package org.huwtl.penfold.client.app.commands;

import org.huwtl.penfold.client.domain.model.NewTask;
import org.huwtl.penfold.client.domain.model.QueueId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class CreateTaskCommand
{
    public final QueueId queue;

    public final Map<String, Object> payload;

    public final Optional<LocalDateTime> triggerDate;

    public CreateTaskCommand()
    {
        queue = null;
        payload = null;
        triggerDate = Optional.empty();
    }

    public CreateTaskCommand(final NewTask task)
    {
        this.queue = task.queue;
        this.payload = task.payload.getAsMap();
        this.triggerDate = task.triggerDate;
    }
}
