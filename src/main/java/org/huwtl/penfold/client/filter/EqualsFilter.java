package org.huwtl.penfold.client.filter;

public class EqualsFilter extends Filter
{
    public final String value;

    public EqualsFilter(final String key, final String value)
    {
        super(Operation.EQ, key);
        this.value = value;
    }

    public static Filter of(final String key, final String value)
    {
        return new EqualsFilter(key, value);
    }
}
