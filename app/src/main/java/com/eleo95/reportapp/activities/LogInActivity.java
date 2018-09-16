package com.eleo95.reportapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eleo95.reportapp.R;
import com.eleo95.reportapp.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    // private boolean isLogged = false;
    private FirebaseAuth mAuth;
    private EditText TextMail;
    private EditText TextPass;
    private EditText dName, dEmail, dPasswrd;
    private ProgressBar loginProgress;

    private GoogleSignInClient mGoogleSignInClient;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.loginButton);
        TextMail = findViewById(R.id.login_mail);
        TextPass = findViewById(R.id.login_password);
        Button googleSignInButton = findViewById(R.id.googleButton);
        Button signUpButton = findViewById(R.id.signup_Button);
        loginProgress = findViewById(R.id.login_progress);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        verifyStoragePermissions(this);
        loginButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

    }

    private void registarUsuario(User user) {
        final String userName = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();

        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LogInActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //add username to user
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    //.setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/logmein-5db02.appspot.com/o/logo-user-png-6.png?alt=media&token=f86209b8-82e0-482c-b0a1-71771eb64acd"))
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdates);
                            }

                        } else {
                            //if collition detected
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LogInActivity.this, R.string.email_create_error,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LogInActivity.this, R.string.create_user_error,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });


    }

    private void simpleLogin() {
        String email = TextMail.getText().toString().trim();
        String password = TextPass.getText().toString().trim();


        if (editTextHasText(TextMail) && editTextHasText(TextPass)) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                loginProgress.setVisibility(View.GONE);
                                goToHome();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LogInActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();

                            }


                        }
                    });
        }

    }

    private void createUser() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_signup);
        dialog.show();
        Button createUserBtn = dialog.findViewById(R.id.create_user_btn);
        dName = dialog.findViewById(R.id.dialog_edittext_name);
        dEmail = dialog.findViewById(R.id.dialog_edittext_email);
        dPasswrd = dialog.findViewById(R.id.dialog_edittext_psswrd);
        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextHasText(dName) && editTextHasText(dEmail) && editTextHasText(dPasswrd)) {
                    User user = new User();
                    user.setEmail(dEmail.getText().toString().trim());
                    user.setPassword(dPasswrd.getText().toString().trim());
                    user.setName(dName.getText().toString().trim());
                    registarUsuario(user);
                    //Toast.makeText(LogInActivity.this, "done!!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                loginProgress.setVisibility(View.VISIBLE);
                simpleLogin();
                break;
            case R.id.googleButton:
                loginProgress.setVisibility(View.VISIBLE);
                googleSignIn();
                break;
            case R.id.signup_Button:
                createUser();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgress.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            goToHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private boolean editTextHasText(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            loginProgress.setVisibility(View.GONE);
            editText.setError(getString(R.string.edit_text_error));
            return false;
        }
        return true;
    }


    private void goToHome() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
