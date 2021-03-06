package org.huwtl.penfold.client.domain.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class Task
{
    public final TaskId id;

    public final String version;

    public final QueueId queue;

    public final LocalDateTime created;

    public final TaskStatus status;

    public final int attempts;

    public final Payload payload;

    public Task(final TaskId id, final String version, final QueueId queue, final TaskStatus status, final LocalDateTime created, final int attempts,
                final Payload payload)
    {
        this.id = id;
        this.version = version;
        this.queue = queue;
        this.created = created;
        this.status = status;
        this.attempts = attempts;
        this.payload = payload;
    }

    public Builder builder()
    {
        return new Builder(this);
    }

    @Override public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Builder
    {
        private TaskId id;

        private String version;

        private QueueId queue;

        private LocalDateTime created;

        private TaskStatus status;

        private int attempts;

        private Payload payload;

        private Builder(final Task task)
        {
            this.id = task.id;
            this.version = task.version;
            this.queue = task.queue;
            this.created = task.created;
            this.status = task.status;
            this.attempts = task.attempts;
            this.payload = task.payload;
        }

        public Builder(final TaskId id)
        {
            this.id = id;
        }

        public Builder withVersion(final String version)
        {
            this.version = version;
            return this;
        }

        public Builder withQueue(final QueueId queue)
        {
            this.queue = queue;
            return this;
        }

        public Builder withCreated(final LocalDateTime created)
        {
            this.created = created;
            return this;
        }

        public Builder withStatus(final TaskStatus status)
        {
            this.status = status;
            return this;
        }

        public Builder withAttempts(final int attempts)
        {
            this.attempts = attempts;
            return this;
        }

        public Builder withPayload(final Payload payload)
        {
            this.payload = payload;
            return this;
        }

        public Task build()
        {
            return new Task(id, version, queue, status, created, attempts, payload);
        }
    }
}
