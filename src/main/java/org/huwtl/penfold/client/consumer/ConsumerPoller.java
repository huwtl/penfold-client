package org.huwtl.penfold.client.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class ConsumerPoller
{
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerPoller.class);

    private final Consumer consumer;

    private final Interval interval;

    private final ScheduledExecutorService scheduledExecutorService;

    private final ShutdownProcedure shutdownProcedure;

    public ConsumerPoller(final Consumer consumer, final Interval interval)
    {
        this.consumer = consumer;
        this.interval = interval;
        this.scheduledExecutorService = newSingleThreadScheduledExecutor();
        this.shutdownProcedure = new ShutdownProcedure(scheduledExecutorService);
    }

    public void start()
    {
        scheduledExecutorService.scheduleAtFixedRate(this::consume, 0, interval.duration, interval.unit);
        shutdownProcedure.registerShutdownHook();
    }

    public void stop()
    {
        shutdownProcedure.runAndRemoveHook();
    }

    private void consume()
    {
        try
        {
            consumer.consume();
        }
        catch (final Exception e)
        {
            LOG.error("failed to consume", e);
        }
    }
}
