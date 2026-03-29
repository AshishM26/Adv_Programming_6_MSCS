package main

// Task is the unit of work sent through the shared channel.
// Keeping it small makes the worker-pool example easy to follow.
type Task struct {
	ID      int
	Payload int
}
