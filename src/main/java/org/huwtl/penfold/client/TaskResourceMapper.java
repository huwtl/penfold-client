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
        final QueueId queueId = input.getValueAsObject("queue", TypeToken.of(QueueId.class)).get();
        final TaskStatus status = new TaskStatus(input.getValueAsString("status").get());
        final LocalDateTime created = TaskDateTimeFormatter.parse(input.getValueAsString("created").get());
        final LocalDateTime triggerDate = TaskDateTimeFormatter.parse(input.getValueAsString("triggerDate").get());
        final Payload payload = new Payload(input.getValueAsObject("payload", new TypeToken<Map<String, Object>>() {}).get());

        return new Task(id, version, queueId, status, created, 0, triggerDate, payload);
    }
}
