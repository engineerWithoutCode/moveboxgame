package com.example.z.moveboxplus;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import Server.MusicService;
import common.Constant;
import common.GameLevels;
import common.Level;
import common.LevelAdapter;

public class ChooseActivity extends AppCompatActivity {
    private GridView gridView;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private List<Level> levelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        gridView = (GridView)findViewById(R.id.gridView_choose);

        //设置level
        sp = getSharedPreferences("State",MODE_PRIVATE);
        editor = sp.edit();

        refresh();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(sp.getInt("levelState"+(i+1),3) < 3){
                    Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                    intent.putExtra("level",i+"");
                    startActivity(intent);
                }else{
                    return;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
        levelList.clear();
        for (int i=0;i < GameLevels.getLevelList().size();i++){
            Level level = new Level();
            level.setLevelId(GameLevels.getLevelList().get(i));
            level.setLevelState(sp.getInt("levelState"+(i+1),3));
            levelList.add(level);
        }
        LevelAdapter levelAdapter = new LevelAdapter(this, R.layout.level_back_color,levelList);
        gridView.setAdapter(levelAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActivity.this);
                View view = getLayoutInflater().inflate(R.layout.settinng_dialog,null);
                Switch bgMusic = (Switch)view.findViewById(R.id.bg_music_switch);
                Switch sound = (Switch)view.findViewById(R.id.sound_switch);
                bgMusic.setChecked(Constant.MUSIC_BOOLEAN);
                sound.setChecked(Constant.SOUND_BOOLEAN);
                builder.setView(view);
                builder.show();
                bgMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Constant.MUSIC_BOOLEAN = true;
                            Intent musicStart = new Intent(getApplicationContext(), MusicService.class);
                            startService(musicStart);
                        }else{
                            Constant.MUSIC_BOOLEAN = false;
                            Intent musicStart = new Intent(getApplicationContext(), MusicService.class);
                            stopService(musicStart);
                        }
                    }
                });
                sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            Constant.SOUND_BOOLEAN = true;
                        }else{
                            Constant.SOUND_BOOLEAN = false;
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
