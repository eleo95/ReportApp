package com.eleo95.reportapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eleo95.reportapp.R;
import com.eleo95.reportapp.model.Upload;
import com.eleo95.reportapp.adapters.MyPostsAdapter;
import com.eleo95.reportapp.adapters.NewPostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView, mRecyclerViewHori;
    private NewPostsAdapter mImageAdapter;
    private MyPostsAdapter myPostadapter;
    private RelativeLayout myPostsLayout;
    private List<Upload> mUploads;
    private List<Upload> mUploadsHori;
    private ProgressBar mProgressCircler, mProgressCircleHori;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewHori = view.findViewById(R.id.horizontal_recycler_view);
        myPostsLayout = view.findViewById(R.id.my_posts_layout);
        mAuth = FirebaseAuth.getInstance();
        mProgressCircler = view.findViewById(R.id.progress_circle);
        mProgressCircleHori = view.findViewById(R.id.horizontal_recycler_progress_circle);

        DatabaseReference mDatabaseRefCurrUser = FirebaseDatabase.getInstance().getReference("reports/" + mAuth.getUid());
        DatabaseReference mDatabaseRefAllUsers = FirebaseDatabase.getInstance().getReference("reports/");

        mDatabaseRefCurrUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    myPostsLayout.setVisibility(View.GONE);
                } else {
                    //Toast.makeText(getContext(), dataSnapshot.getChildren(), Toast.LENGTH_SHORT).show();
                    myPostsLayout.setVisibility(View.VISIBLE);
                    mUploadsHori = new ArrayList<>();
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Upload upload = postSnapShot.getValue(Upload.class);
                        mUploadsHori.add(upload);
                    }
                    myPostadapter = new MyPostsAdapter(getContext(), mUploadsHori);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
                    linearLayoutManager.setStackFromEnd(true);
                    mRecyclerViewHori.setLayoutManager(linearLayoutManager);
                    mRecyclerViewHori.setAdapter(myPostadapter);

                    mProgressCircleHori.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseRefAllUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads = new ArrayList<>();
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapShot2 : postSnapShot.getChildren()) {

                        if (postSnapShot.getKey() != null) {

                            if (!postSnapShot.getKey().equals(mAuth.getUid())) {
                                Upload upload = postSnapShot2.getValue(Upload.class);
                                mUploads.add(upload);
                            }
                        } else {
                            Toast.makeText(getContext(), "Error veifique su conección y reinicia el app", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload o1, Upload o2) {
                        String val1 = o1.getmImageUrl().split("%")[1];
                        String val2 = o2.getmImageUrl().split("%")[1];
                        return (int) (Long.valueOf(val1.substring(2, val1.indexOf(".")))
                                - Long.valueOf(val2.substring(2, val1.indexOf("."))));
                    }
                });

                mImageAdapter = new NewPostsAdapter(getContext(), mUploads);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        true);
                linearLayoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(mImageAdapter);
                mProgressCircler.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircler.setVisibility(View.GONE);
            }
        });
    }
}
