package com.saver.storysaver.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saver.storysaver.R;
import com.saver.storysaver.view.ViewImageActivity;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context context;
    ArrayList<DocumentFile> files;
    List<String> imgUri;

    Boolean isFile;


    public ImageAdapter(Context context, ArrayList<DocumentFile> files) {

        this.context = context;
        this.files = files;

        isFile = true;
    }

    public ImageAdapter(Context context, List<String> imgUrlList) {
        this.context = context;
        this.imgUri = imgUrlList;
        isFile = false;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(files[position].getUri()));

        if (!isFile) {
            Glide.with(context).load(imgUri.get(position)).into(holder.imageView);
        } else {
            Glide.with(context).load(files.get(position).getUri()).into(holder.imageView);
        }


//        Log.d("URI_LOG_ADAPTER", "---------- Uri list ----------" + files.get(holder.getAdapterPosition()).getUri());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ViewImageActivity.class)
                        .putExtra("URI", isFile ? files.get(holder.getBindingAdapterPosition()).getUri().toString()
                                : imgUri.get(holder.getBindingAdapterPosition())));
            }
        });

//        holder.imageView.setImageURI(files.get(position).getUri());

    }

    @Override
    public int getItemCount() {
        if(isFile)
        return files.size();
        else
            return imgUri.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgStatus);
        }
    }
}
