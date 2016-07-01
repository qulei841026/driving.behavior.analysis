package com.carsmart.driving;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 碰撞传感器
 *
 * @author qulei
 */
public class SensorProvider {

    private final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;

    Context mContext;

    private SensorManager mSensorManager;
    private OnSensorListener sensorListener;
    private OnVelocityListener velocityListener;
    private StateAnalyzing.OnStateAnalyzingListener stateAnalyzingListener;

    private static final float HIGH_ALPHA = 0.8f;
    private float[] gravity = new float[3];
    private SensorHelper mSensorHelper;

    FilterAlgorithm filterAlgorithm;

    StateAnalyzing stateAnalyzing;

    private int axis;

    public SensorProvider(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void init() {
        axis = Integer.valueOf(Utils.getString(mContext, "axis", "4"));
        filterAlgorithm = new FilterAlgorithm(mContext);
        mSensorHelper = new SensorHelper(mContext);
        mSensorHelper.setOnVelocityListener(velocityListener);

        stateAnalyzing = new StateAnalyzing();
        stateAnalyzing.setListener(stateAnalyzingListener);

        mSensorManager.registerListener(sensorEventListener, mSensorManager
                .getDefaultSensor(SENSOR_TYPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void destroy() {
        mSensorManager.unregisterListener(sensorEventListener);
        filterAlgorithm = null;
        mSensorHelper = null;
    }

    private float[] filter(float[] input) {
        float[] output = new float[3];
        float x = input[0];
        float y = input[1];
        float z = input[2];

        gravity[0] = HIGH_ALPHA * gravity[0] + (1 - HIGH_ALPHA) * x;
        gravity[1] = HIGH_ALPHA * gravity[1] + (1 - HIGH_ALPHA) * y;
        gravity[2] = HIGH_ALPHA * gravity[2] + (1 - HIGH_ALPHA) * z;

        output[0] = x - gravity[0];
        output[1] = y - gravity[1];
        output[2] = z - gravity[2];

        return output;
    }

    private float acceleration(float xx, float yy, float zz) {
        if (axis == 1)
            return xx;
        if (axis == 2)
            return yy;
        if (axis == 3)
            return zz;

        double pow_x = Math.pow(xx, 2);
        double pow_y = Math.pow(yy, 2);
        double pow_z = Math.pow(zz, 2);

        if (axis == 4)
            return (float) Math.sqrt(pow_x + pow_y);
        if (axis == 5)
            return (float) Math.sqrt(pow_y + pow_z);
        if (axis == 6)
            return (float) Math.sqrt(pow_x + pow_z);

        return (float) Math.sqrt(pow_x + pow_y + pow_z);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (SENSOR_TYPE == sensorEvent.sensor.getType()) {
                float[] values = sensorEvent.values.clone();

                stateAnalyzing.setXYZ(values[0], values[1], values[2]);

                if (sensorListener != null) {
                    sensorListener.onSensorOriginal(values[0], values[1], values[2]);
                }

                values = filter(values);

                if (sensorListener != null) {
                    sensorListener.onSensorDenoise(values[0], values[1], values[2]);
                }

                float acceleration = acceleration(values[0], values[1], values[2]);

                mSensorHelper.setAcceleration(acceleration);

//                delay();    //延迟

                if (sensorListener != null) {
                    float a = filterAlgorithm.averageFilter(acceleration);
                    if (a != -1f) {
                        sensorListener.onSensorAcceleration(a);
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void setSensorListener(OnSensorListener listener) {
        this.sensorListener = listener;
    }

    public void setVelocityListener(OnVelocityListener listener) {
        this.velocityListener = listener;
    }

    public void setStateAnalyzingListener(StateAnalyzing.OnStateAnalyzingListener listener) {
        this.stateAnalyzingListener = listener;
    }

    public interface OnSensorListener {

        void onSensorOriginal(float x, float y, float z);

        void onSensorDenoise(float x, float y, float z);

        void onSensorAcceleration(float acceleration);
    }

    private long lastTime;

    private void delay() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - lastTime;
        if (intervalTime < 10)
            return;
        lastTime = currentTime;
    }

}
