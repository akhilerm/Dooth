package com.akhilerm.dooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private RequestContext requestContext;
    private String name;
    private String email;
    private String account;
    private String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestContext = RequestContext.create(this);
        requestContext.registerListener(new AuthorizeListener() {
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {
                fetchUserProfile();
            }

            @Override
            public void onError(AuthError authError) {
                Toast.makeText(MainActivity.this, "Auth Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(AuthCancellation authCancellation) {
                Toast.makeText(MainActivity.this, "Auth Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton loginButton = findViewById(R.id.login_with_amazon);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthorizationManager.authorize(new AuthorizeRequest
                        .Builder(requestContext)
                        .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                        .build());
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users");
        databaseReference.child("users").child("pranavshenoy06gmailcom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestContext.onResume();
    }

    private void fetchUserProfile() {
        User.fetch(this, new Listener<User,AuthError>() {

            /* fetch completed successfully. */
            @Override
            public void onSuccess(User user) {
                final String name = user.getUserName();
                final String email = user.getUserEmail();
                final String account = user.getUserId();
                final String zipcode = user.getUserPostalCode();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProfileData(name, email, account, zipcode);
                    }
                });
            }

            /* There was an error during the attempt to get the profile. */
            @Override
            public void onError(AuthError ae) {
                /* Retry or inform the user of the error */
            }
        });
    }

    private synchronized void updateProfileData(String name, String email, String account, String zipcode) {
        this.name=name;
        this.email=email;
        this.account=account;
        this.zipcode=zipcode;
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart(){ super.onStart();
        Scope[] scopes = { ProfileScope.profile(), ProfileScope.postalCode() };
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    /* The user is signed in */
                } else {
                    /* The user is not signed in */
                }
            }

            @Override
            public void onError(AuthError ae) {
                /* The user is not signed in */
            }
        });
    }


}
