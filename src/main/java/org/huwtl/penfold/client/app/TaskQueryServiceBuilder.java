package org.huwtl.penfold.client.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import org.huwtl.penfold.client.app.support.Credentials;
import org.huwtl.penfold.client.app.support.ObjectMapperFactory;
import org.huwtl.penfold.client.domain.services.TaskQueryService;

import static com.google.common.base.Preconditions.checkArgument;

public class TaskQueryServiceBuilder
{
    private String url;

    private Credentials credentials;

    public TaskQueryServiceBuilder forServer(final String url)
    {
        this.url = url;
        return this;
    }

    public TaskQueryServiceBuilder withCredentials(final String username, final String password)
    {
        this.credentials = new Credentials(username, password);
        return this;
    }

    public TaskQueryService build()
    {
        checkValid();

        final Client client = Client.create();

        final ObjectMapper objectMapper = ObjectMapperFactory.create();

        return new TaskQueryServiceImpl(url, credentials, client, objectMapper);
    }

    private void checkValid()
    {
        checkArgument(url != null, "missing url");
        checkArgument(credentials != null, "missing credentials");
    }
}
