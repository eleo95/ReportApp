package com.eleo95.reportapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.R;

public class ReportDetailsActivity extends AppCompatActivity {
    private ImageView postImage;
    private ImageView postLocation;
    private TextView postTitle, postDescription;
    private Intent getParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        postTitle = findViewById(R.id.display_post_title);
        postImage = findViewById(R.id.display_post_image);
        postDescription = findViewById(R.id.display_post_description);
        postLocation = findViewById(R.id.display_post_location_img);
        ImageView shareIcon = findViewById(R.id.share_icon);
        getParams = getIntent();
        infoDumper();
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePost();
            }
        });
    }

    private void infoDumper(){
        postTitle.setText(getParams.getStringExtra("title"));
        postDescription.setText(getParams.getStringExtra("description"));
        Glide.with(this).load(getParams.getStringExtra("imgUrl"))
                .into(postImage);
        Glide.with(this).load(getMapUrl(
                getParams.getStringExtra("location"))
        ).into(postLocation);


    }

    private String getMapUrl(String coordinates){
        return "https://maps.googleapis.com/maps/api/staticmap?center="
                +coordinates+"&scale=2&markers=|color:0x0589E1|"
                +coordinates+"&zoom=17&size=300x300&maptype=roadmap";

    }

    private void sharePost(){

    }
}
