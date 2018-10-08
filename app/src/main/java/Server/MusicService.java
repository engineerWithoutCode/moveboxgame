package Server;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.z.moveboxplus.R;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(MusicService.this,R.raw.bg_music);
            mediaPlayer.prepare();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.release();
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }
}
