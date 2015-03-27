package org.huwtl.penfold.client;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Payload
{
    public static final Payload empty = new Payload(ImmutableMap.of());

    private final Map<String, Object> map;

    public Payload(final Map<String, Object> map)
    {
        this.map = map;
    }

    public Map<String, Object> getAsMap()
    {
        return map;
    }
}
