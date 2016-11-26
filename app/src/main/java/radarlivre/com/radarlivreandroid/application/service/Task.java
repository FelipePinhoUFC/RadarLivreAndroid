package radarlivre.com.radarlivreandroid.application.service;

public class Task {
    public enum TaskState {RUNNING, PAUSED, FINISHED};

    private static int SERIAL = 0;

    private TaskState state;
    private Runnable runnable;
    private long interval;

    public Task(Runnable runnable, long interval) {
        this.runnable = runnable;
        this.interval = interval;
        this.state = TaskState.RUNNING;
    }

    public void executeIfIsTime(long timeCount) {
        if(state == TaskState.RUNNING && timeCount % interval == 0) {
            this.runnable.run();
        }
    }

    public void resume() {
        this.state = TaskState.RUNNING;
    }

    public void pause() {
        this.state = TaskState.PAUSED;
    }

    public void finish() {
        this.state = TaskState.FINISHED;
    }

    public TaskState getState() {
        return state;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}


