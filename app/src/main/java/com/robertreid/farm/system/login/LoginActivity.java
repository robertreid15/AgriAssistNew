package com.robertreid.farm.system.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertreid.farm.system.Accelerometer;
import com.robertreid.farm.system.R;
import com.robertreid.farm.system.findusers.FindUserActivity;
import com.robertreid.farm.system.main.MainActivity;
import com.robertreid.farm.system.util.ConnectionHelper;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 12345;
    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth auth;
    private SignInButton signInBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkIfNetworkAvailable();

        signInBtn = findViewById(R.id.sign_in_btn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        auth = FirebaseAuth.getInstance();
        signInBtn.setOnClickListener(v -> signIn());

    }



    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }




    /*public void bRecyclerView_onClick(View v) {
        Intent i = new Intent(LoginActivity.this, FindUserActivity.class);
        startActivity(i);
    }

    public void bShake_onClick(View v) {
        Intent i = new Intent(LoginActivity.this, Accelerometer.class);
        startActivity(i);
    }*/

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("all_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Map<String, String> user = new HashMap<>();
                                    user.put("email", acct.getEmail());
                                    user.put("userpic", acct.getPhotoUrl().toString());
                                    user.put("name", acct.getDisplayName());
                                    userRef.setValue(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed auth", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);

    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            showToast(result.getStatus().toString());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    private void navigateToMain(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    private void checkIfNetworkAvailable(){
        if(ConnectionHelper.isNetworkAvailable(this)){
            navigateToMain();
        } else {
           Snackbar snackbar =
                   Snackbar.make(findViewById(R.id.login_layout),
                           "Network isn't available!", Snackbar.LENGTH_LONG)
                   .setAction("Retry", v -> checkIfNetworkAvailable());
           snackbar.show();

        }
    }
}

