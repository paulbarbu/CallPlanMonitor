package gheorghe.paul.barbu.callplanmonitor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.IntentFilter;
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
    CallPlanMonitor cpm = new CallPlanMonitor(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: setNotificationUri

        cpm.inspectCallData();

        TextView tv = (TextView) findViewById(R.id.textview);

        tv.setText(cpm.toString());
    }
}
