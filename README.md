# penfold-client

Java client for [penfold](https://github.com/huwtl/penfold).


## Usage

#### Add dependency to your project:

```
<dependency>
    <groupId>org.huwtl</groupId>
    <artifactId>penfold-client</artifactId>
    <version>${VERSION}</version>
</dependency>
```

#### Configure a query service:

Use this if you wish to query tasks.

```java
final TaskQueryService service = new TaskQueryServiceBuilder()
    .forServer("http://localhost")
    .withCredentials("user", "pass")
    .build();
```


#### Configure a store:

Use this if you wish to create or update tasks.

```java
final TaskStoreService service = new TaskStoreServiceBuilder()
    .forServer("http://localhost")
    .withCredentials("user", "pass")
    .build();
```


#### Configure and start a consumer:

Use this if you wish to consume from a queue of tasks.

```java
new TaskConsumerBuilder()
    .fromServer("http://localhost")
    .withCredentials("user", "pass")
    .fromQueue("testqueue")
    .delayBetweenEachRetryOf(15, TimeUnit.MINUTES)
    .consumeWith(new ConsumerFunction() {
        @Override public Result execute(final Task task) {
            // your implementation here
        }})
    .build()
    .start();
```