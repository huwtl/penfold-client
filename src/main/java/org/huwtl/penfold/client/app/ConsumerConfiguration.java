package org.huwtl.penfold.client.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import org.huwtl.penfold.client.domain.services.ConsumerFunction;
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

public class ConsumerConfiguration
{
    private String url;

    private QueueId queue;

    private ConsumerFunction function;

    private Credentials credentials;

    private Interval pollingFrequency = new Interval(1, MINUTES);

    private Optional<Interval> retryDelay = Optional.empty();

    public ConsumerConfiguration fromServer(final String url)
    {
        this.url = url;
        return this;
    }

    public ConsumerConfiguration withCredentials(final String username, final String password)
    {
        this.credentials = new Credentials(username, password);
        return this;
    }

    public ConsumerConfiguration fromQueue(final String queue)
    {
        this.queue = new QueueId(queue);
        return this;
    }

    public ConsumerConfiguration consumeWith(final ConsumerFunction function)
    {
        this.function = function;
        return this;
    }

    public ConsumerConfiguration withPollingFrequency(final Interval pollingFrequency)
    {
        this.pollingFrequency = pollingFrequency;
        return this;
    }

    public ConsumerConfiguration delayBetweenEachRetryFor(final Interval retryDelay)
    {
        this.retryDelay = Optional.of(retryDelay);
        return this;
    }

    public ConsumerPoller build()
    {
        checkValid();

        final Client client = Client.create();
        final ObjectMapper objectMapper = ObjectMapperFactory.create();

        final TaskQueryService taskQueryService = new TaskQueryServiceImpl(url, credentials, client, objectMapper);
        final TaskStoreService taskStoreService = new TaskStoreServiceImpl(url, credentials, client, objectMapper);

        final LocalDateTimeSource dateTimeSource = new LocalDateTimeSource();

        final Consumer consumer = new Consumer(queue, function, retryDelay, taskQueryService, taskStoreService, dateTimeSource);

        return new ConsumerPoller(consumer, pollingFrequency);
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
