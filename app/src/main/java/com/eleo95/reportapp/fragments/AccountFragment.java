package com.eleo95.reportapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.activities.LogInActivity;
import com.eleo95.reportapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private TextView postCounter;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView iconLogo = view.findViewById(R.id.user_circleimageview);
        TextView userName = view.findViewById(R.id.textview_user);
        postCounter = view.findViewById(R.id.text_view_post_count);
        TextView userEmail = view.findViewById(R.id.text_view_email);
        Button logOutBtn = view.findViewById(R.id.logout_btn);
        mAuth = FirebaseAuth.getInstance();
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getContext(),"Signed Out",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);
                if(getActivity()!=null){
                    getActivity().finish();
                }

            }
        });

        DatabaseReference mDatabaseRefCurrUser = FirebaseDatabase.getInstance().getReference("reports/" + mAuth.getUid());
        mDatabaseRefCurrUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postCounter.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(mAuth.getCurrentUser()!=null) {
            userName.setText(mAuth.getCurrentUser().getDisplayName());
        }
        if(mAuth.getCurrentUser().getPhotoUrl()!=null) {
            Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(iconLogo);
        }
        userEmail.setText(mAuth.getCurrentUser().getEmail());


    }
}
