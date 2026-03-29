import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DataProcessingSystem {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Data Processing System Demo");
        System.out.println("========================================");

        // Concurrency management: the executor manages a fixed set of worker threads.
        SharedTaskQueue taskQueue = new SharedTaskQueue();
        ResultStore resultStore = new ResultStore();
        int workerCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(workerCount);

        List<Task> sampleTasks = new ArrayList<>();
        sampleTasks.add(new Task(1, 5));
        sampleTasks.add(new Task(2, 8));
        sampleTasks.add(new Task(3, 12));
        sampleTasks.add(new Task(4, -1));
        sampleTasks.add(new Task(5, 7));
        sampleTasks.add(new Task(6, 3));
        sampleTasks.add(new Task(7, 10));
        sampleTasks.add(new Task(8, 4));

        for (Task task : sampleTasks) {
            taskQueue.addTask(task);
        }

        // Poison pills provide a clear and deadlock-free shutdown path.
        for (int i = 0; i < workerCount; i++) {
            taskQueue.addTask(Task.poisonPill());
        }

        for (int i = 1; i <= workerCount; i++) {
            executorService.submit(new Worker("Worker-" + i, taskQueue, resultStore));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("[ERROR] Workers did not finish in time. Forcing shutdown.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR] Main thread interrupted while waiting for workers: " + e.getMessage());
            executorService.shutdownNow();
        }

        // Resource management: write the file only after all worker threads finish.
        resultStore.writeResultsToFile("java_results.txt");

        List<ProcessedResult> finalResults = resultStore.getAllResults();
        finalResults.sort((left, right) -> Integer.compare(left.getTaskId(), right.getTaskId()));

        System.out.println();
        System.out.println("========================================");
        System.out.println("Final Results Summary");
        System.out.println("========================================");
        for (ProcessedResult result : finalResults) {
            System.out.println(result.toString());
        }
        System.out.println();
        System.out.println("========================================");
        System.out.println("Shutdown Complete");
        System.out.println("========================================");
        System.out.println("All tasks processed safely.");
        System.out.println("Results written to file.");
    }
}
