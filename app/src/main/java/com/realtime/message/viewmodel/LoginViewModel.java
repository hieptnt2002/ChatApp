package com.realtime.message.viewmodel;


import static com.realtime.message.constant.Constant.REQUEST_GOOGLE;

import android.app.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.realtime.message.BR;
import com.realtime.message.R;
import com.realtime.message.model.User;
import com.realtime.message.util.CustomProgress;
import com.realtime.message.view.activity.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends BaseObservable {
    String email = "";
    String password = "";
    public ObservableField<String> messageFailed = new ObservableField<>();
    public ObservableBoolean isLogin = new ObservableBoolean();
    public ObservableBoolean isVisibility = new ObservableBoolean();
    Activity mActivity;
    FirebaseAuth auth;
    DatabaseReference refUser;
    public static final String TAG = LoginActivity.class.getName();

//    GoogleSignInClient mGoogleSignInClient;

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    CustomProgress progressDialog;

    public LoginViewModel(Activity mActivity) {
        this.mActivity = mActivity;
        auth = FirebaseAuth.getInstance();
        refUser = FirebaseDatabase.getInstance().getReference("user");
        progressDialog = new CustomProgress(mActivity);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public ObservableBoolean getIsLogin() {
        return isLogin;
    }

    public void onClickLogin() {

//        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressDialog.show();
        FirebaseAuth.getInstance().signOut();
        User user = new User(email, password);
        if (email.isEmpty() || password.isEmpty() || email == null || password == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageFailed.set("Email or Password empty");
                    isLogin.set(false);
                    isVisibility.set(false);
                    progressDialog.dismiss();
                }
            }, 1500);
        } else if (!user.isValidEmail() || !user.isValidPassword()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageFailed.set("Email or Password invalid");
                    isLogin.set(false);
                    isVisibility.set(false);
                    progressDialog.dismiss();
                }
            }, 1500);
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        isLogin.set(true);
                        isVisibility.set(true);
                    } else {
                        messageFailed.set("Email or Password invalid");
                        isLogin.set(false);
                        isVisibility.set(false);
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void onClickLoginWithGoogle() {

        progressDialog.show();

        oneTapClient = Identity.getSignInClient(mActivity);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(mActivity.getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(mActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            mActivity.startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQUEST_GOOGLE,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_GOOGLE) {
            progressDialog.dismiss();

            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    auth.signInWithCredential(firebaseCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser fuser = auth.getCurrentUser();

                                Map<String, Object> map = new HashMap<>();
                                map.put("uid", fuser.getUid());
                                map.put("avatar", fuser.getPhotoUrl().toString());
                                map.put("name", fuser.getDisplayName());
                                map.put("email", fuser.getEmail());
                                map.put("phone", "default");
                                map.put("status", "online");

                                refUser.child(fuser.getUid()).setValue(map);
                                isLogin.set(true);
                            } else
                                Toast.makeText(mActivity, "Error login with google", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
        Toast.makeText(mActivity, "gagaagag", Toast.LENGTH_SHORT).show();
    }
}
