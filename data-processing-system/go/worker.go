package main

import (
	"fmt"
	"log"
	"time"
	"sync"
)

// worker runs as a goroutine. It reads from the task channel, processes each
// task, and sends a result to the results channel. The wait group lets main
// know when all workers have exited safely.
func worker(name string, tasks <-chan Task, results chan<- ProcessedResult, wg *sync.WaitGroup) {
	defer wg.Done()

	log.Printf("[START] %s started", name)

	for task := range tasks {
		log.Printf("[PROCESSING] %s picked Task %d (payload=%d)", name, task.ID, task.Payload)

		result := processTask(name, task)
		results <- result

		if result.Success {
			log.Printf("[COMPLETED] %s finished Task %d -> result=%d", name, task.ID, result.ResultValue)
		} else {
			log.Printf("[ERROR] %s failed Task %d (payload=%d): %s", name, task.ID, task.Payload, result.Message)
		}
	}

	log.Printf("[EXIT] %s exited cleanly", name)
}

func processTask(workerName string, task Task) ProcessedResult {
	// The sleep is a simple simulation of real work and helps make the
	// concurrent worker behavior visible in the console.
	time.Sleep(150*time.Millisecond + time.Duration(task.ID%3)*75*time.Millisecond)

	if task.Payload < 0 {
		return ProcessedResult{
			TaskID:      task.ID,
			Payload:     task.Payload,
			WorkerName:  workerName,
			ResultValue: 0,
			Success:     false,
			Message:     "negative payload is invalid",
		}
	}

	resultValue := task.Payload * task.Payload
	return ProcessedResult{
		TaskID:      task.ID,
		Payload:     task.Payload,
		WorkerName:  workerName,
		ResultValue: resultValue,
		Success:     true,
		Message:     "Processed successfully",
	}
}

func logTaskSummary(results []ProcessedResult) {
	fmt.Println()
	fmt.Println("========================================")
	fmt.Println("Final Results Summary")
	fmt.Println("========================================")
	for _, result := range results {
		fmt.Println(result.String())
	}
}
