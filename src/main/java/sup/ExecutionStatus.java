package sup;

public class ExecutionStatus {
    private Pair<Boolean, String> status;

    public ExecutionStatus(boolean success, String message) {
        this.status = new Pair<>(success, message);
    }

    public boolean isSuccess() {
        return status.getFirst();
    }

    public String getMessage() {
        return status.getSecond();
    }
}
