package com.imageuploaddemo;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imageuploaddemo.netUtils.RuntimePermissionsActivity;

import java.io.File;

public class Splash extends RuntimePermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Splash.super.requestAppPermissions(new
                                String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, R.string.runtime_permissions_txt
                        , 20);
            }
        },500);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        try {
            File pdfDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ""+GlobalElements.directory);
            if (!pdfDir.exists())
            {
                pdfDir.mkdir();
            }

            Intent i = new Intent(Splash.this,MainActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
