package gheorghe.paul.barbu.callplanmonitor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    static final int TOTAL = 275;
    static final int WARNING = 225;
    static final String WARNING_TEXT = "ATENTIE! " + WARNING + " minute vorbite!";
    static final String NOTIF_TITLE = "Minute nationale!";
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: setNotificationUri

        if(sb.length() == 0) {
            inspectCallData();
        }

        TextView tv = (TextView) findViewById(R.id.textview);

        tv.setText(sb.toString());

    }

    protected void showNotif()
    {
        Intent openMain = new Intent(this, MainActivity.class);
        TaskStackBuilder backStack = TaskStackBuilder.create(this);
        backStack.addParentStack(MainActivity.class);
        backStack.addNextIntent(openMain);

        PendingIntent notifIntent = backStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(NOTIF_TITLE)
                .setContentText(WARNING_TEXT)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setContentIntent(notifIntent);

        NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifManager.notify(16, notifBuilder.build());
    }

    protected void inspectCallData()
    {
        sb.delete(0, sb.length());
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.DAY_OF_MONTH) < 14) {
            cal.add(Calendar.MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 14);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);

        double sum = 0;
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = getContentResolver().query(allCalls,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE},
                CallLog.Calls.NUMBER + " NOT LIKE ? AND " + CallLog.Calls.NUMBER + " NOT LIKE ? AND " +
                        CallLog.Calls.NUMBER + " NOT LIKE ? AND " + CallLog.Calls.NUMBER + " NOT LIKE ? AND " +
                        CallLog.Calls.NUMBER + " NOT LIKE ? AND " + CallLog.Calls.DATE + " >= ?",
                new String[]{"072%", "073%", "+4073%", "+4072%", "*%", cal.getTimeInMillis() + ""},
                CallLog.Calls.DATE + " DESC");

        int durationIndex = c.getColumnIndex(CallLog.Calls.DURATION);
        int typeIndex = c.getColumnIndex(CallLog.Calls.TYPE);
        int numberIndex= c.getColumnIndex(CallLog.Calls.NUMBER);

        while(c.moveToNext())
        {
            String number = c.getString(numberIndex);
            long duration = c.getLong(durationIndex);
            int type = Integer.parseInt(c.getString(typeIndex));
            String typeStr = "";

            if(type == CallLog.Calls.OUTGOING_TYPE) {
                double realDuration = 0;
                if(duration > 60) {
                    realDuration = Math.ceil(duration / 60);
                }
                else { // the first several seconds of the first minute are billed as one minute
                    realDuration = 1;
                }

                sum += realDuration;
                sb.append("\n " + number + ": " + realDuration);
            }
        }

        c.close();

        sb.insert(0, sum + " minute nationale consumate din " + TOTAL + " de la data: " + cal.getTime().toString() + "\n");

        if(sum >= WARNING)
        {
            sb.insert(0, WARNING_TEXT + "\n\n");

            showNotif();
        }
    }
}
