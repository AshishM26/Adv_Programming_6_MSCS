public final class Task {
    private final int taskId;
    private final int payload;
    private final boolean poisonPill;

    public Task(int taskId, int payload) {
        this(taskId, payload, false);
    }

    private Task(int taskId, int payload, boolean poisonPill) {
        this.taskId = taskId;
        this.payload = payload;
        this.poisonPill = poisonPill;
    }

    public static Task poisonPill() {
        return new Task(-1, 0, true);
    }

    public int getTaskId() {
        return taskId;
    }

    public int getPayload() {
        return payload;
    }

    public boolean isPoisonPill() {
        return poisonPill;
    }
}
