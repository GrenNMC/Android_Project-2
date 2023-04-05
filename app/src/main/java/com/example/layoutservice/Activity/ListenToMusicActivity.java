package com.example.layoutservice.Activity;

import static android.view.animation.Animation.INFINITE;
import static com.example.layoutservice.MyService.ACTION_CLEAR;
import static com.example.layoutservice.MyService.ACTION_NEXT;
import static com.example.layoutservice.MyService.ACTION_PAUSE;
import static com.example.layoutservice.MyService.ACTION_PRE;
import static com.example.layoutservice.MyService.ACTION_RESUME;
import static com.example.layoutservice.MyService.ACTION_START;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.layoutservice.Models.MusicFiles;
import com.example.layoutservice.MyService;
import com.example.layoutservice.R;
import com.example.layoutservice.Receiver.BroadcastReceiver;

import java.util.ArrayList;
import java.util.Random;

public class ListenToMusicActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    static Uri uri;
    private static final int REPEAT_ONE = 1;
    private static final int REPEAT_ALL = 2;
    private int repeat = REPEAT_ALL;
    private boolean shuffle = false;
    Button btnPlay, btnNext, btnPre, btnList, btnBack, btnRepeat, btnShuffle;
    TextView tvName, tvSinger, tvStart, tvStop;
    ArrayList<MusicFiles> listSong;
    private MusicFiles mSong;
    private int positionSelect;
    SeekBar seekMusic, seekVolume;
    private int actionService, mCurrentPosition, durationTotal;
    private boolean isPlaying;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageView imgViewMusic;
    private ObjectAnimator animator;
    private AudioManager audioManager;
    private Handler handler = new Handler();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null){
                return;
            }
            actionService = bundle.getInt("action_music");
            handleActionFromService(actionService);

        }
    };

    private void handleActionFromService(int actionService) {
        switch (actionService)
        {
            case ACTION_PAUSE:
                mediaPlayer.pause();
                animator.pause();
                isPlaying = false;
                btnPlay.setBackgroundResource(R.drawable.ic_play_main);
                break;
            case ACTION_CLEAR:
                mediaPlayer.stop();
                break;

            case ACTION_RESUME:
                mediaPlayer.start();
                animator.resume();
                isPlaying = true;
                btnPlay.setBackgroundResource(R.drawable.ic_pause_main);
                break;

            case ACTION_NEXT:
                btnNext.performClick();
                break;

            case ACTION_PRE:
                btnPre.performClick();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_play_music);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("send_data_to_activity"));

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        btnPlay = findViewById(R.id.btn_play_main);
        btnNext = findViewById(R.id.btn_next_main);
        btnPre = findViewById(R.id.btn_previous_main);
        btnList = findViewById(R.id.btn_list);
        btnBack = findViewById(R.id.btn_back_playing);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnShuffle = findViewById(R.id.btn_shuffle);

        tvName = findViewById(R.id.title_song);
        tvSinger = findViewById(R.id.single);
        tvStart = findViewById(R.id.tv_time);
        tvStop = findViewById(R.id.tv_time1);

        seekMusic = findViewById(R.id.seekbar);
        seekVolume = findViewById(R.id.seekbar_volume);
        imgViewMusic = findViewById(R.id.img_music);

        getIntentMethod();
        startMusic(mSong);
        RotationAnimation(imgViewMusic);
        animator.start();

        tvName.setText(mSong.getTitle());
        tvSinger.setText(mSong.getArtist());

        clickStartService(ACTION_START);

        btnPlay.setBackgroundResource(R.drawable.ic_pause_main);

        durationTotal = mediaPlayer.getDuration() / 1000;
        tvStop.setText(formatTime(durationTotal));

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekMusic.setMax(mediaPlayer.getDuration() / 1000);
                mediaPlayer.start();
            }
        });

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setMax(maxVolume);
        seekVolume.setProgress(currentVolume);

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ListenToMusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekMusic.setProgress(mCurrentPosition);
                    tvStart.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 500);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause
                if(isPlaying == true){
                    mediaPlayer.pause();
                    clickStartService(ACTION_PAUSE);
                    isPlaying = false;
                    animator.pause();
                    btnPlay.setBackgroundResource(R.drawable.ic_play_main);
                }
                // resume
                else {
                    mediaPlayer.start();
                    clickStartService(ACTION_RESUME);
                    btnPlay.setBackgroundResource(R.drawable.ic_pause_main);
                    animator.resume();
                    isPlaying = true;
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMusic();
                animator.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause_main);
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreMusic();
                animator.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause_main);
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListenToMusicActivity.this, ListSongPlayingActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("listSong_key", listSong);
                i.putExtras(b);
                startActivity(i);

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (repeat){
                    case REPEAT_ALL:
                        repeat = REPEAT_ONE;
                        btnRepeat.setBackgroundResource(R.drawable.repeat_one);
                        break;

                    case REPEAT_ONE:
                        repeat = REPEAT_ALL;
                        btnRepeat.setBackgroundResource(R.drawable.repeat_press);
                        break;
                }
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle == false){
                    shuffle = true;
                    btnShuffle.setBackgroundResource(R.drawable.shuffle_press);
                }
                else {
                    shuffle = false;
                    btnShuffle.setBackgroundResource(R.drawable.shuffle);
                }
            }
        });
    }

    private void playNextMusic() {
        if (shuffle == true && repeat == REPEAT_ALL) {
            positionSelect= getRandom(listSong.size()-1);
        }
        else if (shuffle == false && repeat == REPEAT_ALL){
            positionSelect = (positionSelect+1) % listSong.size();
        }

        mSong = listSong.get(positionSelect);
        startMusic(mSong);
        clickStartService(ACTION_START);
        tvName.setText(mSong.getTitle());
        tvSinger.setText(mSong.getArtist());

        int durationTotal = mediaPlayer.getDuration() / 1000;
        tvStop.setText(formatTime(durationTotal));

        mediaPlayer.setOnCompletionListener(this);
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void playPreMusic(){
        if (shuffle == true && repeat == REPEAT_ALL) {
            positionSelect= getRandom(listSong.size()-1);
        }
        else if (shuffle == false && repeat == REPEAT_ALL){
            positionSelect = ((positionSelect-1)<0) ? (listSong.size()-1):(positionSelect-1);
        }
        mSong = listSong.get(positionSelect);
        startMusic(mSong);
        clickStartService(ACTION_START);
        tvName.setText(mSong.getTitle());
        tvSinger.setText(mSong.getArtist());
        int durationTotal = mediaPlayer.getDuration() / 1000;
        tvStop.setText(formatTime(durationTotal));

        mediaPlayer.setOnCompletionListener(this);
    }
    private void getIntentMethod(){
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {

            positionSelect = bundle.getInt("position_key");
            listSong = (ArrayList<MusicFiles>) bundle.getSerializable("listSong_key");

            mSong = (MusicFiles) bundle.get("object_song");
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            uri = Uri.parse(mSong.getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        }
    }
    private void clickStartService(int actionMusic) {

        Intent intent = new Intent(this, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", mSong);
        intent.putExtra("action_music_to_service", actionMusic);
        intent.putExtras(bundle);
        startService(intent);

    }
    private void startMusic(MusicFiles mSong) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        uri  = Uri.parse(mSong.getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

        mediaPlayer.start();
        isPlaying = true;
    }
    private String formatTime(int mCurrentPosition){
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        }
        else{
            return totalOut;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextMusic();
        mediaPlayer.setOnCompletionListener(this);
    }
    private void RotationAnimation(View view){

        animator  = ObjectAnimator.ofFloat(imgViewMusic, "rotation", 0f, 360f);
        animator.setDuration(10000);
        animator.setRepeatCount(INFINITE);

    }
}


