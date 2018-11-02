package com.imageuploaddemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imageuploaddemo.custom.Toaster;
import com.imageuploaddemo.netUtils.RequestInterface;
import com.imageuploaddemo.netUtils.RetrofitClient;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    File file1=null,imageDir;
    Uri outputFileUri;
    String user_img1;

    ImageView reg_user_img;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "" + GlobalElements.directory + "");
        if (!imageDir.exists())
        {
            imageDir.mkdir();
        }

        reg_user_img= (ImageView) findViewById(R.id.reg_user_img);
        submit = (Button) findViewById(R.id.submit);


        reg_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    file1=null;
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_gallery);

                    LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.dialog_camera);
                    LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.dialog_gallery);
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            try {
                                user_img1 = imageDir+"/test.jpg";
                                file1 = new File(user_img1);
                                try {
                                    file1.createNewFile();
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                                if(GlobalElements.versionCheck())
                                {
                                    outputFileUri = FileProvider.getUriForFile(MainActivity.this, ""+GlobalElements.fileprovider_path, file1);
                                }
                                else {
                                    outputFileUri = Uri.fromFile(file1);
                                }
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                                startActivityForResult(cameraIntent,180);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            //Crop.pickImage(MainActivity.this);
                        }
                    });
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GlobalElements.isConnectingToInternet(MainActivity.this))
                {
                    RegUser();
                }
                else
                {
                    GlobalElements.showDialog(MainActivity.this);
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        /*if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
        else*/ if(requestCode==180)
        {
            cropImage(outputFileUri);
        }
        else if(requestCode==181)
        {
            try {
                Bitmap largeIcon = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                reg_user_img.setImageBitmap(largeIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void cropImage(Uri uri) {
        // Use existing crop activity.
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            if(GlobalElements.versionCheck()) {
                cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            }
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 181);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RegUser() {
        final ProgressDialog pd;
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //todo call upload server client
        RequestInterface request = RetrofitClient.getClient().create(RequestInterface.class);
        //todo request body
        RequestBody requestfile;
        MultipartBody.Part body;

        requestfile = RequestBody.create(MediaType.parse("image/*"), file1);
        body = MultipartBody.Part.createFormData("file", file1.getName(), requestfile);
        Call<ResponseBody> call = request.RegUser("1",body);

        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    String json_responce = response.body().string();
                    JSONObject json = new JSONObject(json_responce);
                    if (json.getInt("ack") == 1) {
                        Toaster.show(MainActivity.this, "Image upload Successfully", false, Toaster.DANGER);
                    } else {
                        Toaster.show(MainActivity.this, "File not found", false, Toaster.DANGER);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }
}
