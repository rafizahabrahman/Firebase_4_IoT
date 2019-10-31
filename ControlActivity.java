package com.pms.fizar.myiotprojectfizar;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.pusher.pushnotifications.PushNotifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class ControlActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Switch sw1;
    TextView tvSwitchStatus;
    RadioGroup rg;
    ToggleButton tbLed2;


    DatabaseReference led1 = myRef.child("LED1").child("status");
    DatabaseReference led2 = myRef.child("LED2").child("status");
    DatabaseReference led3 = myRef.child("LED3").child("status");
    DatabaseReference switchStatus = myRef.child("PushButton").child("status");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        sw1 = findViewById(R.id.switch1);
        tbLed2 = findViewById(R.id.tbLed2);
        tvSwitchStatus = findViewById(R.id.tvSwitchStatus);
        rg = findViewById(R.id.radioGroup1);

        getCurrentToken();
        //subscribeToTopic();  no longer used

        PushNotifications.start(getApplicationContext(), "44cc4074-42d6-4dac-a7ea-f1943df588d1");
        PushNotifications.addDeviceInterest("hello");


        //Retrieves Push button state from Firebase continously
        switchStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("file", "Value push button is: " + value);
                tvSwitchStatus.setText(value);

                if(value=="HIGH") {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("file", "Failed to read push button", databaseError.toException());
            }
        });

        // Retrieve LED states from firebase each time app is launched. Firebase will be read only once.
        led1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("file", "Value  led1 is: " + value);
                if (value.equals("ON")) {
                    sw1.setChecked(true);
                } else {
                    sw1.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("file", "Failed to read led1", databaseError.toException());
            }
        });

        led2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("file", "Value led2 is: " + value);
                if (value.equals("ON")) {
                    tbLed2.setChecked(true);
                } else {
                    tbLed2.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("file", "Failed to read led2", databaseError.toException());
            }
        });

        led3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("file", "Value led3 is: " + value);
                if (value.equals("ON")) {
                    rg.check(R.id.radioOn);
                    Log.d("file", "on");
                } else {
                    rg.check(R.id.radioOff);
                    Log.d("file", "off");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("file", "Failed to read led3", databaseError.toException());
            }
        });


        // Change LED states in the firebase
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //switch enable
                    led1.setValue("ON");
                } else {
                    //switch disable
                    led1.setValue("OFF");
                }
            }
        });

        tbLed2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //switch enable
                    led2.setValue("ON");
                } else {
                    //switch disable
                    led2.setValue("OFF");
                }
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {
                switch (isChecked) {
                    case R.id.radioOn:
                        led3.setValue("ON");
                        break;
                    case R.id.radioOff:
                        led3.setValue("OFF");
                        break;
                }
            }
        });
    }

   /* private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("SampleTopic")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String msg = task.getException().getMessage();
                            Toast.makeText(ControlActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/

    private void getCurrentToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(">>>>>>>", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.d("Current Token " + ">>>>>>>>>", msg);
                    }
                });
    }
}
