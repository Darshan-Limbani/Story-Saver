package com.saver.storysaver.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.saver.storysaver.MyApplication;
import com.saver.storysaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String ROOT = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;
    public static String APP_FOLDER = ROOT + MyApplication.getContext().getString(R.string.app_name);
    public static String WA_IMG = APP_FOLDER + File.separator + "WhatsApp" + File.separator + "Images";
    public static String WA_VID = APP_FOLDER + File.separator + "WhatsApp" + File.separator + "Videos";
    public static String IG_VID = APP_FOLDER + File.separator + "Instagram" + File.separator + "Video";
    public static String IG_IMG = APP_FOLDER + File.separator + "Instagram" + File.separator + "Images";

    public static DocumentFile file;

    //    public static String VID_FOLDER = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "WhatsApp " + File.separator + "Videos";

    /*public static String IG_REEL = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "Instagram" + File.separator + "Reels";
    public static String IG_TV = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "Instagram" + File.separator + "IG TV";
    public static String IG_POST = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "Instagram" + File.separator + "Post";
    public static String IG_PROFILE = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "Instagram" + File.separator + "Profile";
    public static String IG_STORY = ROOT + MyApplication.getContext().getString(R.string.app_name) + File.separator + "Instagram" + File.separator + "Story";
*/

    public static String IG_U_NAME = "ds_user_id";
    public static String IG_SESSION_ID = "sessionid";
    public static String IS_LOGIN = "isLogin";
    public static String URL = "URI";
    public static String IS_PERMISSION = "IS_FILE_PERMISSION";
    public static String IS_URI_PERMISSION = "isPermission";
    public static String URI = "uri";

    public static SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getPackageName(), Context.MODE_PRIVATE);

    public static SharedPreferences.Editor editor = preferences.edit();

    public static List<String> IMG_URL_LIST = new ArrayList<>();
    public static List<String> VID_URL_LIST = new ArrayList<>();


    public static Boolean isPermitted = false;


    public static Boolean createAppFolder() {

        File app = new File(Util.APP_FOLDER);
        File ig = new File(Util.APP_FOLDER + File.separator + "Instagram");
        File wa = new File(Util.APP_FOLDER + File.separator + "WhatsApp");
        File wa_img = new File(Util.WA_IMG);
        File wa_vid = new File(Util.WA_VID);
        File ig_img = new File(Util.IG_IMG);
        File ig_vid = new File(Util.IG_VID);


        /*File post = new File(Util.IG_POST);
        File prof = new File(Util.IG_PROFILE);
        File reel = new File(Util.IG_REEL);
        File story = new File(Util.IG_STORY);
        File tv = new File(Util.IG_TV);*/
        try {

            if (!app.exists()) {
                app.mkdir();
            }
            if (!ig.exists()) {
                ig.mkdir();
            }

            if (!wa.exists()) {
                wa.mkdir();
            }
            if (!wa_vid.exists()) {
                wa_vid.mkdir();
            }

            if (!wa_img.exists()) {
                wa_img.mkdir();
            }


            if (!ig_img.exists()) {
                ig_img.mkdir();
            }

            if (!ig_vid.exists()) {
                ig_vid.mkdir();
            }

            /*if (!post.exists()) {
                post.mkdir();
            }
            if (!reel.exists()) {
                reel.mkdir();
            }
            if (!prof.exists()) {
                prof.mkdir();
            }
            if (!story.exists()) {
                story.mkdir();
            }
            if (!tv.exists()) {
                tv.mkdir();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return app.exists() && ig.exists() && wa.exists();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (cap == null) return false;
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network n : networks) {
                NetworkInfo nInfo = cm.getNetworkInfo(n);
                if (nInfo != null && nInfo.isConnected())
                    return true;
            }
        } else {
            NetworkInfo[] networks = cm.getAllNetworkInfo();
            for (NetworkInfo nInfo : networks) {
                if (nInfo != null && nInfo.isConnected())
                    return true;
            }
        }

        return false;
    }


    public static Boolean requestPermission(Context context) {

        Log.d("Permission_App", "--------- ROOT PATH --------" + ROOT);

        PermissionX.init((FragmentActivity) context)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            isPermitted = true;

                            Util.createAppFolder();
//                            Toast.makeText(MainActivity.this, "All permissions are granted", Toast.LENGTH_LONG).show();
                        } else {
                            isPermitted = false;
                            Toast.makeText(MyApplication.getContext(), "Permissions are Required!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return isPermitted;
    }

    public static void openCustomTab(Context context, String url) {

        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent
                    .Builder()
                    .setToolbarColor(context.getResources()
                            .getColor(R.color.ig_r))
                    .setShowTitle(false)
                    .enableUrlBarHiding()
                    .build();

            customTabsIntent.launchUrl(context, Uri.parse(url));


        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static String getUserId() {
        return preferences.getString(IG_U_NAME, "");
    }

    public static String getSessionId() {
        return preferences.getString(IG_SESSION_ID, "");
    }


    public static Boolean IsLogin() {
        return preferences.getBoolean(IS_LOGIN, false);
    }


    public static Boolean ISURIPermission() {
        return preferences.getBoolean(IS_URI_PERMISSION, false);
    }

    public static void setURIPermission(Boolean permission) {
        editor.putBoolean(IS_URI_PERMISSION, permission);
        editor.apply();
    }

    public static String getUri() {
        return preferences.getString(URI, "");
    }

    public static void setUri(String uri) {
        editor.putString(URI, uri);
        editor.apply();
    }


}
