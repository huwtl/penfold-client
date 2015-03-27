package org.huwtl.penfold.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.core.Version.unknownVersion;

public class JsonModuleFactory
{
    public static Module create()
    {
        final SimpleModule module = new SimpleModule("jacksonConfig", unknownVersion());

        module.addSerializer(LocalDateTime.class, new DateTimeJsonSerializer());
        module.addSerializer(TaskId.class, new TaskIdJsonSerializer());
        module.addSerializer(TaskStatus.class, new TaskStatusJsonSerializer());
        module.addSerializer(QueueId.class, new QueueIdJsonSerializer());

        module.addDeserializer(LocalDateTime.class, new DateTimeJsonDeserializer());
        module.addDeserializer(PageReference.class, new PageReferenceJsonDeserializer());
        module.addDeserializer(QueueId.class, new QueueIdJsonDeserializer());

        return module;
    }

    private static class QueueIdJsonSerializer extends JsonSerializer<QueueId>
    {
        @Override public void serialize(final QueueId queueId, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeString(queueId.value);
        }
    }

    private static class QueueIdJsonDeserializer extends JsonDeserializer<QueueId>
    {
        @Override public QueueId deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
        {
            return new QueueId(jp.getValueAsString());
        }
    }

    private static class TaskIdJsonSerializer extends JsonSerializer<TaskId>
    {
        @Override public void serialize(final TaskId taskId, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeString(taskId.toString());
        }
    }

    private static class TaskStatusJsonSerializer extends JsonSerializer<TaskStatus>
    {
        @Override public void serialize(final TaskStatus taskStatus, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeString(taskStatus.toString());
        }
    }

    private static class PageReferenceJsonDeserializer extends JsonDeserializer<PageReference>
    {
        @Override public PageReference deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
        {
            return new PageReference(jp.getValueAsString());
        }
    }

    private static class DateTimeJsonSerializer extends JsonSerializer<LocalDateTime>
    {
        @Override public void serialize(final LocalDateTime dateTime, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeString(TaskDateTimeFormatter.print(dateTime));
        }
    }

    private static class DateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime>
    {
        @Override public LocalDateTime deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
        {
            return TaskDateTimeFormatter.parse(jp.getValueAsString());
        }
    }
}
