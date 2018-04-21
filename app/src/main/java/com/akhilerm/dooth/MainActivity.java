package com.akhilerm.dooth;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    final int MY_PERMISSIONS_REQUEST_SEND_SMS=20;

    private RequestContext requestContext;
    private String name;
    private String email;
    private String account;
    private String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        /*requestContext = RequestContext.create(this);
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
        });*/
        ImageButton loginButton = findViewById(R.id.login_with_amazon);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AuthorizationManager.authorize(new AuthorizeRequest
                        .Builder(requestContext)
                        .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                        .build());*/
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users");

        /*databaseReference.child("pranavshenoy07").child("pending").addValueEventListener(new ValueEventListener() {
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
        });*/

        databaseReference.child("pranavshenoy07").child("pending").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> messageBody = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> message : messageBody.entrySet()){
                    Log.d(TAG + "Keys", message.getKey());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //requestContext.onResume();
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

    /*@Override
    protected void onStart(){ super.onStart();
        Scope[] scopes = { ProfileScope.profile(), ProfileScope.postalCode() };
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    *//* The user is signed in *//*
                } else {
                    *//* The user is not signed in *//*
                }
            }

            @Override
            public void onError(AuthError ae) {
                *//* The user is not signed in *//*
            }
        });
    }*/

    private boolean sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                } else {
                    //Permission Denied
                    Toast.makeText(MainActivity.this, "App does not have enough permissions", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
