package com.carsmart.driving;

import timber.log.Timber;

public class StateAnalyzing {

    static final float FLAG = 9.8f;

    static final float THRESHOLD = 0.4f;

    private int nSteady = 0;

    private int nStrongBand = 0;
    private int nWeakBand = 0;

    private float t_peak = 0f;
    private boolean isPeak = false;

    private float t_valley = FLAG - THRESHOLD;
    private boolean isValley = false;

    private long lastTime;

    private int nMove = 0;

    private int nShake = 0;

//    private int steps = 0;

    boolean isMove = false;

    public void setXYZ(float xx, float yy, float zz) {
        float a = acceleration(xx, yy, zz);

        if (a > FLAG + THRESHOLD) {
            t_peak = a > t_peak ? a : t_peak;
            isPeak = true;

            if (nSteady > 0) {
                nSteady--;
            }

        } else if (a < FLAG - THRESHOLD) {
            t_valley = a < t_valley ? a : t_valley;
            isValley = true;

            if (nSteady > 0) {
                nSteady--;
            }
        } else {
            if (nSteady < 600) {
                nSteady++;
            }
        }

        if (isPeak && isValley) {
            float dValue = t_peak - t_valley;
            if (dValue > 5) {
                nStrongBand++;
            } else {
                nWeakBand++;
            }
            isPeak = false;
            isValley = false;
            t_peak = 0f;
            t_valley = FLAG - THRESHOLD;
        }

        if (nSteady > 400) {
            isMove = false;
            nMove = 0;
            nShake = 0;
            addState(1);
        } else {
            long currentTime = System.currentTimeMillis();
            long intervalTime = currentTime - lastTime;

            if (intervalTime > 1000) {
                lastTime = currentTime;

//                Timber.d("StrongBand is %s , WeakBand is %s", nStrongBand, nWeakBand);

                if (nStrongBand > 0 && nStrongBand + nWeakBand <= 15 && nStrongBand <= nWeakBand + 1) {

                    if (nMove <= 4) {
                        nMove++;
                    }

                    if (isMove) {
                        if (nShake > 0) {
                            nShake--;
                        }
                    }

                    if (nMove > 4) {
                        isMove = true;
                        nShake = 0;
                    }

                } else {
                    if (nShake <= 4) {
                        nShake++;
                    }

                    if (nShake > 4) {
                        isMove = false;
                        nMove = 0;
                    }

                }

//                steps = nStrongBand;

                nStrongBand = 0;
                nWeakBand = 0;

                addState(isMove ? 2 : 3);

            }

        }

    }

    private float acceleration(float xx, float yy, float zz) {

        double pow_x = Math.pow(xx, 2);
        double pow_y = Math.pow(yy, 2);
        double pow_z = Math.pow(zz, 2);

        return (float) Math.sqrt(pow_x + pow_y + pow_z);
    }

    int mState;

    private void addState(int state) {

        if (mState != state) {
            mState = state;

            switch (state) {
                case 1:
                    Timber.d("device is Steady");
                    break;
                case 2:
                    Timber.d("device is Move");
                    break;
                case 3:
                    Timber.d("device is Shake");
                    break;
            }

            if (listener != null) {
                listener.onStateAnalyzing(state);
            }

        }

    }

    private OnStateAnalyzingListener listener;

    public void setListener(OnStateAnalyzingListener listener) {
        this.listener = listener;
    }

    public interface OnStateAnalyzingListener {
        void onStateAnalyzing(int state);
    }

}
