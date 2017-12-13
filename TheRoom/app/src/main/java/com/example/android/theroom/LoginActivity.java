package com.example.android.theroom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private TextView mLoginStatusTextView;
    private LoginButton mLoginButton;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

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

        // Initialize facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();

        // Hide signing in message until we check user's login status
        mLoginStatusTextView = (TextView) findViewById(R.id.login_status_text);
        mLoginStatusTextView.setVisibility(View.GONE);

        // Hide login button until we check user's login status
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("email");
        mLoginButton.setVisibility(View.GONE);

        // User is logged in to facebook if AccessToken is not null
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            // User is logged in, just authenticate with firebase
            firebaseAuthWithFacebook(accessToken);
        } else {
            // show login button
            mLoginButton.setVisibility(View.VISIBLE);

            // handle callback for facebook sign in
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // Success, authenticate with firebase
                            firebaseAuthWithFacebook(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            // User cancelled login, display message and login button
                            Toast.makeText(LoginActivity.this, "Login Cancelled.", Toast.LENGTH_SHORT).show();
                            mLoginStatusTextView.setVisibility(View.GONE);
                            mLoginButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // User cancelled login, display message and login button
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            mLoginStatusTextView.setVisibility(View.GONE);
                            mLoginButton.setVisibility(View.VISIBLE);
                        }
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Let facebook's CallbackManager handle the login result
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Authenticate user with firebase after successful facebook login
     * @param token
     */
    private void firebaseAuthWithFacebook(AccessToken token) {
        mLoginButton.setVisibility(View.GONE);
        mLoginStatusTextView.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authentication success, navigate to MainActivity
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            viewMainMenu();
                        } else {
                            // If authentication fails, display a message to the user, and redisplay login button
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut(); // since authentication failed sign out of facebook
                            mLoginStatusTextView.setVisibility(View.GONE);
                            mLoginButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Navigate to MainActivity or Onboarding if this is the user's first time using app
     */
    private void viewMainMenu() {
        // check shared preferences to determine whether onboarding should be skipped or not
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean skipOnboarding = sharedPref.getBoolean(OnboardingActivity.SKIP_ONBOARDING, false);
        Log.d(TAG, "SkipOnboarding: " + skipOnboarding);
        Intent i = skipOnboarding ? MainActivity.newIntent(this) : OnboardingActivity.newIntent(this);
        startActivity(i);
        finish(); // finish LoginActivity so user can't navigate back to it
    }

}
