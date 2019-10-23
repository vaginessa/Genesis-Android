package com.darkweb.genesissearchengine.helperManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class downloadFileService extends IntentService
{
    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    public downloadFileService() {
        super("DownloadSongService");
    }
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
                downloadFileService.context = callingClassContext;
                return new Intent(callingClassContext, downloadFileService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        startDownload(downloadPath, destinationPath);
    }
    private void startDownload(String downloadPath, String destinationPath) {
        String []fn = downloadPath.split("__");

        Uri uri = Uri.parse(fn[0]); // Path where you want to download file.
        DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fn[1]);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        manager.enqueue(req);
    }

}