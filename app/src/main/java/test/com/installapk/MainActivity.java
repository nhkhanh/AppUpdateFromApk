package test.com.installapk;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DownloadManager mgr=null;
    private long lastDownload=-1L;

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //findViewById(R.id.start).setEnabled(true);

            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = mgr.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // process download
                    //title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    // get other required data by changing the constant passed to getColumnIndex
                    Toast.makeText(ctxt, "Download completed!", Toast.LENGTH_LONG).show();
                    //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.apk");
                    //file.setReadable(true, false);
                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))),
                                    "application/vnd.android.package-archive");
                    startActivity(promptInstall);
                }
            }


        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        TextView versionTextView = (TextView)findViewById(R.id.version);
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            versionTextView.setText(                    "PackageName = " + info.packageName + "\nVersionCode = "
                            + info.versionCode + "\nVersionName = "
                            + info.versionName + "\nPermissions = " + info.permissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
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

    public void onUpdateButtonClicked(View view){
        Uri uri=Uri.parse("https://drive.google.com/uc?export=download&id=0BxFde2ydXZ5kd3NZNF9OemNvWWs");

        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        lastDownload=
                mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Demo")
                        .setDescription("Something useful. No, really.")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                "test.apk"));
    }
}
