package com.degifetise.madguzoethiopiamobapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "com.degifetise.madguzoethiopiamobapp.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.degifetise.madguzoethiopiamobapp.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.degifetise.madguzoethiopiamobapp.ACTION_STOP";
    private static final String CHANNEL_ID = "MusicChannel";
    private static final int NOTIFICATION_ID = 1;

    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "MusicService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    playMusic();
                    break;
                case ACTION_PAUSE:
                    pauseMusic();
                    break;
                case ACTION_STOP:
                    stopMusic();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void playMusic() {
        // Mock MediaPlayer logic for Masenqo track
        if (mediaPlayer == null) {
            // mediaPlayer = MediaPlayer.create(this, R.raw.masenqo_track);
            // mediaPlayer.setLooping(true);
        }
        
        // Start foreground service with notification
        Notification notification = createNotification(getString(R.string.music_playing));
        startForeground(NOTIFICATION_ID, notification);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, createNotification(getString(R.string.music_paused)));
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        stopSelf();
    }

    private Notification createNotification(String contentText) {
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, MusicService.class).setAction(ACTION_PLAY);
        PendingIntent pPlayIntent = PendingIntent.getService(this, 1, playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MusicService.class).setAction(ACTION_PAUSE);
        PendingIntent pPauseIntent = PendingIntent.getService(this, 2, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, MusicService.class).setAction(ACTION_STOP);
        PendingIntent pStopIntent = PendingIntent.getService(this, 3, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_play, getString(R.string.play), pPlayIntent)
                .addAction(android.R.drawable.ic_media_pause, getString(R.string.pause), pPauseIntent)
                .addAction(android.R.drawable.ic_delete, getString(R.string.stop), pStopIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Music Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.release();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}