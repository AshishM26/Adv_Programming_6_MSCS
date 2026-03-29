public final class Worker implements Runnable {
    private final String workerName;
    private final SharedTaskQueue taskQueue;
    private final ResultStore resultStore;

    public Worker(String workerName, SharedTaskQueue taskQueue, ResultStore resultStore) {
        this.workerName = workerName;
        this.taskQueue = taskQueue;
        this.resultStore = resultStore;
    }

    @Override
    public void run() {
        System.out.println("[START] " + workerName + " started");

        while (true) {
            Task task;
            try {
                task = taskQueue.getTask();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[ERROR] " + workerName + " interrupted while waiting for a task: " + e.getMessage());
                break;
            }

            if (task.isPoisonPill()) {
                System.out.println("[EXIT] " + workerName + " received shutdown signal");
                break;
            }

            System.out.println("[PROCESSING] " + workerName + " picked Task " + task.getTaskId() + " (payload=" + task.getPayload() + ")");

            try {
                ProcessedResult result = processTask(task);
                resultStore.addResult(result);
                if (result.isSuccess()) {
                    System.out.println("[COMPLETED] " + workerName + " finished Task " + task.getTaskId() + " -> result=" + result.getResultValue());
                } else {
                    System.out.println("[ERROR] " + workerName + " failed Task " + task.getTaskId() + " (payload=" + task.getPayload() + "): " + result.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String message = "interrupted during processing";
                resultStore.addResult(new ProcessedResult(task.getTaskId(), task.getPayload(), workerName, 0, false, message));
                System.out.println("[ERROR] " + workerName + " " + message + " for Task " + task.getTaskId());
            } catch (IllegalArgumentException e) {
                resultStore.addResult(new ProcessedResult(task.getTaskId(), task.getPayload(), workerName, 0, false, e.getMessage()));
                System.out.println("[ERROR] " + workerName + " failed Task " + task.getTaskId() + " (payload=" + task.getPayload() + "): " + e.getMessage());
            }
        }

        System.out.println("[STOP] " + workerName + " exited cleanly");
    }

    private ProcessedResult processTask(Task task) throws InterruptedException {
        /*
         * The sleep is a simple simulation of real work. It also makes the
         * concurrency visible when the console output is captured.
         */
        Thread.sleep(150L + (task.getTaskId() % 3) * 75L);

        if (task.getPayload() < 0) {
            throw new IllegalArgumentException("negative payload is invalid");
        }

        int resultValue = task.getPayload() * task.getPayload();
        return new ProcessedResult(
            task.getTaskId(),
            task.getPayload(),
            workerName,
            resultValue,
            true,
            "Processed successfully"
        );
    }
}
