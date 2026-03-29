package main

import "fmt"

// ProcessedResult captures both success and failure cases so the final summary
// can show what each worker did with each task.
type ProcessedResult struct {
	TaskID      int
	Payload     int
	WorkerName  string
	ResultValue int
	Success     bool
	Message     string
}

func (r ProcessedResult) String() string {
	return fmt.Sprintf(
		"Task %d | Payload=%d | Worker=%s | Result=%d | Success=%t | Message=%s",
		r.TaskID,
		r.Payload,
		r.WorkerName,
		r.ResultValue,
		r.Success,
		r.Message,
	)
}
