package prestozen.presto.watch.prestozen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import watch.nudge.phonegesturelibrary.AbstractPhoneGestureService;

/**
 * Created by Sumeet Thadani on 12/17/15.
 */
public class GestureCallbackService extends AbstractPhoneGestureService {


    private ArrayList<String> directions = null;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private int currDirIdx = 0;
    BroadcastReceiver directionsReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(tts == null) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS) {
                        ttsReady = true;
                    }
                }
            });
            tts.setLanguage(Locale.US);
        }
        if(directionsReceiver == null) {
            directionsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    directions = intent.getStringArrayListExtra("prestozen.presto.watch.prestozen.INSTRUCTIONS");
                    currDirIdx = 0;
                }
            };
            IntentFilter filter = new IntentFilter("prestozen.presto.watch.prestozen.DIRS_ACTION");
            LocalBroadcastManager.getInstance(this).registerReceiver(directionsReceiver, filter );
        }

        startGestureOnWatch(intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if(tts != null)
            tts.shutdown();
    }

    @Override
    public void onSnap() {
        onFlick();
    }

    @Override
    public void onFlick() {
        currDirIdx = 0;
    }

    @Override
    public void onTwist() {
        if(!ttsReady) {
            Toast.makeText(this, "Text To Speach not ready, please retry later", Toast.LENGTH_LONG).show();
            currDirIdx = 0;
            return;
        }
        if(tts.isSpeaking())
            return;
        String currentDirString = directions.get(currDirIdx);
        tts.speak(currentDirString, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onTiltX(float v) {
        throw new IllegalStateException("Unsubscribed Gesutre received");
    }

    @Override
    public void onTilt(float v, float v1, float v2) {
        throw new IllegalStateException("Unsubscribed Gesutre received");
    }

    @Override
    public void onWindowClosed() {
        throw new IllegalStateException("Windowing is disabled");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("GestureCallbackService is started and not bound");
    }
}
