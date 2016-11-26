package radarlivre.com.radarlivreandroid.application.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by felipe on 20/08/16.
 */
public class UpdaterService extends IntentService {

    public static final String SERVICE_NAME = "UPDATER_SERVICE";

    private UpdaterScheduler updaterScheduler = new UpdaterScheduler();

    public UpdaterService() {
        super(SERVICE_NAME);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UpdaterServiceBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updaterScheduler.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        updaterScheduler.stop();
        return super.onUnbind(intent);
    }

    public UpdaterScheduler getUpdaterScheduler() {
        return updaterScheduler;
    }

    public class UpdaterServiceBinder extends Binder {
        public UpdaterService getService() {
            return UpdaterService.this;
        }
    }

}
