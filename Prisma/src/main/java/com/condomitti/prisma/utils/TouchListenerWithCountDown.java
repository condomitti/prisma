/*
 *     This file is part of Prisma.
 *
 *     Prisma is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Prisma is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Prisma.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.condomitti.prisma.utils;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchListenerWithCountDown implements View.OnTouchListener {

    private int counter = 0;
    private CountDownHandler handler;
    private int endTimeout;
    private AccurateCountDownTimer timer;


    public TouchListenerWithCountDown(CountDownHandler handler , int endTimeout){


        this.handler = handler;
        this.endTimeout = endTimeout;

        setupTimer();


    }

    public void setupTimer(){
        timer = new AccurateCountDownTimer(endTimeout, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                handler.onTick(counter);
            }

            @Override
            public void onFinish() {

                handler.execute(counter);
                counter = 0;

            }
        };
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if( event.getAction() == MotionEvent.ACTION_UP ){
            counter++;
            Log.i("[PRISMA]","ACTION_UP... touched " + counter + " times.");
            timer.cancel();
            timer.start();
        }

        return false;
    }

    public interface CountDownHandler{
        void execute(int count);
        void onTick(int count);
    }

}
