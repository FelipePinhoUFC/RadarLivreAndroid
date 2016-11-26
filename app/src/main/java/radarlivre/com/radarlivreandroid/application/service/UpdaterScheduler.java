package radarlivre.com.radarlivreandroid.application.service;

import java.util.List;
import java.util.Vector;

public class UpdaterScheduler {
    private boolean running = false;
    private List<Task> tasks = new Vector<>();

    public void schedule(Task task) {
        this.tasks.add(task);
    }

    public void start() {
        running = true;
        long timeCount = 0;
        while(running) {

            for(Task t: tasks) {
                if(t.getState() == Task.TaskState.FINISHED)
                    tasks.remove(t);
                t.executeIfIsTime(timeCount);
            }

            timeCount++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void stop() {
        this.running = false;
    }
}