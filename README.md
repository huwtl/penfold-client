# penfold-client

Java client for consuming from a penfold queue.


#### Usage

Add dependency to your project (available from [Maven central repository](http://search.maven.org/)):

```
<dependency>
    <groupId>org.huwtl</groupId>
    <artifactId>penfold-client</artifactId>
    <version>${VERSION}</version>
</dependency>
```


Configure and start your consumer:

```java
new ConsumerConfiguration()
    .fromServer("http://localhost")
    .withCredentials("user", "pass")
    .fromQueue("testqueue")
    .delayBetweenEachRetryFor(new Interval(15, TimeUnit.MINUTES))
    .consumeWith(new ConsumerFunction() {
        @Override public Result execute(final Task task) {
            // your implementation here
        }})
    .build()
    .start();
```
