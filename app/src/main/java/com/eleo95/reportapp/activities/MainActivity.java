package com.eleo95.reportapp.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.R;
import com.eleo95.reportapp.Upload;
import com.eleo95.reportapp.fragments.AccountFragment;
import com.eleo95.reportapp.fragments.HomeFragment;
import com.eleo95.reportapp.fragments.ReportsFragment;
import com.eleo95.reportapp.interfaces.FragmentComunicator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import static com.eleo95.reportapp.myapplication.MyApplication.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity implements FragmentComunicator {
    private FirebaseAuth mAuth;
    final Fragment homeFrag = new HomeFragment();
    final Fragment reportFrag = new ReportsFragment();
    final Fragment accFrag = new AccountFragment();
    private Fragment selectedFragment = homeFrag;
    private ImageView userAvatar;
    public StorageReference mStorageRef;
    public DatabaseReference mDatabaseRef;
    public Task mUploadTask;


    public NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userAvatar = findViewById(R.id.photo);
        mNotificationManager = NotificationManagerCompat.from(this);


        if (currentUser != null) {
            if (currentUser.getPhotoUrl() != null) {
                userAvatar.setImageTintList(null);
                Glide.with(this).load(currentUser.getPhotoUrl()).into(userAvatar);
            }

            Toast.makeText(this, getString(R.string.welcome) + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, accFrag).hide(accFrag).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, reportFrag).hide(reportFrag).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFrag).commit();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            mStorageRef = FirebaseStorage.getInstance().getReference("reports");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("reports/" + currentUser.getUid());
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), userAvatar);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.about:
                                    // item one clicked
                                    aboutDialog();
                                    return true;
                                case R.id.salir:
                                    // item two clicked
                                    mAuth.signOut();
                                    Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.avatar_menu);
                    popupMenu.show();
                }
            });


        } else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(homeFrag).commit();
                            selectedFragment = homeFrag;
                            userAvatar.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_report:
                            getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(reportFrag).commit();
                            selectedFragment = reportFrag;
                            userAvatar.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_account:
                            getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(accFrag).commit();
                            selectedFragment = accFrag;
                            userAvatar.setVisibility(View.INVISIBLE);

                            break;
                    }


                    return true;
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    public void uploadFile(Uri imgUrl, final String title, final String description, final String location) {
        if (imgUrl != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imgUrl));

            showNotifUpload();
//            File file = new File(String.valueOf(imgUrl));
//            boolean isDeleted = file.getAbsoluteFile().delete();
//
//            if (isDeleted){
//                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
//            }


            mUploadTask = fileReference.putFile(imgUrl).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                    notification.setProgress(100, (int) progress, false);
                    mNotificationManager.notify(0, notification.build());

                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        //  Toast.makeText(MainActivity.this, "1111error", Toast.LENGTH_SHORT).show();
                        notification.setContentText(getString(R.string.report_upload_complete));
                        notification.setProgress(0, 0, false);
                        mNotificationManager.notify(0, notification.build());

                        //Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        Upload upload = new Upload(title,
                                downloadUri.toString(), description, location, "");
                        //Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                        String uploadId = mDatabaseRef.push().getKey();
                        upload.setmKey(uploadId);

                        if (uploadId != null) {
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }



                    } else {
                        Toast.makeText(MainActivity.this, "upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });


        } else {
            Toast.makeText(MainActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = MainActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    public void showNotifUpload() {
        notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_report_24dp)
                .setContentTitle(getString(R.string.Report_upload))
                .setContentText(getString(R.string.report_upload_progress))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setProgress(100, 0, false);
        mNotificationManager.notify(0, notification.build());
    }

    public void aboutDialog(){
        final AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
        aDialog.setTitle(R.string.about);
        aDialog.setMessage(R.string.aboutTxt);
        aDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog diag = aDialog.show();
        TextView DialogMssg = diag.findViewById(android.R.id.message);
        TextView DialogTitle = diag.findViewById(android.R.id.title);
        Typeface typeface = ResourcesCompat.getFont(this,R.font.productsansregular);
        if (DialogTitle != null && DialogMssg != null) {
            DialogTitle.setTypeface(typeface);
            DialogMssg.setTypeface(typeface);
            DialogMssg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        aDialog.create();

    }
}
