package com.robertreid.farm.system;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Accelerometer extends AppCompatActivity {

    private SensorManager sm;

    private float acelVal;      //Current accelereation value and gravity
    private float acelLast;     //Last accelereation value and gravity
    private float shake;

    private static final int FORCE_THRESHOLD = 10000;   //shake intensity
    private static final int TIME_THRESHOLD = 75;       //only allow one update between every 75ms
    private static final int SHAKE_TIMEOUT = 500;       //time between each shake
    private static final int SHAKE_DURATION = 150;      //how long shake is present


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {


            /*float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;*/

            if(sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
            long now = System.currentTimeMillis();

            /*if((now - previousForce) > SHAKE_TIMEOUT) {
                int mShakeCount = 0;
            }*/


            if(shake > 12){
                /*Toast toast = Toast.makeText(getApplicationContext(), "Do Not Shake Me", Toast.LENGTH_SHORT);
                toast.show();*/
                sendNotification();

            }

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void sendNotification() {
        String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("notifications").child(pushId).child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).setValue(true);

    }
}

