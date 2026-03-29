# Adv_Programming_6_MSCS

Project contains the same multi-threaded data processing system in Java and Go. Both versions process a fixed set of tasks in parallel, handle one invalid task safely, log worker activity, and write final results to a text file.

## Tree: Data Processing System

```text
Adv_Programming_6_MSCS/
├── README.md
└── data-processing-system/
    ├── .gitignore
    ├── README.md
    ├── java/
    │   ├── README.md
    │   ├── Task.java
    │   ├── ProcessedResult.java
    │   ├── SharedTaskQueue.java
    │   ├── ResultStore.java
    │   ├── Worker.java
    │   └── DataProcessingSystem.java
    └── go/
        ├── README.md
        ├── go.mod
        ├── task.go
        ├── result.go
        ├── worker.go
        ├── collector.go
        └── main.go
```