import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class SharedTaskQueue {
    /*
     * LinkedBlockingQueue is thread-safe, so workers can safely block here
     * without us writing low-level synchronized wait/notify logic.
     * That keeps the example beginner-friendly and prevents race conditions.
     */
    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    public void addTask(Task task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR] Queue add interrupted: " + e.getMessage());
        }
    }

    public Task getTask() throws InterruptedException {
        return queue.take();
    }
}
