package org.huwtl.penfold.client;

import java.util.Optional;

public class Result
{
    public final ResultType type;

    public final Optional<String> reason;

    private Result(final ResultType type, final Optional<String> reason)
    {
        this.type = type;
        this.reason = reason;
    }

    public static Result success()
    {
        return new Result(ResultType.SUCCESS, Optional.empty());
    }

    public static Result fail(final Optional<String> reason)
    {
        return new Result(ResultType.FAIL, reason);
    }

    public static Result retry(final Optional<String> reason)
    {
        return new Result(ResultType.RETRY, reason);
    }
}
