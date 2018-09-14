package com.eleo95.reportapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.BuildConfig;
import com.eleo95.reportapp.R;
import com.eleo95.reportapp.interfaces.FragmentComunicator;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ReportsFragment extends Fragment implements View.OnClickListener{
    private ImageView photo;
    private ImageView camButton;
    private ImageView map;
    private Uri mImageUri;
    public String mCurrentPhotoPath;
    private EditText postTitle, postDescription;
    public static final String IMAGE_DIRECTORY_NAME = "ReportApp";
    private static final int REQUEST_CAMERA = 1, REQUEST_GALLERY = 2,PLACE_PICKER_REQUEST=3, REQUEST_LOCATION_PERMISSION=4;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String user_uid;
    private Task mUploadTask;
    private Button submitBtn,clearBtn;
    private FrameLayout locationBtn;
    private FragmentComunicator mFragmentComunicator;
    private String postLocation;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camButton = view.findViewById(R.id.camImgBtn);
        locationBtn = view.findViewById(R.id.post_location);
        submitBtn = view.findViewById(R.id.submit_button);
        clearBtn = view.findViewById(R.id.clear_button);
        postTitle = view.findViewById(R.id.post_title);
        postDescription = view.findViewById(R.id.post_description);
        map = view.findViewById(R.id.mapView);
        photo = view.findViewById(R.id.reportImage);

        camButton.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camImgBtn:
                imagePicker();
                break;
            case R.id.post_location:
                verifyLocationPermissions(getActivity());
                break;
            case R.id.submit_button:
                if(!postTitle.getText().toString().equals("") && !postDescription.getText().toString().equals("") && postLocation != null && mImageUri!=null){
                    mFragmentComunicator.uploadFile(mImageUri,
                            postTitle.getText().toString().trim(),
                            postDescription.getText().toString().trim(),
                            postLocation);
                    clearPost();

                }else{
                    Toast.makeText(getContext(), "Revisa los campos", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear_button:
                clearPost();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentComunicator = (FragmentComunicator) getActivity();
    }

    public void imagePicker(){
        AlertDialog.Builder pickerDialog = new AlertDialog.Builder(getContext());
        pickerDialog.setTitle("Select Action");
        String[] dialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pickerDialog.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        pickFromGallery();
                        break;
                    case 1:
                        dispatchTakePictureIntent();
                        break;
                }

            }
        });
        pickerDialog.show();
    }

    public void pickFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    if(getContext()!=null){
                        Place place = PlacePicker.getPlace(getContext(),data);
                        String placeName = String.format("Place: %s", place.getName());
                        String latitude = String.valueOf(place.getLatLng().latitude);
                        String longitude = String.valueOf(place.getLatLng().longitude);
                        try{
                            addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        postLocation = latitude+","+longitude;
                        //Toast.makeText(getContext(), "Place: "+placeName+"\nlat: "+latitude+"\nLon: "+longitude+"\nwhere: "+addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                        map.setVisibility(View.VISIBLE);

//                    Glide.with(getContext()).load(getMapUrl(latitude,longitude,"17","300","300","roadmap")).into(map);
                        Glide.with(this).load(getMapUrl(
                                latitude,
                                longitude,
                                "17",
                                "300",
                                "300",
                                "roadmap")
                        ).into(map);
                        if(addresses!=null){
                            postDescription.append("\n\n"+placeName+"\n"+addresses.get(0).getLocality()+"\n"+addresses.get(0).getCountryName());

                        }

                    }
                    break;

                case REQUEST_GALLERY:
                    if(data != null && data.getData() != null) {
                        mImageUri = data.getData();
                        photo.setVisibility(View.VISIBLE);
                        Glide.with(this).load(mImageUri).into(photo);
                    }
                    break;

            }
        }


    }


    private File createImageFile(){
        File mediaStorageDir =
//                new File(
//                Environment.getExternalStoragePublicDirectory(Objects.requireNonNull(getContext()).getFilesDir().getAbsolutePath()),
//                IMAGE_DIRECTORY_NAME);
                new File(
                        Environment.getExternalStorageDirectory(),
                        IMAGE_DIRECTORY_NAME);


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_hh:mm:ss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        try {
            mediaFile = File.createTempFile(
                    "IMG_" + timeStamp,
                    ".jpg",
                    mediaStorageDir
            );
        } catch (Exception e) {
            Log.e("Camera", "" + e);
            mediaFile = new File(mediaStorageDir + File.separator
                    , "IMG_" + timeStamp + ".jpg");
        }
        mCurrentPhotoPath = mediaFile.getAbsolutePath();
        return mediaFile;

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getContext()!= null && takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile;

            photoFile = createImageFile();



            if (photoFile != null) {

                Toast.makeText(getContext(), "Creado!!", Toast.LENGTH_SHORT).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, urifrom(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                mImageUri = urifrom(photoFile);
                Glide.with(this).load(mImageUri).into(photo);
                photo.setVisibility(View.VISIBLE);

            }
        }
    }

    public Uri urifrom(File mediaFile){
        if (getContext() != null){
            return FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    mediaFile);
        }
        Toast.makeText(getContext(), "error restart the app", Toast.LENGTH_SHORT).show();
        return Uri.parse("");
    }

    private void clearPost(){
        postTitle.setText("");
        postDescription.setText("");
        postLocation=null;
        photo.setVisibility(View.GONE);
        map.setVisibility(View.GONE);

    }


    public void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            requestPermissions(
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION_PERMISSION
            );
        }else{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                // for activty
                if(getActivity()!=null){
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }

                // for fragment
                //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }


    public String getMapUrl(String latitude, String longitude,String zoom, String width, String height, String mapType){
        return "https://maps.googleapis.com/maps/api/staticmap?center="
                +latitude+","+longitude+"&scale=2&markers=|color:0x0589E1|"
                +latitude+","+longitude+"&zoom="+zoom+"&size="+width+"x"
                +height+"&maptype="+mapType;

    }
}
