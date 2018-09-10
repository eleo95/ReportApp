package com.eleo95.reportapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private boolean isLogged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
       if(!isLogged){

           Intent intent = new Intent(MainActivity.this,LogInActivity.class);
           startActivity(intent);
           finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
            isLogged = true;

        }else{
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        }
    }

}
