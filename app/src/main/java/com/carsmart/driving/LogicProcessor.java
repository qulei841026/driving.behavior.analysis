package com.carsmart.driving;

public class LogicProcessor {

    protected static final int MAX = 3;
    protected int fix = 0;
    protected float temp = 0;
    protected int status = 0;   //0,1,2

    private OnLogicProcessorListener listener;

    public void setVelocity(float velocity) {
        if (velocity - temp > 0.5) {
            if (status == 1) {
                fix++;
            } else {
                fix = 1;
            }
            status = 1;
        } else if (temp - velocity > 0.25) {
            if (status == 2) {
                fix++;
            } else {
                fix = 1;
            }
            status = 2;
        } else {
            temp = (velocity + temp) / 2;
            return;
        }

        temp = velocity;

        if (fix > MAX)
            return;

        if (fix == MAX) {
            if (status == 1) {
                if (listener != null) {
                    listener.onStatusChanged(1);
                }
            } else if (status == 2) {
                if (listener != null) {
                    listener.onStatusChanged(2);
                }
            }
        }

        if (listener != null) {
            listener.onStatusChanged(0);
        }

    }

    public void setListener(OnLogicProcessorListener listener) {
        this.listener = listener;
    }

    public interface OnLogicProcessorListener {
        void onStatusChanged(int status);
    }


}
