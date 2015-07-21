package gheorghe.paul.barbu.callplanmonitor;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: setNotificationUri
        //TODO: get the SMSes and subtract them
        //TODO: limit by date

        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.DAY_OF_MONTH) < 14) {
            cal.add(Calendar.MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 14);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);

        final int TOTAL = 275;
        final int WARNING = 225;

        double sum = 0;

        TextView tv = (TextView) findViewById(R.id.textview);

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

        StringBuffer sb = new StringBuffer();

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

        sb.insert(0, sum + " minute nationale consumate din " + TOTAL + " de la data: " + cal.getTime().toString() + "\n");

        if(sum >= WARNING)
        {
            sb.insert(0, "ATENTIE! " + WARNING + " minute vorbite!\n\n");
        }

        tv.setText(sb.toString());

        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
