# Java Version: Data Processing System

This folder contains the Java implementation of the multi-threaded data processing system.

## Purpose

The Java version demonstrates:

- a shared task queue
- worker threads that process tasks in parallel
- synchronization through thread-safe collections and blocking queues
- exception handling for invalid tasks, interruptions, and file writing
- safe shutdown using a poison-pill pattern

## File Overview

- `Task.java` - immutable task object with an optional poison-pill flag
- `ProcessedResult.java` - result record for completed or failed work
- `SharedTaskQueue.java` - thread-safe wrapper around the queue
- `ResultStore.java` - synchronized shared result storage and file writing
- `Worker.java` - worker logic for task processing
- `DataProcessingSystem.java` - program entry point

## Compile

From this directory:

```bash
javac *.java
```

## Run

```bash
java DataProcessingSystem
```

## Concurrency Approach

The program uses `ExecutorService` with a fixed thread pool. Each worker runs as a `Runnable` and pulls tasks from a `LinkedBlockingQueue` through the `SharedTaskQueue` wrapper. The queue protects access to shared work items so only one worker receives each task.

A poison-pill task is added for each worker after the real tasks are queued. That gives every worker a clear, deadlock-free stop signal.

## Exception Handling Approach

Java uses `try-catch` blocks to handle:

- `InterruptedException` during queue access or sleep
- invalid payload validation with `IllegalArgumentException`
- `IOException` while writing the results file

The worker logs failures and stores them as result objects instead of stopping the whole program.

## Resource Management Notes

- `try-with-resources` closes the output file automatically.
- The executor is shut down after all workers finish.
- The shared result list is synchronized so results are safe to read and write.
- The design avoids deadlocks because workers only block on the queue and the queue is guaranteed to receive a poison pill for each worker.