package components.listeners;

public interface TimeListener {
    void onTimeChanged(int remainingSeconds);
    default void onTimeOut() {}
}
