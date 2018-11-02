package com.imageuploaddemo;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import com.gdacciaro.iOSDialog.iOSDialog;

/**
 * Created by CRAFT BOX on 7/27/2017.
 */

public class GlobalElements extends Application {


    public static String directory="demo";
    public static String fileprovider_path="com.imageuploaddemo.fileprovider";

    public static boolean isConnectingToInternet(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
                else
                {
                    NetworkInfo info1 = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (info1.isConnected()) {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static void showDialog(Context context)
    {
        final iOSDialog iOSDialog = new iOSDialog(context);
        iOSDialog.setTitle( "Internet");
        iOSDialog.setSubtitle("Please check your internet connection... ");
        iOSDialog.setPositiveLabel("Ok");
        iOSDialog.setBoldPositiveLabel(true);

        iOSDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"OK clicked",Toast.LENGTH_SHORT).show();
                iOSDialog.dismiss();
            }
        });
        iOSDialog.show();
    }



    public static boolean versionCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

}
