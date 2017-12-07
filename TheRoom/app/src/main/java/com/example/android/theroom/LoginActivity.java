package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private final int REQUEST_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInButton mGoogleLoginButton;
    private TextView mLoginStatusTextView;

    /**
     * Static method that returns intent used to start LoginActivity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginStatusTextView = (TextView) findViewById(R.id.login_status_text);
        mLoginStatusTextView.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleLoginButton = (SignInButton) findViewById(R.id.google_login_button);
        mGoogleLoginButton.setVisibility(View.GONE); // hide login button until auth status is checked
        mGoogleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_SIGN_IN);
                mGoogleLoginButton.setVisibility(View.GONE); // hide login button while signing in
                mLoginStatusTextView.setVisibility(View.VISIBLE);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser != null && account != null) {
            // if user is already signed in start MainActivity
            viewMainMenu();
        } else {
            // otherwise display login button
            mGoogleLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            mGoogleLoginButton.setVisibility(View.GONE);
            mLoginStatusTextView.setVisibility(View.VISIBLE);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed into google account successfully, now authenticate with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                Log.w(TAG, "signInResult:failed message=" + e.getMessage());
                StackTraceElement[] stackTrace = e.getStackTrace();
                for (StackTraceElement s : stackTrace) {
                    Log.w(TAG, s.toString());
                }
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                mLoginStatusTextView.setVisibility(View.GONE);
                mGoogleLoginButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Authenticate user with firebase after signing into google account
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, navigate to MainActivity
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            viewMainMenu();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            mLoginStatusTextView.setVisibility(View.GONE);
                            mGoogleLoginButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Navigate to MainActivity
     */
    private void viewMainMenu() {
        Intent i = MainActivity.newIntent(this);
        startActivity(i);
        finish(); // finish LoginActivity so user can't navigate back to it
    }

}
