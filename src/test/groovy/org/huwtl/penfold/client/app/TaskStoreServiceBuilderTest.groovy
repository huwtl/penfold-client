package org.huwtl.penfold.client.app

import spock.lang.Specification

class TaskStoreServiceBuilderTest extends Specification {

    def "should build task store service"()
    {
        expect:
        new TaskStoreServiceBuilder()
                .fromServer("http://localhost")
                .withCredentials("user", "pass")
                .build();
    }
}
