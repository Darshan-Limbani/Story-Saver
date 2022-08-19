package com.saver.storysaver.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.saver.storysaver.R;
import com.saver.storysaver.instagram.IgLink;
import com.saver.storysaver.instagram.IgLoginActivity;
import com.saver.storysaver.view.ViewImageActivity;
import com.saver.storysaver.view.ViewVideoActivity;
import com.saver.storysaver.whatsapp.WhatsAppStatusActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class IGUtils {

    Context context;
    String url;

    String IG_MAIN = "https://www.instagram.com/";
    String IG_MAIN_STORIES = "https://instagram.com/";
    String IG_FINAL;
//    String USER_NAME;

    String POST = "POST";
    String GET = "GET";
    //    String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Instagram 105.0.0.11.118 (iPhone11,8; iOS 12_3_1; en_US; en-US; scale=2.Mozilla/5.0 (iPhone; CPU iPhone OS 12_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Instagram 105.0.0.11.118 (iPhone11,8; iOS 12_3_1; en_US; en-US; scale=2.00; 828x1792; 165586599)";
    String USER_AGENT =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 12_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Instagram 105.0.0.11.118 (iPhone11,8; iOS 12_3_1; en_US; en-US; scale=2.00; 828x1792; 165586599)";
    String LOG = "INSTA_LOG";
    Boolean IS_POST;
    Boolean IS_REEL;
    Boolean IS_TV;
    Boolean IS_STORY;
    Boolean IS_DP;

    String result;
    String storyId;
    Boolean IS_FINAL = false;
    String id;
    Boolean isImage = false;
    Boolean isVideo = false;

    Dialog loadingDialog;


    public IGUtils(Context ctx, String url) {
        this.context = ctx;
        this.url = url;
//        USER_NAME = url;

        IS_DP = IgLink.isDp;

        showLoadingDialog();
        validateUrl();


    }

    private void showLoadingDialog() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = new Dialog(context);
//                loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                loadingDialog.setContentView(R.layout.custom_dialog);
                loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                loadingDialog.getWindow().setGravity(Gravity.CENTER);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(false);

                if (!loadingDialog.isShowing())
                    loadingDialog.show();
            }
        });

    }


    public String buildResultString(HttpURLConnection httpconn) throws IOException {

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));

            if ("gzip".equals(httpconn.getContentEncoding())) {
                rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(httpconn.getInputStream())));
            }
        } catch (IOException e) {
            dismissDialog();

            if (!Util.IsLogin()) {
                showLoginDialog();
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Media can be not Download!!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            Log.d("INSTA_LOG", "------ IOEXCEPTION in buildresult string ------ " + e.getMessage());
            e.printStackTrace();
        }
        StringBuffer result = new StringBuffer();
        String str;
        while (true) {
            str = rd.readLine();
            if (str == null) {
//                Log.d("INSTA_LOG", "------ Result String------ " + result);
                dismissDialog();
                return result.toString();
            }
            result.append(str);
        }
    }

    private void dismissDialog() {

        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    public void showLoginDialog() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {


                new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Login")
                        .setIcon(R.drawable.ic_insta)
                        .setMessage("You have to Login to Access this Content")
                        .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            getFolderPermission();
//                        }

                                dismissDialog();
                                context.startActivity(new Intent(context, IgLoginActivity.class));
                                dialogInterface.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void validateUName() {
        result = "";
        Log.d(LOG, "------ validateUName called ------ ");

    /*    if (IG_FINAL.isEmpty()) {
            IG_FINAL = IG_MAIN + USER_NAME + "/" + "?__a=1";
        }*/

        Log.d(LOG, "------ IG_FINAL ------ " + IG_FINAL);
        try {
            URLConnection urlConn = new URL(IG_FINAL).openConnection();
            HttpsURLConnection conn = (HttpsURLConnection) urlConn;
            conn.setRequestMethod(GET);
            conn.setConnectTimeout(20000);
//            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("User-Agent", USER_AGENT);
//            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Cookie", "ds_user_id=\"" + Util.getUserId().trim() + "\";sessionid=\" " + Util.getSessionId().trim() + "\";");

//            conn.setRequestProperty("Cookie", "ds_user_id=\"48566271418\";sessionid=\"48566271418%3AHiv2n6wBOuCQdA%3A10\";csrftoken=\"gBwcGi2REgHZVTvjN8X1WY7fTtPsrk08\"");
            conn.setRequestProperty("Referer", IG_MAIN);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("authority", "i.instagram.com/");
//            conn.connect();

            Log.d(LOG, "------ Request Properties : ------ " + conn);
            Log.d(LOG, "------ Response Code ------ :  ------ " + conn.getResponseCode());
            Log.d(LOG, "------ Response Message ------ : ------ " + conn.getResponseMessage());
//            Log.d(LOG, "------ Response Message ------ : ------ " + conn.getre());


            if (conn.getResponseCode() == 200) {
                JSONObject obj;
                result = buildResultString(conn);

                Log.d(LOG, "------ FINAL RESULT ------ " + result);

                if (result.contains("<!DOCTYPE html>")) {
                    if (!Util.IsLogin()) {
                        showLoginDialog();

                        return;
                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        });
                    }

                }

                if (IS_POST) {
                    IS_POST = false;


                    obj = new JSONObject(result);

                    if (obj.has("graphql")) {

                        Log.d(LOG, "------====================== POST graphql ------ : ------ ");


                        obj = obj.getJSONObject("graphql").getJSONObject("shortcode_media");//.getJSONObject("display_url");

                        if (obj.getString("__typename").equals("GraphSidecar")) {
                            JSONArray array = obj.getJSONObject("edge_sidecar_to_children")
                                    .getJSONArray("edges");

                            for (int i = 0; i < array.length(); i++) {

                                obj = array.getJSONObject(i).getJSONObject("node");

                                if (obj.getString("__typename").equals("GraphImage")) {
                                    isImage = true;

                                    Util.IMG_URL_LIST.add(
                                            obj.getString("display_url")
                                    );
                                } else if (obj.getString("__typename").equals("GraphVideo")) {
                                    isVideo = true;
                                    Util.VID_URL_LIST.add(
                                            obj.getString("video_url")
                                    );
                                }
                            }


                            context.startActivity(new Intent(context, WhatsAppStatusActivity.class)
                                    .putExtra("type", "ig")
                                    .putExtra("isVideo", isVideo)
                                    .putExtra("isImage", isImage));

                        } else if (obj.getString("__typename").equals("GraphImage")) {
                            context.startActivity(new Intent(context, ViewImageActivity.class)
                                    .putExtra(Util.URL, obj.getString("display_url")));
                        } else if (obj.getString("__typename").equals("GraphVideo")) {
                            context.startActivity(new Intent(context, ViewVideoActivity.class)
                                    .putExtra(Util.URL, obj.getString("video_url")));
                        }
                        dismissDialog();

                    } else if (obj.has("items")) {

//                    JSONArray array = new JSONObject(result).getJSONArray("media_ids");
                        obj = obj.getJSONArray("items").getJSONObject(0);


                        if (obj.has("carousel_media")) {

                            JSONArray arr = obj.getJSONArray("carousel_media");

                            Log.d(LOG, "------------- POST ARRAY  ------ " + arr.length() + "\n\n\n");
                            Util.IMG_URL_LIST = new ArrayList<>();
                            Util.VID_URL_LIST = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {

                                if (arr.getJSONObject(i).getInt("media_type") == 1) {
                                    isImage = true;
                                    Util.IMG_URL_LIST.add(arr.getJSONObject(i)
                                            .getJSONObject("image_versions2")
                                            .getJSONArray("candidates")
                                            .getJSONObject(0)
                                            .getString("url"));
                                } else if (arr.getJSONObject(i).getInt("media_type") == 2) {

                                    isVideo = true;
                                    Util.VID_URL_LIST.add(arr.getJSONObject(i)
                                            .getJSONArray("video_versions")
                                            .getJSONObject(i)
                                            .getString("url"));
                                }
                            }

                            context.startActivity(new Intent(context, WhatsAppStatusActivity.class)
                                    .putExtra("type", "ig")
                                    .putExtra("isVideo", isVideo)
                                    .putExtra("isImage", isImage));

                        } else if (obj.has("image_versions2")) {


                            if (obj.getInt("media_type") == 1) {

                                goToImageActivity(obj);

                            } else if (obj.getInt("media_type") == 2) {

                                goToVideoActivity(obj);

                            }
                        }
                    }
                    dismissDialog();

                } else if (IS_TV) {
//                IS_TV = false;
                    obj = new JSONObject(result);
                    if (obj.has("graphql")) {
                        obj = obj.getJSONObject("graphql")
                                .getJSONObject("shortcode_media");//.getJSONObject("video_url");
                        context.startActivity(new Intent(context, ViewVideoActivity.class)
                                .putExtra(Util.URL, obj.getString("video_url")));
                    } else if (obj.has("items")) {
                        obj = obj.getJSONArray("items").getJSONObject(0);

                        goToVideoActivity(obj);

                    }
                    dismissDialog();

                } else if (IS_REEL) {
//                IS_REEL = false;
                    obj = new JSONObject(result);

                    if (obj.has("graphql")) {
                        obj = obj.getJSONObject("graphql")
                                .getJSONObject("shortcode_media");//.getJSONObject("video_url");
                        context.startActivity(new Intent(context, ViewVideoActivity.class)
                                .putExtra(Util.URL, obj.getString("video_url")));
                    } else if (obj.has("items")) {

                        obj = obj.getJSONArray("items")
                                .getJSONObject(0);
                        goToVideoActivity(obj);

/*                            .getJSONArray("video_versions")
                            .getJSONObject(0);//.getJSONObject("video_url");

                    context.startActivity(new Intent(context, ViewVideoActivity.class)
                            .putExtra(Util.URL, obj.getString("url")));*/
                    }
                    dismissDialog();

                } else if (IS_STORY) {

                    if (!IS_FINAL) {
                        obj = new JSONObject(result).getJSONObject("graphql").getJSONObject("user");
                        id = obj.getString("id");
                    }

                    if (!id.trim().isEmpty()) {
                        IS_STORY = false;
                        IG_FINAL = "https://i.instagram.com/api/v1/feed/user/" + id + "/reel_media/?__a=1";
                        Log.d(LOG, "------ IG MAIN with id ------ " + IG_FINAL);
                        IS_FINAL = true;
                        Log.d(LOG, "--------------- validateUName Called : -------------" + result);
                        validateUName();
                        JSONArray array;
                        array = new JSONObject(result).getJSONArray("items");
                        obj = new JSONObject(result).getJSONArray("items").getJSONObject(getIdNum(array));

                        if (obj.getInt("media_type") == 1) {
                            goToImageActivity(obj);
                        /*String url = obj.getJSONObject("image_versions2")
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getString("url");

                        context.startActivity(new Intent(context, ViewImageActivity.class).putExtra(Util.URL, url));*/
                        }
                        if (obj.getInt("media_type") == 2) {

                            goToVideoActivity(obj);
                        /*String url = obj
                                .getJSONArray("video_versions")
                                .getJSONObject(0)
                                .getString("url");*/

//                        context.startActivity(new Intent(context, ViewVideoActivity.class).putExtra(Util.URL, url));
                        }
                    }
                    dismissDialog();

                } else if (IS_DP) {
                    IS_DP = false;
                    Log.d(LOG, "--------- DP CALLED ----------------");
                    obj = new JSONObject(result).getJSONObject("graphql").getJSONObject("user");//.getJSONObject("display_url");

                    context.startActivity(new Intent(context, ViewImageActivity.class)
                            .putExtra(Util.URL, obj.getString("profile_pic_url_hd")));

                }
                dismissDialog();

            } else if (conn.getResponseCode() == 500) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        Toast.makeText(context, "Internal Server Error Please try after some time !!!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        Toast.makeText(context, "Media can not be downloaded !!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Something went Wrong. Please try after some time!! ", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(LOG, "------ EXCEPTION ------ " + e.getMessage());
        }
    }

    private void goToImageActivity(JSONObject obj) {
        String url = null;
        try {
            url = obj.getJSONObject("image_versions2")
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getString("url");
            dismissDialog();
        } catch (JSONException e) {
            e.printStackTrace();
            dismissDialog();

        }
        context.startActivity(new Intent(context, ViewImageActivity.class)
                .putExtra(Util.URL, url));
        dismissDialog();


    }

    private void goToVideoActivity(JSONObject obj) {
        String url = null;
        try {
            url = obj.getJSONArray("video_versions")
                    .getJSONObject(0)
                    .getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.startActivity(new Intent(context, ViewVideoActivity.class)
                .putExtra(Util.URL, url)
                .putExtra("isFile", false));


    }

    private int getIdNum(JSONArray array) {
        Log.d(LOG, "-------------- Methode called id lenght ------------- " + storyId);

        for (int i = 0; i < array.length(); i++) {
            try {
                if (array.getJSONObject(i).getString("id").contains(storyId)) {
                    Log.d(LOG, "-------------- getIdNum ------------- " + array.getJSONObject(i).getString("id"));

                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return 0;
    }

    private void validateUrl() {

        if (url.startsWith(IG_MAIN) || url.startsWith(IG_MAIN_STORIES)) {

            String[] str = url.split("/");

            if (str[3].contains("p")) {
//                String[] str = url.split("/");
                IG_FINAL = IG_MAIN + "p/" + str[4] + "/?__a=1";
                IS_POST = true;
                IS_TV = false;
                IS_REEL = false;
                IS_STORY = false;
                IS_DP = false;

//                IG_FINAL = IG_FINAL + "/?__a=1";

                Log.d(LOG, "------ FINAL POST URL ------ " + IG_FINAL);

            } else if (str[3].contains("reel")) {
                IG_FINAL = IG_MAIN + "tv/" + str[4] + "/?__a=1";

                IS_REEL = false;
                IS_POST = false;
                IS_TV = true;
                IS_STORY = false;
                IS_DP = false;

//                IG_FINAL = IG_FINAL + "/?__a=1";

                Log.d(LOG, "------ FINAL REEL URL ------ " + IG_FINAL);

            } else if (str[3].contains("tv")) {
                IG_FINAL = IG_MAIN + "tv/" + str[4] + "/?__a=1";
                IS_TV = true;
                IS_REEL = false;
                IS_POST = false;
                IS_STORY = false;
                IS_DP = false;

//                IG_FINAL = IG_FINAL + "/?__a=1";

                Log.d(LOG, "------ FINAL TV URL ------ " + IG_FINAL);

            } else if (str[3].contains("stories")) {

                IS_STORY = true;
                IS_TV = false;
                IS_REEL = false;
                IS_POST = false;
                IS_DP = false;

                IG_FINAL = IG_MAIN + str[4] + "/?__a=1";
                for (int j = 0; j < str.length; j++) {

                    Log.d(LOG, "------ FINAL stories URL ------" + j + "----- " + str[j]);
                }

                String[] arr = str[5].split("\\?");
                storyId = arr[0];
                Log.d(LOG, "------ FINAL stories URL ------ " + storyId);


            }

            validateUName();

        } else if (IS_DP) {
            IS_STORY = false;
            IS_TV = false;
            IS_REEL = false;
            IS_POST = false;
            IG_FINAL = IG_MAIN + url + "/?__a=1";
            validateUName();

        }

    }


}
