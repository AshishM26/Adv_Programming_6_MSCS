public final class ProcessedResult {
    private final int taskId;
    private final int payload;
    private final String workerName;
    private final int resultValue;
    private final boolean success;
    private final String message;

    public ProcessedResult(int taskId, int payload, String workerName, int resultValue, boolean success, String message) {
        this.taskId = taskId;
        this.payload = payload;
        this.workerName = workerName;
        this.resultValue = resultValue;
        this.success = success;
        this.message = message;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getPayload() {
        return payload;
    }

    public String getWorkerName() {
        return workerName;
    }

    public int getResultValue() {
        return resultValue;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format(
            "Task %d | Payload=%d | Worker=%s | Result=%d | Success=%s | Message=%s",
            taskId,
            payload,
            workerName,
            resultValue,
            success,
            message
        );
    }
}
