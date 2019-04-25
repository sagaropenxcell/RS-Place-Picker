package com.example.mapactivity;

import android.view.MotionEvent;
import android.view.View;

public class Utils {


    public static void setAlpha(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setAlpha(0.5f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.setAlpha(1f);
                }
                return false;
            }
        });
    }


}
