package com.ustc.quincy.docshare.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Author: Created by QuincyHu on 2016/6/24 0024 13:48.
 * Email:  zhihuqunxing@163.com
 */
public class SocketManager {
    private ServerSocket server;

    //构造函数
    public SocketManager(ServerSocket serverSocket){
        this.server = serverSocket;
    }

    //接收文件名
    public String receiveFileName(){
        try{
            //接收文件名
            Socket name = server.accept();
            InputStream nameStream = name.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(nameStream);
            BufferedReader br = new BufferedReader(streamReader);
            String fileName = br.readLine();
            br.close();
            streamReader.close();
            nameStream.close();
            name.close();
            return fileName;
        }catch(Exception e){
            return "接收错误:\n" + e.getMessage();
        }
    }

    //接收文件大小
    public String receiveFileSize(){
        try{
            //接收文件名
            Socket size = server.accept();
            InputStream sizeStream = size.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(sizeStream);
            BufferedReader br = new BufferedReader(streamReader);
            String fileSize = br.readLine();
            br.close();
            streamReader.close();
            sizeStream.close();
            size.close();
            return fileSize;
        }catch(Exception e){
            return "接收错误:\n" + e.getMessage();
        }
    }

    //接收文件内容
    public String ReceiveFileContent(String fileName){
        try{
            //接收文件数据
            Socket data = server.accept();
            InputStream dataStream = data.getInputStream();
            String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
            FileOutputStream file = new FileOutputStream(savePath, false);
            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = dataStream.read(buffer)) != -1){
                file.write(buffer, 0 ,size);
            }
            file.close();
            dataStream.close();
            data.close();
            return fileName + " 接收完成";
        }catch(Exception e){
            return "接收错误:\n" + e.getMessage();
        }
    }

    //发送文件
    public String SendFile(String fileName, String path, String ipAddress, int port){
        try {
            //发送文件名
            Socket name = new Socket(ipAddress, port);
            OutputStream outputName = name.getOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
            BufferedWriter bwName = new BufferedWriter(outputWriter);
            bwName.write(fileName);
            bwName.close();
            outputWriter.close();
            outputName.close();
            name.close();

            //发送文件大小
            Socket fileSize = new Socket(ipAddress, port);
            OutputStream outputFileSize = fileSize.getOutputStream();
            OutputStreamWriter outputSizeWriter = new OutputStreamWriter(outputFileSize);
            BufferedWriter bwSize = new BufferedWriter(outputSizeWriter);
            File tmp = new File(path);
            bwSize.write(String.valueOf(FileUtils.getFileSize(tmp)));
            bwSize.close();
            outputSizeWriter.close();
            outputFileSize.close();
            fileSize.close();

            //发送文件内容
            Socket data = new Socket(ipAddress, port);
            OutputStream outputData = data.getOutputStream();
            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024];
            while((size = fileInput.read(buffer, 0, 1024)) != -1){
                outputData.write(buffer, 0, size);
            }
            outputData.close();
            fileInput.close();
            data.close();
            return fileName + " 发送完成";
        } catch (Exception e) {
            return "发送错误:\n" + e.getMessage();
        }
    }

}
