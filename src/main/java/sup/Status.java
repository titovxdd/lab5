package sup;

public class Status {
    private Pair<Boolean, String> status;

    public Status(boolean success, String message) {
        this.status = new Pair<>(success, message);
    }

    public boolean isSuccess() {
        return status.getFirst();
    }

    public String getMessage() {
        return status.getSecond();
    }
}
