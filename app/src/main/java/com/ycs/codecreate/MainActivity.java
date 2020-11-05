package com.ycs.codecreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.print.PrinterId;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private EditText et;
    private ImageView ig;
    private Bitmap bm;
    private boolean flag=false;
    private int width;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissions,199);
            }
        }
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.btn);
        et=findViewById(R.id.ed);
        ig=findViewById(R.id.QRcode);
        ig.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=false;
                ig.setVisibility(View.VISIBLE);
                String code=et.getText().toString();
                if(code.length()==0){
                    shake();
                }else{
                    //Toast.makeText(MainActivity.this, et.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    changeIcon();
                }

            }
        });
        ig.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bm!=null){
                            bm=TwoCodeCreateUtil.createQRCode(et.getText().toString().trim(),200,200,null);
                            savePic(bm);
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage("确认保存二维码吗？")
                .create().show();
                return false;
            }
        });

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 321) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
//                        finish();
//                } else {
//                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
    public void changeIcon(){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(btn, "translationY", -width*2/3);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(et, "translationY", -width*2/3);
        animatorSet.play(animator3).with(animator5);
        animatorSet.setDuration(500).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bm=TwoCodeCreateUtil.createQRCode(et.getText().toString().trim(),200,200,null);
                ImageView ig=findViewById(R.id.QRcode);
                ig.setImageBitmap(bm);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    public void shake(){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(et, "translationX", 0f,-60f,100f,0f);
        animatorSet.play(animator1);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        animatorSet.setDuration(200).start();
    }
    public void savePic(Bitmap bitmap){
        if(flag){
            Toast.makeText(this, "文件已存在", Toast.LENGTH_SHORT).show();
            flag=true;
            return;
        }
        String filename = Environment.getExternalStorageDirectory() +"/savedQRCode/";
        File file = new File(filename);
        if(!file.exists()){
            file.mkdirs();
        }
        Timestamp t=new Timestamp(new Date().getTime());
        String time=t.toString().substring(0,t.toString().length()-4);
        time=time.replace(" ","");
        time=time.replace("-","");
        time=time.replace(":","");
        String strFileName="QRcode"+time+".jpg";
        File f = new File(file,strFileName);
        if(f.exists()){
            Toast.makeText(this, "文件已存在", Toast.LENGTH_SHORT).show();
            return;
        }

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);//把Bitmap对象解析成流
                Toast.makeText(this,"保存成功！文件名为"+strFileName,Toast.LENGTH_SHORT).show();
                flag=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        width = ig.getWidth();
        //Log.e("1", width+"");
    }


}