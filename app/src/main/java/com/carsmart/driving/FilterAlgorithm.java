package com.carsmart.driving;

import android.content.Context;

public class FilterAlgorithm {

    private int nAverage;
    private float average;
    private int filter;

    public FilterAlgorithm(Context context) {
        filter = Integer.valueOf(Utils.getString(context, "filter", "3"));
    }

    public float averageFilter(float value) {
        if (filter == 1)
            return value;

        if (nAverage < filter) {
            average = (average + value) / 2;
            nAverage++;
            return -1f;
        } else {
            nAverage = 0;
            float result = average;
            average = 0;
            return result;
        }

    }

    private float limitTemp;

    public float limitFilter(float value) {
        if (value <= 0.2) {
            return 0;
        }

        if (Math.abs(limitTemp - value) > 2) {
            return limitTemp;
        }
        return limitTemp = value;
    }

}



