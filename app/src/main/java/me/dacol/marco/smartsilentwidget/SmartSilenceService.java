package me.dacol.marco.smartsilentwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

public class SmartSilenceService extends Service{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        silencePhone();
        RemoteViews views = buildViews();
        updateWidget(views);

        stopSelf();
        return START_NOT_STICKY;
    }

    // this will just update the widget and reset a pending intent so that when the button is
    // clicked again it will fire up again the Intent and start this service
    private void updateWidget(RemoteViews views) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName widget = new ComponentName(this, SmartSilentWidget.class);
        appWidgetManager.updateAppWidget(widget, views);
    }

    private RemoteViews buildViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.smart_silent_widget);
        PendingIntent silenceIntent = PendingIntent.getService(this
                , 0
                , new Intent(this, SmartSilenceService.class)
                , 0);
        remoteViews.setOnClickPendingIntent(R.id.silence_button, silenceIntent);
        return remoteViews;
    }

    private void silencePhone() {
        setPriorityAndSilence();

        // this is because of the first call set the Priority mode in Lollypop
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    setPriorityAndSilence();
                }
            }).run();
        }
    }

    private void setPriorityAndSilence() {
        AudioManager audioManager;
        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
