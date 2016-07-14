package com.codiodes.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Salman Khan on 14/07/16.
 */
public class DownloadFileTask extends AsyncTask<String, Integer, String> {

    public static final int BUFFER_SIZE = 8192;
    Context mContext;
    ProgressDialog mPDialog;

    public DownloadFileTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        initProgressDialog();
        mPDialog.show();
    }

    private void initProgressDialog() {
        mPDialog = new ProgressDialog(mContext);
        mPDialog.setMessage("Downloading file...");
        mPDialog.setIndeterminate(false);
        mPDialog.setMax(100);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPDialog.setCancelable(false);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url.toString());
            String fileName = URLUtil.guessFileName(url.toString(), null, fileExtension);

            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();
            InputStream inputStream = new BufferedInputStream(url.openStream(), BUFFER_SIZE);

            OutputStream outputStream = new FileOutputStream("sdcard/"+fileName+fileExtension);

            byte data[] = new byte[1024];
            long total = 0;
            int count;

            while((count = inputStream.read(data)) != -1) {
                total += count;
                int progress = (int) (total*100/fileLength);
                publishProgress(progress);
                outputStream.write(data, 0, count);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mPDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        mPDialog.dismiss();
    }
}
