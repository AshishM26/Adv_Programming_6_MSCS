# Go Version: Data Processing System

This folder contains the Go implementation of the multi-threaded data processing system.

## Purpose

The Go version demonstrates:

- goroutines as workers
- channels for task distribution and result delivery
- a `WaitGroup` for worker lifecycle control
- explicit error checking instead of exceptions
- clean file and channel shutdown

## File Overview

- `task.go` - task struct used by workers
- `result.go` - processed result struct and formatting
- `worker.go` - worker goroutine logic
- `collector.go` - result aggregation and file writing
- `main.go` - program entry point and orchestration

## Run

From this directory:

```bash
go run .
```

## Concurrency Approach

The program uses a channel as the shared task queue. Multiple worker goroutines read from the task channel, process each task, and send results to the results channel. A `WaitGroup` lets main wait until all workers finish before the results channel is closed.

A separate collector goroutine receives completed results, stores them in a slice, and writes the final text file when the channel closes. This keeps the responsibilities simple and easy to explain.

## Error Handling Approach

Go uses explicit error returns instead of exceptions. The worker checks task validity and turns invalid payloads into failed result records. The collector checks file creation and write errors and returns them to main.

## Resource Management Notes

- `defer` closes files when they are opened.
- Channels are closed in a predictable order.
- The task channel is closed after all tasks are submitted.
- The results channel is closed only after all workers finish.
- The collector writes the output file after consuming the full results stream.