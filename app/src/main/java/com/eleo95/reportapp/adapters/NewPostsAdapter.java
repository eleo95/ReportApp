package com.eleo95.reportapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleo95.reportapp.R;
import com.eleo95.reportapp.model.Upload;
import com.eleo95.reportapp.activities.ReportDetailsActivity;


import java.util.List;

public class NewPostsAdapter extends RecyclerView.Adapter<NewPostsAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

    public NewPostsAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_structure, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewTitle.setText(uploadCurrent.getmTitle());
        holder.textViewDescription.setText(uploadCurrent.getmDescription());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(new ColorDrawable(Color.WHITE));
        Glide.with(mContext)
                .load(uploadCurrent.getmImageUrl())
                .apply(options)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mUploads.get(pos).getmTitle();
                String description = mUploads.get(pos).getmDescription();
                String imgUrl = mUploads.get(pos).getmImageUrl();
                String location = mUploads.get(pos).getmLocation();
                showResults(title, imgUrl, description, location);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle, textViewDescription;
        public ImageView imageView;

        private ImageViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_post_title);
            textViewDescription = itemView.findViewById(R.id.post_description);
            imageView = itemView.findViewById(R.id.image_view_upload);

        }
    }

    private void showResults(String title, String imgUrl, String description, String location) {
        Intent intent = new Intent(mContext, ReportDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("imgUrl", imgUrl);
        intent.putExtra("description", description);
        intent.putExtra("location", location);
        mContext.startActivity(intent);

    }
}
