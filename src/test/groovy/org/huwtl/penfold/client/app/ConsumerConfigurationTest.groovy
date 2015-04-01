package org.huwtl.penfold.client.app

import org.huwtl.penfold.client.domain.services.ConsumerFunction
import org.huwtl.penfold.client.domain.model.Result
import org.huwtl.penfold.client.domain.model.Task
import org.huwtl.penfold.client.app.support.Interval
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class ConsumerConfigurationTest extends Specification {

    def "should configure consumer"()
    {
        when:
        new ConsumerConfiguration()
                .fromServer("http://localhost")
                .withCredentials("user", "pass")
                .fromQueue("testqueue")
                .withPollingFrequency(new Interval(1, TimeUnit.MINUTES))
                .delayBetweenEachRetryFor(new Interval(15, TimeUnit.MINUTES))
                .consumeWith(new ConsumerFunction() {
                    @Override
                    public Result execute(final Task task) {
                        // your implementation here
                    }})
                .build();

        then:
        true
    }
}
