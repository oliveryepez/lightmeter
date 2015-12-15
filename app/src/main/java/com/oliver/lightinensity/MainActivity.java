package com.oliver.lightinensity;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView txtCurrentValue;
    private TextView txtMaxValue;
    private DonutProgress donutProgress;
    private FitChart lightProgressBar;
    private float maxValue;
    private String TAG = "LightSensorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        lightProgressBar = (FitChart) findViewById(R.id.light_progress_bar);
        txtMaxValue = (TextView) findViewById(R.id.txt_max_value);
        txtCurrentValue = (TextView) findViewById(R.id.txt_current_value);


        this.setTypefaceFont(txtMaxValue, "lato-regular");
        this.setTypefaceFont(txtCurrentValue, "lato-regular");

        if(lightSensor == null){
            Toast.makeText(this, "There is no Light Sensor", Toast.LENGTH_LONG).show();
        }else{
            maxValue = lightSensor.getMaximumRange();
            lightProgressBar.setMinValue(0f);
            lightProgressBar.setMaxValue(maxValue);

            txtMaxValue.setText("Max Reading: " + maxValue + " Lux");

            Log.d(TAG, "max_value: " + maxValue);
            sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.sensor.getType() ==  Sensor.TYPE_LIGHT){
                float currentReading = event.values[0];
                lightProgressBar.setValues(addChartValues(maxValue, currentReading));
                txtCurrentValue.setText(currentReading + " Lux");
                Log.d(TAG, "current_value: " + currentReading);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener((lightSensorEventListener));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private Collection<FitChartValue> addChartValues(float maxValue, float currentValue){

        Collection<FitChartValue> values = new ArrayList<>();

       if((currentValue > 0) && currentValue <= (maxValue * 0.20))
           values.add(new FitChartValue(currentValue, getResources().getColor(R.color.progressTwentyPercent)));

        if(currentValue >= (maxValue * 0.20) && (currentValue <= (maxValue * 0.40))){
            values.add(new FitChartValue((float)(maxValue * 0.20), getResources().getColor(R.color.progressTwentyPercent)));
            values.add(new FitChartValue(currentValue, getResources().getColor(R.color.progressFourtyPercent)));
        }

        if((currentValue >= (maxValue * 0.40)) && (currentValue <= (maxValue * 0.60))){
            values.add(new FitChartValue((float)(maxValue * 0.20), getResources().getColor(R.color.progressTwentyPercent)));
            values.add(new FitChartValue((float)(maxValue * 0.40), getResources().getColor(R.color.progressFourtyPercent)));
            values.add(new FitChartValue(currentValue, getResources().getColor(R.color.progressSixtyPercent)));
        }

        if(currentValue >= (maxValue * 0.60) && (currentValue <= maxValue)){
            values.add(new FitChartValue((float)(maxValue * 0.20), getResources().getColor(R.color.progressTwentyPercent)));
            values.add(new FitChartValue((float)(maxValue * 0.40), getResources().getColor(R.color.progressFourtyPercent)));
            values.add(new FitChartValue((float)(maxValue * 0.60), getResources().getColor(R.color.progressSixtyPercent)));
            values.add(new FitChartValue(maxValue, getResources().getColor(R.color.progressHundredPercent)));
        }

        return values;

    }

    private void setTypefaceFont(TextView controlView, String font) {

        String fontPath = "fonts/Lato-Regular.ttf";
        Typeface typeface;
        typeface = Typeface.createFromAsset(getAssets(), fontPath);
        controlView.setTypeface(typeface);
    }
}
