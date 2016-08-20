package radarlivre.com.radarlivreandroid.application.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by felipe on 20/08/16.
 */
public class UpdaterService extends IntentService {

    public UpdaterService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {

        }
    }
}
