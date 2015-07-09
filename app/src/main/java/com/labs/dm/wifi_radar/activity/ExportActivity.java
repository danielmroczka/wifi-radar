package com.labs.dm.wifi_radar.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.labs.dm.wifi_radar.R;
import com.labs.dm.wifi_radar.db.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ExportActivity extends Activity implements View.OnClickListener {

    private final String FILENAME = DBManager.DB_NAME;
    private final int CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = FILENAME;
                File currentDB = getDatabasePath(FILENAME);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        exportDB();
        send();
    }

    private void send() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        File sd = Environment.getExternalStorageDirectory();

        File attachment = new File(sd, FILENAME);
        Uri uri = Uri.fromFile(attachment);

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("application/octet-stream");
        startActivityForResult(Intent.createChooser(shareIntent, "Send to..."), CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE) {
            File file = new File(Environment.getExternalStorageState() + "/" + FILENAME);
            file.delete();
        }
    }
}
