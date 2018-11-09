package com.example.moon.onlineradiostreamigdemo;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.kimjihyok.ripplesoundplayer.RippleVisualizerView;
import info.kimjihyok.ripplesoundplayer.SoundPlayerView;
import info.kimjihyok.ripplesoundplayer.renderer.ColorfulBarRenderer;
import info.kimjihyok.ripplesoundplayer.util.PaintUtil;

public class RadioOptions extends AppCompatActivity {

    ListView stationList;
    ImageButton ibtn_play, ibtn_next,ibtn_previous,ibtn_menu;
    MediaPlayer radioPlayer;
    int currentPosition;
    TextView stationName;
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;
    String link,name;
    int image_id;
    GoogleProgressBar progressBar;
    int playClicked;
    CircleImageView circleImageView;
    ImageView radioFrequency;
    Runnable runnable;
    Thread thread;


    int[] backImage = {
            R.drawable.rf1,
            R.drawable.rf2,
            R.drawable.rf3,
            R.drawable.rf2,
            R.drawable.rf1
    };


    String[] stationsLinks = {
            "http://103.253.47.173:8000/;stream/1",
            "http://ample-zeno-19.radiojar.com/8w0533k6vewtv?rj-ttl=4&amp;rj-token=AAABZtaA5LEW6ssfUd_YRskxMManG4IoTsZcZuh2Q6WzzYh99fl2FA",
            "http://stream.radioreklama.bg:80/radio1rock128"
    };


    int[]  images = {
            R.drawable.r71,
            R.drawable.banbater,
            R.drawable.r_unknown
    };

    String[] stations_name = {
            "Radio Ekattor",
            "Bangladesh Bater",
            "Unknown Station"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_options);
        init_views();
        init_variables();
        init_functions();
        init_listeners();

    }

    private void init_views() {
        stationList = (ListView)findViewById(R.id.stationsList);
        ibtn_play = (ImageButton)findViewById(R.id.playOrPause);
        ibtn_next = (ImageButton)findViewById(R.id.next);
        ibtn_previous = (ImageButton)findViewById(R.id.previous);
        ibtn_menu = (ImageButton)findViewById(R.id.menu);
        stationName = (TextView)findViewById(R.id.tv_stationName);
        relativeLayout = (RelativeLayout)findViewById(R.id.playZone);
        linearLayout = (LinearLayout)findViewById(R.id.linear);
        progressBar = (GoogleProgressBar) findViewById(R.id.google_progress);
        circleImageView = (CircleImageView)findViewById(R.id.radio_image);
        radioFrequency = (ImageView)findViewById(R.id.image_fre);

    }

    private void init_variables() {
       radioPlayer = new MediaPlayer();



       currentPosition = 0;
       playClicked = 0;
    }

    private void init_functions() {
        stationList.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        radioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        runnable = new Runnable() {
            @Override
            public void run() {
                for(int i =0;i<backImage.length;i++){

                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            radioFrequency.setImageResource(backImage[finalI]);
                        }
                    });

                    if(i==backImage.length-1){
                        i = 0;
                    }
                    SystemClock.sleep(1000);
                }

            }
        };



    }

    private void init_listeners() {

        ibtn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stationList.setVisibility(View.VISIBLE);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stations_name);
                stationList.setAdapter(arrayAdapter);
            }
        });

        ibtn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playClicked % 2 == 0){
                    if (playClicked > 0) {
                        thread.start();
                        radioPlayer.start();
                        if (radioPlayer.isPlaying()) {
                            ibtn_play.setImageResource(R.drawable.ic_pause_circle_filled_green_24dp);
                            // playClicked = 0;
                        }

                    } else {
                        main_functionality();
                    }
            }
               else {
                   radioPlayer.pause();
                   ibtn_play.setImageResource(R.drawable.ic_play_circle_filled_green_24dp);
                   ibtn_play.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
               playClicked++;
            }
        });


        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                main_functionality();
                stationList.setVisibility(View.INVISIBLE);
            }
        });


        ibtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition++;
                if(currentPosition>=stationsLinks.length)
                    currentPosition = 0;

                main_functionality();
            }
        });
        ibtn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition--;
                if(currentPosition < 0)
                    currentPosition = stationsLinks.length-1;
            }
        });
    }


    public void main_functionality(){
        settingButtons();
       // settingThread();
        progressBar.setVisibility(View.VISIBLE);
        link = stationsLinks[currentPosition];
        image_id = images[currentPosition];
        name = stations_name[currentPosition];

        if(radioPlayer!=null && radioPlayer.isPlaying()){
            radioPlayer.reset();
        }

        new PlayingRadio().execute(link);
    }

    private void settingThread() {
        try {
            if(thread!=null) {
                thread.join();
                thread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void settingButtons() {
        ibtn_play.setClickable(false);
        ibtn_previous.setClickable(false);
        ibtn_next.setClickable(false);
        ibtn_play.setImageResource(R.drawable.ic_pause_circle_filled_green_24dp);
        ibtn_play.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }


    private class PlayingRadio extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                radioPlayer.setDataSource(strings[0]);
                radioPlayer.prepare();
                return true;
                } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            againSetting();
            if(aBoolean){
                radioPlayer.start();
                stationName.setText(name);
                circleImageView.setImageResource(image_id);
                ibtn_play.setClickable(true);
                if(thread==null) {
                    thread = new Thread(runnable);
                    thread.start();
                }

            }else{
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "No Internet", Snackbar.LENGTH_LONG);
                ibtn_play.setImageResource(R.drawable.ic_play_circle_filled_green_24dp);
                playClicked = 0;

                snackbar.show();
            }
        }

        private void againSetting() {
            progressBar.setVisibility(View.INVISIBLE);
            ibtn_play.setClickable(true);
            ibtn_next.setClickable(true);
            ibtn_previous.setClickable(true);

        }
    }
}
