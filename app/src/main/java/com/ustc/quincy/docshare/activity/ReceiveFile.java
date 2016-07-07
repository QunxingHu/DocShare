package com.ustc.quincy.docshare.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.util.FileUtils;
import com.ustc.quincy.docshare.util.SocketManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Author: Created by QuincyHu on 2016/7/2 0002 16:30.
 * Email:  zhihuqunxing@163.com
 */
public class ReceiveFile extends AppCompatActivity {
    private TextView txtFileName;
    private TextView txtFileType;
    private TextView txtFileSize;
    private TextView txtFileUser;
    private ProgressBar progressBar;
    private int port = 6666;
    private ServerSocket server;
    private SocketManager socketManager;
    private Handler handler;
    private long fileSize;
    private String fileName;
    private double fileSizeKB;
    private String fileType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_file);

        //初始化控件
        txtFileName = (TextView) findViewById(R.id.receive_file_name);
        txtFileType = (TextView) findViewById(R.id.receive_file_type);
        txtFileSize = (TextView) findViewById(R.id.receive_file_size);
        txtFileUser = (TextView) findViewById(R.id.receive_file_user_name);
        progressBar = (ProgressBar) findViewById(R.id.receive_file_progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                switch (msg.what){
                    case 2:{
                        //显示文件信息
                        if(fileName!=null){
                            txtFileSize.setText(FileUtils.FormetFileSize(fileSize));
                            txtFileName.setText(fileName);
                            txtFileType.setText(FileUtils.getFileType(fileName));
                        }else
                        {
                            txtFileSize.setText("0B");
                            txtFileName.setText("/");
                            txtFileType.setText("");
                        }


                    }break;
                }
            }
        };

        //监听端口6666
        Thread listener = new Thread(new Runnable(){
            @Override
            public void run() {
                //绑定端口{
                    try {
                        server = new ServerSocket(port);
                    } catch (Exception e) {
                       e.printStackTrace();
                    }

                if (server != null) {
                    socketManager = new SocketManager(server);
                    Message.obtain(handler, 1, "监听端口:" + port).sendToTarget();
                    while (true)
                    {
                        final String receiveFileName;
                        //接收文件名
                        receiveFileName = socketManager.receiveFileName();
                        fileName = receiveFileName;
                        final String name = receiveFileName;
                        Log.v("DocShare", "收到文件名：" + fileName);
                        //接收文件大小
                        fileSize = Long.valueOf(socketManager.receiveFileSize());

                        Log.v("DocShare", "收到文件大小：" + fileSize);
                        Message.obtain(handler, 2, "收到文件信息:" + port).sendToTarget();

                        //弹出提示框
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ReceiveFile.this);
                        dialog.setTitle("DocShare提示：");
                        dialog.setMessage("是否接收文件：" + fileName);

                        dialog.setPositiveButton("接收", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new receiveTask().execute(fileName);
                            }
                        });
                        dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //拒绝接收文件
                                try {
                                    server.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(ReceiveFile.this, "拒绝接收文件" + fileName, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Looper.prepare();
                        dialog.show();
                        Looper.loop();
                    }
                    }else{
                        Message.obtain(handler, 1, "未能绑定端口").sendToTarget();
                    }
            }
        });
        listener.start();
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

    private class receiveTask extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //开始接收文件内容
            String response =null;
            //接收文件数据
            try{
                Socket data = server.accept();
                InputStream dataStream = data.getInputStream();
                String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + params[0];
                FileOutputStream file = new FileOutputStream(savePath, false);
                byte[] buffer = new byte[1024];
                int size = -1;
                long receiveSize =0;
                int percent=0;
                //更新进度
                while ((size = dataStream.read(buffer)) != -1){
                        file.write(buffer, 0 ,size);
                        receiveSize +=1024;
                        percent = (int) ((receiveSize/fileSize)*100);

                    if (percent>=100)
                        percent=100;
                    publishProgress(percent);
                }

                file.close();
                dataStream.close();
                data.close();

                response =  fileName + " 接收完成";
                Message.obtain(handler, 0, response).sendToTarget();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                response = fileName + " 接收失败";
                Message.obtain(handler, 0, response + e.getMessage()).sendToTarget();
                return false;
            }


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                //通知栏提醒
                //新建通知
                NotificationManager nm = (NotificationManager) getSystemService(ReceiveFile.NOTIFICATION_SERVICE);
                NotificationCompat.Builder ntf = new NotificationCompat.Builder(ReceiveFile.this);
                //设置通知图标
                ntf.setSmallIcon(R.drawable.ic_notify_file);
                ntf.setContentTitle("收到文件");
                //设置通知内容
                ntf.setContentText("接收文件" + fileName + "完成");
                //设置通知声音
                ntf.setDefaults(Notification.DEFAULT_ALL);
                //设置点击后自动清除通知
                ntf.setAutoCancel(true);

                //通知点击事件
                String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
                Intent pIntent = openFile(filePath);
                PendingIntent pd = PendingIntent.getActivity(ReceiveFile.this, 0, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                ntf.setContentIntent(pd);
                nm.notify(0, ntf.build());
            }else {
                Toast.makeText(ReceiveFile.this, "接收文件失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Intent openFile(String filePath){

        File file = new File(filePath);
        if(!file.exists()) return null;
		/* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
		/* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            return getAudioFileIntent(filePath);
        }else if(end.equals("3gp")||end.equals("mp4")){
            return getAudioFileIntent(filePath);
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            return getImageFileIntent(filePath);
        }else if(end.equals("apk")){
            return getApkFileIntent(filePath);
        }else if(end.equals("ppt")){
            return getPptFileIntent(filePath);
        }else if(end.equals("xls")){
            return getExcelFileIntent(filePath);
        }else if(end.equals("doc")){
            return getWordFileIntent(filePath);
        }else if(end.equals("pdf")){
            return getPdfFileIntent(filePath);
        }else if(end.equals("chm")){
            return getChmFileIntent(filePath);
        }else if(end.equals("txt")){
            return getTextFileIntent(filePath,false);
        }else{
            return getAllIntent(filePath);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public Intent getAllIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"*/*");
        return intent;
    }
    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent( String param ){

        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent( String param, boolean paramBoolean){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean){
            Uri uri1 = Uri.parse(param );
            intent.setDataAndType(uri1, "text/plain");
        }else{
            Uri uri2 = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }
    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
