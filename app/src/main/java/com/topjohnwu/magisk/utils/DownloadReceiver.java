package com.topjohnwu.magisk.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.topjohnwu.magisk.R;

import java.io.File;

/**
 * Created by topjohnwu on 2016/8/27.
 */
public abstract class DownloadReceiver extends BroadcastReceiver{
    public Context context;
    DownloadManager downloadManager;
    long downloadID;

    public DownloadReceiver(long downloadID) {
        this.downloadID = downloadID;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String action = intent.getAction();
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadID);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = c.getInt(columnIndex);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        File file = new File(Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                        task(file);
                        break;
                    default:
                        Toast.makeText(context, R.string.error_download_file, Toast.LENGTH_LONG).show();
                        break;
                }
                context.unregisterReceiver(this);
            }
        }
    }

    public abstract void task(File file);
}