package com.ustc.quincy.docshare.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.model.Device;
import com.ustc.quincy.docshare.util.SocketManager;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Author: Created by QuincyHu on 2016/7/1 0001 19:07.
 * Email:  zhihuqunxing@163.com
 */
public class SendFile extends AppCompatActivity{
    //UI控件
    private TextView txtUserName;
    private TextView txtDeviceName;
    private TextView txtDeviceIp;
    private TextView txtFilePath;
    private Button btnChoseFile;
    private Button btnSendFile;
    private Button btnSendCancel;

    //目标机信息
    private Device targetDevice;

    //文件信息
    private File file;
    private String filePath;
    private String fileName;
    private String fileAbsPath;

    //socket
    private ServerSocket server;
    private SocketManager socketManager;
    private Handler handler;
    private int port=6666;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        //初始化控件
        txtUserName = (TextView) findViewById(R.id.send_device_username);
        txtDeviceName = (TextView) findViewById(R.id.send_device_devicename);
        txtDeviceIp = (TextView) findViewById(R.id.send_device_ipaddress);
        txtFilePath = (TextView) findViewById(R.id.send_file_path);
        btnChoseFile = (Button) findViewById(R.id.btn_chosefile);
        btnSendFile = (Button) findViewById(R.id.btn_send_file);
        btnSendCancel = (Button) findViewById(R.id.btn_send_cancel);

        //显示目标机信息
        targetDevice = new Device();
        Intent it = getIntent();
        targetDevice = (Device) it.getSerializableExtra("target");
        txtUserName.setText(targetDevice.getUserName());
        txtDeviceName.setText(targetDevice.getDeviceName());
        txtDeviceIp.setText(targetDevice.getIpAddress());

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (server != null) {
            socketManager = new SocketManager(server);
            Message.obtain(handler, 1,  "监听端口:" + port).sendToTarget();
        }
        //选择文件
        btnChoseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择文件
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });

        //发送
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始发送文件
                Message.obtain(handler, 0, fileName + "正在发送文件至" + targetDevice.getUserName() ).sendToTarget();
                Thread sendThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = socketManager.SendFile(fileName, fileAbsPath, targetDevice.getIpAddress(), port);
                        Message.obtain(handler,0,response).sendToTarget();
                    }
                });
                sendThread.start();
            }
        });

        //取消
        btnSendCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消发送文件
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            Log.v("DocShare","file uri:" + uri);
            filePath = getPath(SendFile.this,uri);
            txtFilePath.setText(filePath);

            file = new File(filePath);
            fileName = file.getName();
            fileAbsPath = file.getAbsolutePath();
            Log.v("DocShare","file name: "+file.getName());
            Log.v("DocShare","file absoulte path: " + file.getAbsolutePath());
        }
        else {
            txtFilePath.setText("/");
        }
    }

    @Override
    protected void onDestroy() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }else{
            String filename=null;
            Cursor cursor;
            if (uri.getScheme().toString().compareTo("content") == 0) {
                cursor = getContentResolver().query(uri, new String[] {MediaStore.Audio.Media.DATA}, null, null, null);
                if (cursor.moveToFirst()) {
                    filename = cursor.getString(0);
                }
            }else if (uri.getScheme().toString().compareTo("file") == 0)         //file:///开头的uri
            {
                filename = uri.toString();
                filename = uri.toString().replace("file://", "");
                //替换file://
                if(!filename.startsWith("/mnt")){
                    //加上"/mnt"头
                    filename += "/mnt";
                }
            }
            return filename;
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
