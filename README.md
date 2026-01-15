# Java Virtual Threading Examples 

This repository was built to support a [talk on Java's virtual threads and structured concurrency](https://docs.google.com/presentation/d/1hzSnyir5_3TAXnjFKGC_1KGoWiSzZ92qM6NpAWgm_l8/edit#slide=id.p).


## Virtual Threads

### [Cpu Intensive Experiment](client/src/main/java/org/threading/virtual/simple/CpuIntensiveTasks.java)

The first experiment compares the performance of traditional threading approaches against virtual threads when performing
a CPU intensive task

Observations:

Virtual threads do provide benefits vs traditional threads when performing CPU intensive tasks.

### [Blocking Task Experiment]((client/src/main/java/org/threading/virtual/simple/BlockingTasks.java))

This experiment compares the performance of traditional threading approaches against virtual threads when performing
tasks that block on IO (or in this case, sleep).

Observations:

You can create a much larger number of virtual threads without exhausting the operating system's resources. Attempts to
create a fraction of the number of traditional threads with result in an exception as process/resource limits are reached.

### Http Client/Server Experiment

This experiment requires that you run an http server on port 8080 prior to running the client experiments. This is to
simulate the traditional client/server model where the client makes a request to a server and waits for a response.

#### Running the server:

The [SleepyServerApplication](sleepy-server/src/main/java/org/threading/sleepyserver/SleepyServerApplication.java) is a
simple Spring Boot application that listens on port 8080 and sleeps for 2 second before responding to any request.

The [application.yml](sleepy-server/src/main/resources/application.yml) file contains the following configuration:

```yaml
spring:
  threads:
    virtual:
      enabled: false
server:
  tomcat:
    accept-count: 2000
    max-connections: 2000
```

You should attempt to run the experiments with/without the `spring.threads.virtual.enabled` property set to `true` to see
the differences in performance.

#### Running the various clients:

There are three clients that you can use against the server:

1. [HttpClientReactive](client/src/main/java/org/threading/virtual/http/HttpClientReactive.java)
2. [HttpClientTraditionalThreads](client/src/main/java/org/threading/virtual/http/HttpClientTraditionalThreads.java)
3. [HttpClientVirtualThreads](client/src/main/java/org/threading/virtual/http/HttpClientVirtualThreads.java)

Observations:

The clients each attempt to make concurrent requests to the server, you can tweak the number of requests in each client
to find their "breaking points" when running against the server.

One thing you will quickly learn is you must enable virtual threads on the server to increase the performance of all
the clients.

## Structured Concurrency

To quote JEP 453: Structured Concurrency:

"Simplify concurrent programming by introducing an API for structured concurrency. Structured concurrency treats groups
of related tasks running in different threads as a single unit of work, thereby streamlining error handling and
cancellation, improving reliability, and enhancing observability"

The following experiments are meant to compare how one might achieve structured concurrent using various approaches and
highlight the complexities of the traditional approaches.

### [Using Futures and the ExecutorService to attempt structured concurrency](client/src/main/java/org/threading/coordinate/UnstructuredExperiments.java)

The use of Futures and the ExecutorService provides a primitive way in which to run tasks and subtasks concurrently and
when those tasks succeed, using this approach appears to be straightforward. However, when one of the subtasks fail,
it becomes difficult to cancel the other subtasks resulting in "leaked threads". If you wrap each executor in a try-with-resource
block, it will wait for all tasks to complete even when one of the tasks has failed. There are ways to work around this
but it is easy to get wrong.

You may use the above test harness to experiment with concurrent tasks and adjust wait/failure states.

### [Using a reactive framework to achieve structured concurrency](client/src/main/java/org/threading/coordinate/ReactiveExperiments.java)

Reactive frameworks like Project Reactor and RxJava provide a way to achieve structured concurrency by allowing you to
build reactive pipelines that can be composed of multiple tasks and subtasks. This programming model works but it can be
difficult to debug when compared to imperative programming.

### [Using Project Loom's StructuredTaskScope](client/src/main/java/org/threading/coordinate/StructuredExperiments.java)

The last set of experiments demonstrates the various capabilities of Project Loom's StructuredTaskScope. This API provides
a simplified way to run tasks and subtasks concurrently, has robust error handling and provides strategies for completing/
cancelling the tasks.

### Bonus material

- [debugging Virtual Threads at Netflix](https://netflixtechblog.com/java-21-virtual-threads-dude-wheres-my-lock-3052540e231d)
 