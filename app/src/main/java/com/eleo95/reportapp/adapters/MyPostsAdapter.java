package com.eleo95.reportapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleo95.reportapp.R;
import com.eleo95.reportapp.Upload;
import com.eleo95.reportapp.activities.ReportDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;


    public MyPostsAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_structure_square, parent, false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        final Upload uploadCurrent = mUploads.get(pos);


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


                showResults(uploadCurrent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.dialogTheme);
                dialog.setTitle(R.string.delete_report);
                dialog.setMessage(R.string.delete_report_ask);
                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if (mAuth.getCurrentUser() != null) {
                            FirebaseDatabase.getInstance().getReference("reports/" + mAuth.getCurrentUser().getUid())
                                    .child(uploadCurrent.getmKey()).removeValue();
                            FirebaseStorage.getInstance().getReferenceFromUrl(uploadCurrent.getmImageUrl()).delete();
                        }


                    }
                });
                dialog.setNegativeButton("No", null);
                dialog.create();
                dialog.show();

                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        private ImageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_upload);

        }
    }

    private void showResults(Upload currentPost) {
        Intent intent = new Intent(mContext, ReportDetailsActivity.class);
        intent.putExtra("title", currentPost.getmTitle());
        intent.putExtra("imgUrl", currentPost.getmImageUrl());
        intent.putExtra("description", currentPost.getmDescription());
        intent.putExtra("location", currentPost.getmLocation());
        mContext.startActivity(intent);

    }

}
