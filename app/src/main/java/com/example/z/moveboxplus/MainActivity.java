package com.example.z.moveboxplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


import Server.MusicService;
import common.Constant;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private Boolean isFirst;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnPlay = (Button) findViewById(R.id.btn1);
        Button btn = (Button) findViewById(R.id.btn2);
        Button btnQuit = (Button) findViewById(R.id.btn3);

        //读写SharedPreferences
        sp = getSharedPreferences("State",MODE_PRIVATE);
        spEditor = sp.edit();
        isFirst = sp.getBoolean("isFirst",true);
        if(isFirst){
            spEditor.putBoolean("isFirst",false);
            spEditor.putInt("levelState1",2);
            spEditor.putInt("levelState2",3);
            spEditor.putInt("levelState3",3);
            spEditor.putInt("levelState4",3);
            spEditor.putInt("levelState5",3);
            spEditor.putInt("levelState6",3);
            spEditor.putInt("levelState7",3);
            spEditor.putInt("levelState8",3);
            spEditor.commit();
        }

        //背景音乐
        if (Constant.MUSIC_BOOLEAN){
            Intent musicStart = new Intent(getApplicationContext(),MusicService.class);
            startService(musicStart);
        }

        btnPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,ChooseActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,IntroActivity.class);
                startActivity(intent);
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
                System.exit(0);
            }
        });
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
