package com.eleo95.reportapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.BuildConfig;
import com.eleo95.reportapp.R;

import java.io.File;
import java.io.FileOutputStream;

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
                Toast.makeText(ReportDetailsActivity.this, R.string.please_wait, Toast.LENGTH_SHORT).show();
                sharePost();
            }
        });
    }

    private void infoDumper() {
        postTitle.setText(getParams.getStringExtra("title"));
        postDescription.setText(getParams.getStringExtra("description"));
        Glide.with(this).load(getParams.getStringExtra("imgUrl"))
                .into(postImage);
        Glide.with(this).load(getMapUrl(
                getParams.getStringExtra("location"))
        ).into(postLocation);


    }

    private String getMapUrl(String coordinates) {
        return "https://maps.googleapis.com/maps/api/staticmap?center="
                + coordinates + "&scale=2&markers=|color:0x0589E1|"
                + coordinates + "&zoom=17&size=300x300&maptype=roadmap";

    }


    private void sharePost() {
        Bitmap bitmap = getBitmapFromImageView(postImage);
        try {
            File file = new File(this.getExternalCacheDir(), "tempImage.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            fOut.flush();
            fOut.close();
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, postTitle.getText() + "\n" + postDescription.getText());
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, getString(R.string.share_image_via)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromImageView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cnvs = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(cnvs);
        } else {
            cnvs.drawColor(Color.WHITE);
        }
        view.draw(cnvs);
        return returnedBitmap;
    }
}
