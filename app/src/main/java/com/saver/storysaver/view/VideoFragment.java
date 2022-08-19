package com.saver.storysaver.view;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saver.storysaver.instagram.IgLink;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;
import com.saver.storysaver.whatsapp.adapter.VideoAdapter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class VideoFragment extends Fragment {

    View view;

    RecyclerView rvVideo;
    DocumentFile uri;
    DocumentFile[] files;
    String[] okFileExtensions;
    ArrayList<DocumentFile> videoFiles;

    ProgressBar pgVideo;
    TextView tvStatus;

    String uriString;
    Button btnDownloadVideo;
    Boolean isInsta = false;
    Boolean isError = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, container, false);

        initView();


        if (requireActivity().getIntent().getStringExtra("type").equals("ig")) {

            isInsta = true;

            rvVideo.setAdapter(new VideoAdapter(getContext(), Util.VID_URL_LIST));

            pgVideo.setVisibility(View.GONE);
        } else {

            try {

                getVideoList();
            } catch (Exception e) {
                Log.d("URI_LOG", "---------- ERROR ----------" + e);

            }
        }


        btnDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < (isInsta ? Util.IMG_URL_LIST.size() : videoFiles.size()); i++) {

                    if (isInsta) {
                        downloadInstaVideo(Util.IMG_URL_LIST.get(i));
                    } else {
                        Log.d("WA_LOG", "------------ URI PATH -----------" + videoFiles.get(i).getUri().getPath());
                        Log.d("WA_LOG", "------------ URI   -----------" + videoFiles.get(i).getUri());
                        Log.d("WA_LOG", "------------ URI normalizeScheme -----------" + videoFiles.get(i).getUri().normalizeScheme().getPath());

                        String[] str = videoFiles.get(i).getUri().getPath().split(":");
                        String url = str[str.length - 1];

                        Log.d("WA_LOG", "============= URI  =============" + url);

                        downloadWhatsappVideo(videoFiles.get(i));

                    }
                }

                if (!isError) {
                    if (isInsta) {
                        Toast.makeText(getContext(), "Download Started to to Downloads/" + getContext().getString(R.string.app_name) + "/Instagram/Videos", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Images Saved Successfully to Downloads/" + getContext().getString(R.string.app_name) + "/WhatsApp/Images", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isError = false;
                    Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    private void getVideoList() {


        uriString = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                .getString("uri", "");

        Log.d("TAG_URI", "---------- uriString ----------" + uriString);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = DocumentFile.fromTreeUri(getContext(), Uri.parse(uriString));
            Log.d("URI_LOG", "---------- VERSION >= Q ----------" + uri.listFiles().length);

        } else {
            uri = DocumentFile.fromFile(IgLink.file);
            Log.d("", "---------- VERSION <Q ----------" + uri.listFiles().length);
        }

        if (uri != null) {
            files = uri.listFiles();
        }

        videoFiles = getVideoFile(files);
        if (videoFiles.size() == 0) {

            pgVideo.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            pgVideo.setVisibility(View.GONE);
            tvStatus.setVisibility(View.GONE);
            rvVideo.setAdapter(new VideoAdapter(getContext(), videoFiles));
        }
    }

    ArrayList<DocumentFile> getVideoFile(DocumentFile[] files) {

        ArrayList<DocumentFile> documentFiles = new ArrayList<>();

        okFileExtensions = new String[]{
                "mp4",
                "mkv",
                "avi",
                "mov",
        };

        for (DocumentFile file : files) {

            for (String type : okFileExtensions) {

                if (file.getName().toLowerCase().endsWith(type)) {
                    documentFiles.add(file);
                    break;

                }
            }
        }


        return documentFiles;
    }

    private void downloadWhatsappVideo(DocumentFile srcFile) {


        ContentResolver resolver = getContext().getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, srcFile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, srcFile.getType());

//        if (Build.VERSION.SDK_INT >=)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Util.IMG_FOLDER);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                    + File.separator + getContext().getString(R.string.app_name)
                    + File.separator + "WhatsApp"
                    + File.separator + "Videos");
        }
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

        try {
            InputStream in = resolver.openInputStream(srcFile.getUri());

            OutputStream out = resolver.openOutputStream(uri);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
//                Log.d("WA_LOG", "============= While Calles =============");

                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (Exception e) {
//            Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();

            isError = true;
            e.printStackTrace();
        }

    }


    private void downloadInstaVideo(String uriString) {

        try {


            String fName = "VID_" + System.currentTimeMillis() + ".mp4";

            File file = new File(Util.IG_VID, fName);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uriString));
//        request.setDescription("Some descrition");
            request.setTitle(fName);
            request.setVisibleInDownloadsUi(true);
// in order for this if to run, you must use the android 3.2 to compile your app
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(file));

            DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            Toast.makeText(getContext(), "Downloading Started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
        }
    }


    private void initView() {

        rvVideo = view.findViewById(R.id.rvVideo);
        rvVideo.setLayoutManager(new GridLayoutManager(getContext(), 2));
        pgVideo = view.findViewById(R.id.pgVideo);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnDownloadVideo = view.findViewById(R.id.btnDownloadVideo);

    }
}