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

import com.saver.storysaver.instagram.IgLinkActivity;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;
import com.saver.storysaver.whatsapp.adapter.ImageAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ImageFragment extends Fragment {

    View view;
    RecyclerView rvImages;
    DocumentFile uri;
    DocumentFile[] files;
    String[] validExtension;
    ArrayList<DocumentFile> imageFiles;
//    ArrayList<String> imgUri;

    ProgressBar pgImages;
    TextView tvStatus;

    Button btnDownloadImages;
    Boolean isInsta = false;

    String uriString;

    Boolean isError = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);

        initView();
        uriString = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                .getString("uri", "");


        if (requireActivity().getIntent().getStringExtra("type").equals("ig")) {

            isInsta = true;
            rvImages.setAdapter(new ImageAdapter(getContext(), Util.IMG_URL_LIST));
            pgImages.setVisibility(View.GONE);
        } else {
            isInsta = false;
            getImagesList();
        }


        btnDownloadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < (isInsta ? Util.IMG_URL_LIST.size() : imageFiles.size()); i++) {

                    if (isInsta) {
                        downloadIGImages(Util.IMG_URL_LIST.get(i));

                    } else {
                        Log.d("WA_LOG", "------------ URI PATH -----------" + imageFiles.get(i).getUri().getPath().toString());
                        Log.d("WA_LOG", "------------ URI   -----------" + imageFiles.get(i).getUri());

                        Log.d("WA_LOG", "------------ URI normalizeScheme -----------" + imageFiles.get(i).getUri().normalizeScheme().getPath());
                        String[] str = imageFiles.get(i).getUri().getPath().split(":");
                        String url = str[str.length - 1];

                        Log.d("WA_LOG", "============= URI  =============" + url);

                        downloadWhatsappImage(imageFiles.get(i));

                    }
                }

                if (!isError) {
                    if (isInsta) {
                        Toast.makeText(getContext(), "Download Started to to Downloads/" + getContext().getString(R.string.app_name) + "/Instagram/Images", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Images Saved Successfully to Downloads/" + getContext().getString(R.string.app_name) + "/WhatsApp/Images", Toast.LENGTH_SHORT).show();
                    }
//                    isError = false;
                } else {
                    isError = false;
                    Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    private void downloadIGImages(String uriString) {

        try {
            String fName = "IMG_" + System.currentTimeMillis() + ".jpg";

            File file = new File(Util.IG_IMG, fName);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uriString));
//        request.setDescription("Some descrition");
            request.setTitle(fName);
// in order for this if to run, you must use the android 3.2 to compile your app
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        if (isInsta)
//            request.setDestinationInExternalPublicDir(Util.IG_POST, "IMG_" + System.currentTimeMillis() + ".jpg");
//            request.setDestinationInExternalFilesDir(getContext(), Util.IG_IMG, fName);
            request.setDestinationUri(Uri.fromFile(file));
            request.allowScanningByMediaScanner();
//        else
//            request.setDestinationInExternalFilesDir(getContext(), Util.IMG_FOLDER, "IMG_" + System.currentTimeMillis() + ".jpg");
// get download service and enqueue file
            DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);


        } catch (Exception e) {
            isError = true;
            e.printStackTrace();
            Log.d("ERROR_DOWNLOAD", " -------- ERROR --------" + e);
        }

    }
    private void downloadWhatsappImage(DocumentFile srcFile) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                FileInputStream inStream = new FileInputStream(srcFile.getUri().getPath());

                String dst = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS
                        + File.separator + getContext().getString(R.string.app_name)
                        + File.separator + "WhatsApp"
                        + File.separator + "Images"
                        + File.separator + srcFile.getName();
                FileOutputStream outStream = new FileOutputStream(dst);
                FileChannel inChannel = inStream.getChannel();
                FileChannel outChannel = outStream.getChannel();

                inChannel.transferTo(0, inChannel.size(), outChannel);
                inStream.close();
                outStream.close();
            } catch (Exception e) {

                Log.d("ERROR_DOWNLOAD", " -------- ERROR  in download--------" + e);

                e.printStackTrace();
            }

        } else {

            ContentResolver resolver = getContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.MIME_TYPE, srcFile.getType());


            //            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Util.IMG_FOLDER);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                    + File.separator + getContext().getString(R.string.app_name)
                    + File.separator + "WhatsApp"
                    + File.separator + "Images");


            Uri target;//= resolver.insert(MediaStore.Files.getContentUri("external"), values);
            target = MediaStore.Downloads.EXTERNAL_CONTENT_URI;


            Uri uri = resolver.insert(target, values);

            try {
                InputStream in = resolver.openInputStream(srcFile.getUri());

                OutputStream out = resolver.openOutputStream(uri);

                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    Log.d("WA_LOG", "============= While Calles =============");

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


    }


    /*    private void downloadWhatsappImage(Uri srcFile) {



     */
    /* File source = new File(srcFile);
        File destination = new File(Util.IMG_FOLDER, "IMG_" + System.currentTimeMillis() + ".jpg");
        if (source.exists()) {
            FileChannel src = null;
            try {
                src = new FileInputStream(source).getChannel();

                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                src.close();
                dst.close();
            } catch (Exception e) {

                Log.d("WA_LOG", "============= Exception  =============" + e);

                e.printStackTrace();
            }
        }*/
    /*

        File file = new File(srcFile.getPath());
        Log.d("WA_LOG", "============= exists called  ============="+file.exists());
        Log.d("WA_LOG", "============= getPath called  ============="+file.getPath());
        Log.d("WA_LOG", "============= getAbsolutePath called  ============="+file.getAbsolutePath());

        if (file.exists()) {
            try {
                InputStream in = new FileInputStream(file);
                OutputStream out = null;
                Log.d("WA_LOG", "============= InputStream called  =============" + in.available());

                out = new FileOutputStream(Util.IMG_FOLDER + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    Log.d("WA_LOG", "============= While Calles =============");

                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                Log.d("WA_LOG", "============= Exception in FIle  =============" + e);

                e.printStackTrace();
            }
        }
    }
*/

    private void getImagesList() {

        Log.d("TAG_URI", "---------- uriString ----------" + uriString);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = DocumentFile.fromTreeUri(getContext(), Uri.parse(uriString));
            Log.d("URI_LOG", "---------- VERSION >= Q ----------" + uri.listFiles().length);

        } else {
            uri = DocumentFile.fromFile(IgLinkActivity.file);
            Log.d("URI_LOG", "---------- VERSION <Q ----------" + uri.listFiles().length);
        }

        if (uri != null) {
            files = uri.listFiles();
        }

        imageFiles = getImageFile(files);
        if (imageFiles.size() == 0) {

            pgImages.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            pgImages.setVisibility(View.GONE);
            tvStatus.setVisibility(View.GONE);
            rvImages.setAdapter(new ImageAdapter(getContext(), imageFiles));
        }

    }

    ArrayList<DocumentFile> getImageFile(DocumentFile[] files) {
        ArrayList<DocumentFile> documentFiles = new ArrayList<>();

        validExtension = new String[]{
                "jpg",
                "png",
                "gif",
                "jpeg",
                "webp"
        };

        for (DocumentFile file : files) {

            for (String type : validExtension) {

                if (file.getName().toLowerCase().endsWith(type)) {
                    documentFiles.add(file);
                    break;

                }
            }
        }

        return documentFiles;
    }

    private void initView() {

        rvImages = view.findViewById(R.id.rvVideo);
        rvImages.setLayoutManager(new GridLayoutManager(getContext(), 2));
        pgImages = view.findViewById(R.id.pgVideo);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnDownloadImages = view.findViewById(R.id.btnDownloadImages);

    }

}