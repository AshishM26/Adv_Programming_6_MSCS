package main

import (
	"fmt"
	"log"
	"sync"
)

func main() {
	log.SetFlags(0)

	fmt.Println("========================================")
	fmt.Println("Data Processing System Demo")
	fmt.Println("========================================")

	// Concurrency model: the task channel is the shared queue, worker goroutines
	// consume from it, and a collector goroutine owns the final result slice.
	tasks := make(chan Task)
	results := make(chan ProcessedResult)
	var wg sync.WaitGroup

	workerCount := 3
	for i := 1; i <= workerCount; i++ {
		wg.Add(1)
		go worker(fmt.Sprintf("Worker-%d", i), tasks, results, &wg)
	}

	collectionDone := make(chan struct {
		results []ProcessedResult
		err     error
	}, 1)

	// The collector goroutine waits for the results channel to close, writes the
	// file, and returns the aggregated slice to main.
	go func() {
		collected, err := collectResults(results, "go_results.txt")
		collectionDone <- struct {
			results []ProcessedResult
			err     error
		}{results: collected, err: err}
	}()

	sampleTasks := []Task{
		{ID: 1, Payload: 5},
		{ID: 2, Payload: 8},
		{ID: 3, Payload: 12},
		{ID: 4, Payload: -1},
		{ID: 5, Payload: 7},
		{ID: 6, Payload: 3},
		{ID: 7, Payload: 10},
		{ID: 8, Payload: 4},
	}

	for _, task := range sampleTasks {
		tasks <- task
	}
	close(tasks)

	wg.Wait()
	close(results)

	collectionOutcome := <-collectionDone
	if collectionOutcome.err != nil {
		log.Printf("[ERROR] Failed to write results file: %v", collectionOutcome.err)
	}

	logTaskSummary(collectionOutcome.results)

	fmt.Println()
	fmt.Println("========================================")
	fmt.Println("Shutdown Complete")
	fmt.Println("========================================")
	fmt.Println("All tasks processed safely.")
	fmt.Println("Results written to file.")
}
