package org.huwtl.penfold.client.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import org.huwtl.penfold.client.domain.services.ConsumerFunction;
import org.huwtl.penfold.client.domain.services.TaskConsumer;
import org.huwtl.penfold.client.domain.services.TaskQueryService;
import org.huwtl.penfold.client.domain.services.TaskStoreService;
import org.huwtl.penfold.client.app.support.Credentials;
import org.huwtl.penfold.client.app.support.LocalDateTimeSource;
import org.huwtl.penfold.client.app.support.ObjectMapperFactory;
import org.huwtl.penfold.client.domain.services.Consumer;
import org.huwtl.penfold.client.app.support.Interval;
import org.huwtl.penfold.client.domain.model.QueueId;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.MINUTES;

public class TaskConsumerBuilder
{
    private String url;

    private QueueId queue;

    private ConsumerFunction function;

    private Credentials credentials;

    private Interval pollingFrequency = new Interval(1, MINUTES);

    private Optional<Interval> retryDelay = Optional.empty();

    public TaskConsumerBuilder fromServer(final String url)
    {
        this.url = url;
        return this;
    }

    public TaskConsumerBuilder withCredentials(final String username, final String password)
    {
        this.credentials = new Credentials(username, password);
        return this;
    }

    public TaskConsumerBuilder fromQueue(final String queue)
    {
        this.queue = new QueueId(queue);
        return this;
    }

    public TaskConsumerBuilder consumeWith(final ConsumerFunction function)
    {
        this.function = function;
        return this;
    }

    public TaskConsumerBuilder withPollingFrequency(final Interval pollingFrequency)
    {
        this.pollingFrequency = pollingFrequency;
        return this;
    }

    public TaskConsumerBuilder delayBetweenEachRetryOf(final Interval retryDelay)
    {
        this.retryDelay = Optional.of(retryDelay);
        return this;
    }

    public TaskConsumer build()
    {
        checkValid();

        final Client client = Client.create();
        final ObjectMapper objectMapper = ObjectMapperFactory.create();

        final TaskQueryService taskQueryService = new TaskQueryServiceImpl(url, credentials, client, objectMapper);
        final TaskStoreService taskStoreService = new TaskStoreServiceImpl(url, credentials, client, objectMapper);

        final LocalDateTimeSource dateTimeSource = new LocalDateTimeSource();

        final Consumer consumer = new Consumer(queue, function, retryDelay, taskQueryService, taskStoreService, dateTimeSource);

        return new TaskConsumerImpl(consumer, pollingFrequency);
    }

    private void checkValid()
    {
        checkArgument(url != null, "missing url");
        checkArgument(queue != null, "missing queue");
        checkArgument(function != null, "missing function");
        checkArgument(credentials != null, "missing credentials");
        checkArgument(pollingFrequency != null, "missing polling frequency");
        checkArgument(retryDelay != null, "missing retry delay");
    }
}
