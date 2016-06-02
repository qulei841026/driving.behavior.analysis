package com.carsmart.driving;

import android.content.Context;

public class SensorHelper {

    private FilterAlgorithm filterAlgorithm;

    private long last;
    private long delayMillis;

    private float[] velocity = new float[2];
    private float[] acceleration = new float[2];

    private OnVelocityListener listener;

    public SensorHelper(Context context) {
        filterAlgorithm = new FilterAlgorithm(context);
        delayMillis = Long.valueOf(Utils.getString(context, "delay_millis", "200"));
    }

    public void setOnVelocityListener(OnVelocityListener listener) {
        this.listener = listener;
    }

    public void setAcceleration(float value) {
        long time = System.currentTimeMillis();

        float a = filterAlgorithm.averageFilter(value);
        if (a == -1f) {
            return;
        }

        if ((time - last) < delayMillis) {
            acceleration[1] = a;
            velocity[1] = velocity[0] + acceleration[0] + ((acceleration[1] - acceleration[0]) / 2);
            acceleration[0] = acceleration[1];
            velocity[0] = velocity[1];
            return;
        }

        if (listener != null) {
            listener.onChanged(velocity[0]);
        }

        velocity = new float[2];
        acceleration = new float[2];
        last = time;
    }

}

