package com.example.layoutservice.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layoutservice.Adapter.SongPlayingAdapter;
import com.example.layoutservice.R;
import com.example.layoutservice.Song;

import java.util.ArrayList;

public class ListSongPlayingActivity extends AppCompatActivity {

    private ArrayList<Song> listSong;
    private ListView listView;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_music_playing);

        listView = findViewById(R.id.list_view_music_playing);
        btnBack = findViewById(R.id.btn_back);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            listSong = (ArrayList<Song>) bundle.getSerializable("listSong_key");
        }

        SongPlayingAdapter adapter = new SongPlayingAdapter(this, R.layout.layout_listview_playing, listSong);
        listView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
