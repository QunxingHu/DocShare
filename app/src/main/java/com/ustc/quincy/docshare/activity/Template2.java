package com.ustc.quincy.docshare.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.ustc.quincy.docshare.R;
/**
 * Created by Administrator on 2016-07-04.
 */
public class Template2  extends Activity{

    private static final int PHOTO_REQUEST_JPG1 = 1;
    private static final int PHOTO_REQUEST_JPG2 = 2;

    private static final int PHOTO_REQUEST_SYNTHESIZE = 0;
    private static final int PHOTO_REQUEST_SAVE1=5;
    private static final int PHOTO_REQUEST_SAVE2=6;

    private Button btn_jpg1;
    private Button btn_jpg2;
    private Button btn_synthesize;
    private ImageView iv_image1;
    private ImageView iv_image2;
;
    private Uri imageUri;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template2);
        Toast.makeText(getApplicationContext(),"请选择需要合成的图片！",Toast.LENGTH_SHORT).show();
        iv_image1 = (ImageView) this.findViewById(R.id.imageView);
        iv_image2 = (ImageView) this.findViewById(R.id.imageView2);

        btn_jpg1 = (Button) findViewById(R.id.btn_jpg1);
        btn_jpg1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                File outputImage=new File(Environment.getExternalStorageDirectory(),"tempImage.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri=Uri.fromFile(outputImage);
                gallery(v, PHOTO_REQUEST_JPG1);

            }
        });

        btn_jpg2 = (Button) findViewById(R.id.btn_jpg2);
        btn_jpg2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                File outputImage=new File(Environment.getExternalStorageDirectory(),"tempImage1.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri=Uri.fromFile(outputImage);
                gallery(v, PHOTO_REQUEST_JPG2);
            }
        });

        btn_synthesize = (Button) findViewById(R.id.btn_synthesize);
        btn_synthesize.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                synthesize();
                Toast.makeText(getApplicationContext(),"恭喜你合成图片成功！",Toast.LENGTH_SHORT).show();
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQUEST_JPG1) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri, 360, 250,1);
            }
        } else if (requestCode == PHOTO_REQUEST_JPG2) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri, 360,250,2);
            }
        } else if (requestCode == PHOTO_REQUEST_SYNTHESIZE) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                //crop(uri);
            }
        }else if (requestCode == PHOTO_REQUEST_SAVE1) {
            try{
                Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                iv_image1.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (requestCode == PHOTO_REQUEST_SAVE2) {
            try{
                Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                iv_image2.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    /*
    ** 从相册获取
    */
    public void gallery(View view, int requestCode) {
        // 激活系统图库，选择一张图片

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestCode);
    }


    /*
     * 剪切图片
     */
    private void crop(Uri uri, int width, int height,int fla) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //intent.setClassName("com.android.camera", "com.android.camera.CropImage");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);

        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if(fla==1) startActivityForResult(intent, PHOTO_REQUEST_SAVE1);
        else if(fla==2) startActivityForResult(intent, PHOTO_REQUEST_SAVE2);

    }


    public void saveImage(Bitmap bmp) {

        File file=new File("/sdcard/Synthesize002.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


 /*   public File saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "DocShare");
        if (!appDir.exists()) {
            appDir.mkdir();
            try {
                appDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        String fileName = System.currentTimeMillis() + ".jpg";
        String fileName = "Synthesize002.jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO @xtf 这里得要通知图库更新
        return  file;
    }*/


    public List<String> getPictures(final String strPath) {
        List<String> list = new ArrayList<String>();
        File file = new File(strPath);
        File[] allfiles = file.listFiles();
        if (allfiles == null) {
            return null;
        }
        for(int k = 0; k < allfiles.length; k++) {
            final File fi = allfiles[k];
            if(fi.isFile()) {
                int idx = fi.getPath().lastIndexOf(".");
                if (idx <= 0) {
                    continue;
                }
                String suffix = fi.getPath().substring(idx);
                if (suffix.toLowerCase().equals(".jpg") ||
                        suffix.toLowerCase().equals(".jpeg") ||
                        suffix.toLowerCase().equals(".bmp") ||
                        suffix.toLowerCase().equals(".png") ||
                        suffix.toLowerCase().equals(".gif") ) {
                    list.add(fi.getPath());
                }
            }
        }
        return list;
    }

    private void synthesize(){
        Paint paint = new Paint();
        //创建一个的Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(360, 500, Bitmap.Config.ARGB_8888)  ;
        Canvas canvas = new Canvas (bitmap) ;

        List<String> list = getPictures(Environment.getExternalStorageDirectory() + "");
        if (list != null) {
            Bitmap bm0 = BitmapFactory.decodeFile(list.get(0));
            Bitmap bm1 = BitmapFactory.decodeFile(list.get(1));

            canvas.drawBitmap(bm0, 0, 0, paint);
            canvas.drawBitmap(bm1, 0,bm0.getHeight(), paint);

        }
        else {
            Log.d("222", "list is null!!!");
        }
        iv_image1.setImageBitmap(bitmap);
        saveImage(bitmap);
    }
}

