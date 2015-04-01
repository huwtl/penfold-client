package org.huwtl.penfold.client.app.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ObjectMapperFactory
{
    public static ObjectMapper create()
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(JsonModuleFactory.create());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }
}
