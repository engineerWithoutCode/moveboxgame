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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import Server.MusicService;
import common.Constant;
import common.GameLevels;
import common.GameView;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView textView, last, next, restart;
    private int[][] currentLevel;
    private int level;
    private final int size = GameLevels.getLevelList().size();
    private GameLevels gameLevels;
    private String s;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        s = intent.getStringExtra("level");
        sp = getSharedPreferences("State", MODE_PRIVATE);
        editor = sp.edit();
        level = Integer.parseInt(s);
        gameLevels = new GameLevels();
        currentLevel = gameLevels.getCurrentLevel(level);
        gameView = (GameView) findViewById(R.id.game_view);
        textView = (TextView) findViewById(R.id.tv_level);
        last = (TextView) findViewById(R.id.last_level);
        next = (TextView) findViewById(R.id.next_level);
        restart = (TextView) findViewById(R.id.tv_restart);
        textView.setText("第" + (level + 1) + "关");

        checkLock();
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level > 0) {
                    level = level - 1;
                }
                textView.setText("第" + (level + 1) + "关");
                currentLevel = gameLevels.getCurrentLevel(level);
                gameView.postInvalidate();
                checkLock();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level < size - 1 && sp.getInt("levelState" + (level + 2), 3) != 3) {
                    level = level + 1;
                    textView.setText("第" + (level + 1) + "关");
                    currentLevel = gameLevels.getCurrentLevel(level);
                    gameView.postInvalidate();
                    checkLock();
                }
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLevel = gameLevels.getCurrentLevel(level);
                gameView.postInvalidate();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                View view = getLayoutInflater().inflate(R.layout.settinng_dialog, null);
                Switch bgMusic = (Switch) view.findViewById(R.id.bg_music_switch);
                Switch sound = (Switch) view.findViewById(R.id.sound_switch);
                bgMusic.setChecked(Constant.MUSIC_BOOLEAN);
                sound.setChecked(Constant.SOUND_BOOLEAN);
                builder.setView(view);
                builder.show();
                bgMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            Constant.MUSIC_BOOLEAN = true;
                            Intent musicStart = new Intent(getApplicationContext(), MusicService.class);
                            startService(musicStart);
                        } else {
                            Constant.MUSIC_BOOLEAN = false;
                            Intent musicStart = new Intent(getApplicationContext(), MusicService.class);
                            stopService(musicStart);
                        }
                    }
                });
                sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            Constant.SOUND_BOOLEAN = true;
                        } else {
                            Constant.SOUND_BOOLEAN = false;
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkLock() {
        if (level == 0) {
            last.setEnabled(false);
        }else{
            last.setEnabled(true);
        }
        if (level < size - 1 && sp.getInt("levelState" + (level + 2), 3) == 3) {
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
        }
    }

    public void setLevelState() {
        editor.putInt("levelState" + (level + 2), 2);
        editor.putInt("levelState" + (level + 1), 1);
        editor.commit();
        checkLock();
    }

    public int[][] getCurrentLevel() {
        return currentLevel;
    }
}
