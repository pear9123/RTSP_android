package com.bae.message.ipcam;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;

class MyPlayerListener implements MediaPlayer.EventListener {

    private static String TAG = "PlayerListener";
    private WeakReference<VideoActivity> mOwner;


    public MyPlayerListener(VideoActivity owner) {
        mOwner = new WeakReference<VideoActivity>(owner);
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        VideoActivity player = mOwner.get();

        switch(event.type) {
            case MediaPlayer.Event.EndReached:
                Log.d(TAG, "MediaPlayerEndReached");
                player.releasePlayer();
                break;
            case MediaPlayer.Event.Playing:
            case MediaPlayer.Event.Paused:
            case MediaPlayer.Event.Stopped:
            case MediaPlayer.Event.Buffering:
                Log.d("LOGDDING ::::::", String.valueOf(Math.floor(event.getBuffering())));
            default:
                break;
        }
    }
}
