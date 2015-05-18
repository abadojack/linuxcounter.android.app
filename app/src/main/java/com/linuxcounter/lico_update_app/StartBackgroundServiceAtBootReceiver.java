package com.linuxcounter.lico_update_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by alex on 17.05.15.
 */
public class StartBackgroundServiceAtBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        System.out.println("test 1");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            System.out.println("test 2");
            Intent i = new Intent(context, UpdateInBackgroundService.class);
            context.startService(i);
        }
    }
}
