package com.example.compass;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageView compassImage;
    TextView displayer;
    Float azimuth_angle;
    private SensorManager compassSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] accel_read;
    float[] magnetic_read;
    private float current_degree = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        displayer = findViewById(R.id.displayer);
        compassImage = findViewById(R.id.compassimage);
        compassSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    protected void onResume() {
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }
    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    private String getDirectionText(int degrees) {
        degrees = (degrees + 360) % 360;

        String direction = "";
        int relativeDegree;

        if (degrees >= 0 && degrees < 90) {
            relativeDegree = degrees;
            direction = relativeDegree + "° North East";

        } else if (degrees >= 90 && degrees < 180) {
            relativeDegree = degrees - 90;
            direction = relativeDegree + "° South East";

        } else if (degrees >= 180 && degrees < 270) {
            relativeDegree = degrees - 180;
            direction = relativeDegree + "° South West";

        } else {
            relativeDegree = degrees - 270;
            direction = relativeDegree + "° North West";
        }

        return direction;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accel_read = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetic_read = sensorEvent.values;
        if (accel_read != null && magnetic_read != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean successsful_read = SensorManager.getRotationMatrix(R, I, accel_read, magnetic_read);
            if (successsful_read) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth_angle = orientation[0];
                float degrees = ((azimuth_angle * 180f) / 3.14f);
                int degreesInt = Math.round(degrees);

                displayer.setText(getDirectionText(degreesInt));

                RotateAnimation rotate = new RotateAnimation(current_degree, -degreesInt,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setFillAfter(true);
                compassImage.startAnimation(rotate);
                current_degree = -degreesInt;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}