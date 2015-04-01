package org.huwtl.penfold.client.app.commands;

import java.util.Optional;

public class CloseTaskCommand
{
    public final Optional<String> reason;

    public CloseTaskCommand(final Optional<String> reason)
    {
        this.reason = reason;
    }
}