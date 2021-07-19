package com.bae.message.ipcam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.bae.message.R;
import com.bae.message.firebase.auth.BaseActivity;
import com.bae.message.sqlite.CameraDbHelper;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VideoActivity extends BaseActivity implements IVLCVout.Callback {
    public final static String TAG = "VideoActivity";
    public static final String RTSP_URL = "rtspurl";
    // rtsp://admin:woojin@@@192.168.0.133:554/Streaming/Channels/102
    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    private WeakReference<VideoActivity> mOwner;

    private ProgressDialog dialog;
    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);
    private String rtspUrl;
    private int restSeq;
    private String mTitle, mContents, mIp, mPort, mId, mPassword;
    private boolean playChk = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        showProgressBar();
        // Get URL
        Intent intent = getIntent();
        restSeq = intent.getExtras().getInt(RTSP_URL);
        // Db Selection
        CameraDbHelper dbHelper = CameraDbHelper.getInstance(this);
        String sql = "SELECT * FROM camera WHERE _ID = "+restSeq;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            mTitle = cursor.getString(cursor.getColumnIndex("title"));
            mContents = cursor.getString(cursor.getColumnIndex("contents"));
            mIp = cursor.getString(cursor.getColumnIndex("ip"));
            mPort = cursor.getString(cursor.getColumnIndex("port"));
            mId = cursor.getString(cursor.getColumnIndex("id"));
            mPassword = cursor.getString(cursor.getColumnIndex("pw"));
        }
        rtspUrl = "rtsp://" + mId + ":" + mPassword + "@" + mIp + ":" + mPort + "/Streaming/Channels/" + mTitle;
        Log.d("RTSP URL ::: ", rtspUrl);

        mSurface = (SurfaceView) findViewById(R.id.surface);
        holder = mSurface.getHolder();

        ArrayList<String> options = new ArrayList<String>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity
        options.add("--aout=opensles");
        options.add("--avcodec-codec=h264");
        options.add("--file-logging");
        options.add("--logfile=vlc-log.txt");

        libvlc = new LibVLC(getApplicationContext(), options);
        holder.setKeepScreenOn(true);

        // Create media player
        mMediaPlayer = new MediaPlayer(libvlc);


        // Media size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup.LayoutParams layoutParams = mSurface.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;

        // Set up video output
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.setVideoView(mSurface);
        vout.setWindowSize(layoutParams.width, layoutParams.height);
        vout.addCallback(this);
        vout.attachViews();

        Media m = new Media(libvlc, Uri.parse(rtspUrl));

        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();

        //test
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                //VideoActivity player = mOwner.get();
                switch(event.type) {
                    case MediaPlayer.Event.EndReached:
                        releasePlayer();
                        break;
                    case MediaPlayer.Event.Playing:
                        dialog = new ProgressDialog(VideoActivity.this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("잠시만 기다려주세요.");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        playChk = true; // 정상 시작됨
                        break;
                    case MediaPlayer.Event.Paused:
                    case MediaPlayer.Event.Stopped:
                        if(playChk) {
                            Log.d("STOPPED", "정상 종료 완료");
                        } else {
                            Log.d("STOPPED", "연결 오류");
                            onBackPressed();
                            DynamicToast.makeError(VideoActivity.this, "연결할 수 없습니다.").show();
                        }
                        break;
                    case MediaPlayer.Event.Buffering:
                        if(event.getBuffering() >= 100.0f) {
                            if (dialog != null){
                                dialog.dismiss();
                            }
                        } else {}
                        break;
                    default:
                        break;
                }
            }
        });
//
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }


}
