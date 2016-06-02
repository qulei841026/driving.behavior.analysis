package com.carsmart.driving;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test1() {

        float result = acceleration(-2.143692f, 4.389755f, -5.008575f);
        Log.d("qulei", "result : " + result);
        assertTrue(result != 0);

    }


    private float acceleration(float x, float y, float z) {
        double pow_x = Math.pow(x, 2);
        double pow_y = Math.pow(y, 2);
        double pow_z = Math.pow(z, 2);
        return (float) Math.sqrt(pow_x + pow_y + pow_z);
    }
}