package com.eleo95.reportapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eleo95.reportapp.R;
import com.eleo95.reportapp.fragments.AccountFragment;
import com.eleo95.reportapp.fragments.HomeFragment;
import com.eleo95.reportapp.fragments.ReportsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    final Fragment homeFrag = new HomeFragment();
    final Fragment reportFrag = new ReportsFragment();
    final Fragment accFrag = new AccountFragment();
    private Fragment selectedFragment = homeFrag;
    private ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userAvatar = findViewById(R.id.photo);


        if(currentUser != null){
            if(currentUser.getPhotoUrl()!=null){
                userAvatar.setImageTintList(null);
                Glide.with(this).load(currentUser.getPhotoUrl()).into(userAvatar);
            }

            Toast.makeText(this, "Welcome "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, accFrag).hide(accFrag).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, reportFrag).hide(reportFrag).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFrag).commit();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
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
                                return true;
                            case R.id.salir:
                                // item two clicked
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
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



        }else{
            Intent intent = new Intent(MainActivity.this,LogInActivity.class);
            startActivity(intent);
            finish();
        }


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch(item.getItemId()){
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

}
