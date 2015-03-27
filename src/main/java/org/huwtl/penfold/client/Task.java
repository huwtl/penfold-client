package org.huwtl.penfold.client;

import java.time.LocalDateTime;

public class Task
{
    public final TaskId id;

    public final String version;

    public final QueueId queue;

    public final LocalDateTime created;

    public final TaskStatus status;

    public final LocalDateTime triggerDate;

    public final int attempts;

    public final Payload payload;

    public Task(final TaskId id, final String version, final QueueId queue, final TaskStatus status, final LocalDateTime created, final int attempts,
                final LocalDateTime triggerDate, final Payload payload)
    {
        this.id = id;
        this.version = version;
        this.queue = queue;
        this.created = created;
        this.status = status;
        this.triggerDate = triggerDate;
        this.attempts = attempts;
        this.payload = payload;
    }
}
