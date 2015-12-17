package prestozen.presto.watch.prestozen;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import watch.nudge.phonegesturelibrary.AbstractPhoneGestureService;

/**
 * Created by Sumeet Thadani on 12/17/15.
 */
public class GestureCallbackService extends AbstractPhoneGestureService {

    @Override
    public void onSnap() {
        //TODO: remove snap from the subscriptions, we don't really need it.
    }

    @Override
    public void onFlick() {
        //TODO: Resume the directions per normal
    }

    @Override
    public void onTwist() {
        //Read out the next direction
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
