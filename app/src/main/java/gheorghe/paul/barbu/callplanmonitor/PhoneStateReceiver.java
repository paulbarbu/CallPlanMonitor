package gheorghe.paul.barbu.callplanmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String TAG = PhoneStateReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
            CallPlanMonitor cpm = new CallPlanMonitor(context);
            cpm.inspectCallData();
        }
    }
}
