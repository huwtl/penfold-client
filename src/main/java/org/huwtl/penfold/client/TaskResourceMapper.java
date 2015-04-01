package org.huwtl.penfold.client;

import com.google.common.reflect.TypeToken;
import com.qmetric.hal.reader.HalResource;

import java.time.LocalDateTime;
import java.util.Map;

class TaskResourceMapper
{
    public Task getTaskFromResource(final HalResource input)
    {
        final TaskId id = new TaskId(input.getValueAsString("id").orNull());
        final String version = input.getValueAsString("version").orNull();
        final Integer attempts = Integer.valueOf(input.getValueAsString("version").orNull());
        final QueueId queueId = new QueueId(input.getValueAsString("queue").orNull());
        final TaskStatus status = new TaskStatus(input.getValueAsString("status").orNull());
        final LocalDateTime created = TaskDateTimeFormatter.parse(input.getValueAsString("created").get());
        final Payload payload = new Payload(input.getValueAsObject("payload", new TypeToken<Map<String, Object>>() {}).get());

        return new Task(id, version, queueId, status, created, attempts, payload);
    }
}
