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

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Schedule a countdown until a time in the future, with regular notifications
 * on intervals along the way.
 * <p>
 * Example of showing a 30 second countdown in a text field:
 * <p>
 * <pre class="prettyprint">
 * new CountDownTimer(30000, 1000) {
 * <p>
 * public void onTick(long millisUntilFinished) {
 * mTextField.setText(&quot;seconds remaining: &quot; + millisUntilFinished / 1000);
 * }
 * <p>
 * public void onFinish() {
 * mTextField.setText(&quot;done!&quot;);
 * }
 * }.start();
 * </pre>
 * <p>
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete. This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */
public abstract class AccurateCountDownTimer {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    // ************AccurateCountdownTimer***************
    private int mTickCounter;
    private long mStartTime;

    // ************AccurateCountdownTimer***************

    /**
     * @param millisInFuture    The number of millis in the future from the call to
     *                          {@link #start()} until the countdown is done and
     *                          {@link #onFinish()} is called.
     * @param countDownInterval The interval along the way to receive {@link #onTick(long)}
     *                          callbacks.
     */
    public AccurateCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;

        // ************AccurateCountdownTimer***************
        mTickCounter = 0;
        // ************AccurateCountdownTimer***************
    }

    /**
     * Cancel the countdown.
     */
    public final void cancel() {
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final AccurateCountDownTimer start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }

        // ************AccurateCountdownTimer***************
        mStartTime = SystemClock.elapsedRealtime();
        mStopTimeInFuture = mStartTime + mMillisInFuture;
        // ************AccurateCountdownTimer***************

        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private static final int MSG = 1;

    // handles counting down
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (AccurateCountDownTimer.this) {
                final long millisLeft = mStopTimeInFuture
                        - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    // ************AccurateCountdownTimer***************
                    long now = SystemClock.elapsedRealtime();
                    long extraDelay = now - mStartTime - mTickCounter
                            * mCountdownInterval;
                    mTickCounter++;
                    long delay = lastTickStart + mCountdownInterval - now
                            - extraDelay;

                    // ************AccurateCountdownTimer***************

                    // take into account user's onTick taking time to execute

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0)
                        delay += mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}