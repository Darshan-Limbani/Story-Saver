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
import com.saver.storysaver.utils.Util;
import com.saver.storysaver.view.ViewVideoActivity;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    Context context;
    ArrayList<DocumentFile> files;
    List<String> vidUri;
    Boolean isFile;

    public VideoAdapter(Context context, ArrayList<DocumentFile> files) {

        this.context = context;
        this.files = files;
        isFile = true;
    }

    public VideoAdapter(Context context, List<String> vidUrlList) {
        this.context = context;
        this.vidUri = vidUrlList;
        isFile = false;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        if (isFile) {

            Glide.with(context).load(files.get(position).getUri()).into(holder.imageView);
        } else {
            Glide.with(context).load(vidUri.get(position)).into(holder.imageView);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isFile) {

                    Util.file = files.get(holder.getBindingAdapterPosition());
                    context.startActivity(new Intent(context, ViewVideoActivity.class)
                            .putExtra("isFile", isFile));

                } else {
                    context.startActivity(new Intent(context, ViewVideoActivity.class)
                            .putExtra("url",
//                                isFile ? files.get(holder.getBindingAdapterPosition()).getUri().toString():
                                    vidUri.get(holder.getBindingAdapterPosition())
                            ).putExtra("isFile", isFile));
                }


            }
        });

    }

    @Override
    public int getItemCount() {


        return isFile ? files.size() : vidUri.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgStatus);
        }
    }
}
