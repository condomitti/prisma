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

package com.condomitti.prisma.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.condomitti.prisma.utils.Tools;
import com.condomitti.prisma.utils.TouchListenerWithCountDown;

import java.io.IOException;
import java.lang.reflect.Method;

public class PhoneCallHandler extends BroadcastReceiver {

	private static WindowManager wm;
	private static LinearLayout ly;
    private Context ctx;

    /**
     * Listener handles touches on screen to control the call flow.
     * 2 touches = catch he call (after 1.5 second)
     * 3 touches = declines the call (immediately)
     */
    private TouchListenerWithCountDown touchListener = new TouchListenerWithCountDown(new TouchListenerWithCountDown.CountDownHandler(){
        @Override
        public void execute(int count) {

            if( count == 2 ){

                if( Tools.phoneState == TelephonyManager.CALL_STATE_RINGING ){
                    Log.i("[PRISMA]","Touched 2 times!!");
                    answerCall();
                }


            }else if( count >= 3 ){
                if( Tools.phoneState == TelephonyManager.CALL_STATE_OFFHOOK ) {
                    Log.i("[PRISMA]", "Touched 3+ times!!");
                    disconnectCall();
                }

            }

        }

        @Override
        public void onTick(int count) {
            if( Tools.phoneState == TelephonyManager.CALL_STATE_OFFHOOK && count >= 3) {
                Log.i("[PRISMA]", "Touched 3+ times!!");

                disconnectCall();
            }
        }
    }, 1500 );


	@Override
	public void onReceive(final Context context, final Intent intent) {

        //What's the current state of the phone (ringing, off hook, idle)?
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i("[PRISMA]","Incoming state: " + state);
        this.ctx = context;

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)
				|| state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            //If phone is ringing we need to add the overlay over native phone app so that user can control the call state
            addInvitePopup();

		}

	}

	public static void removeCallHandlerPopup() {
		if (Tools.callHandlerLlRef != null) {
			wm.removeView(Tools.callHandlerLlRef);
			Tools.callHandlerLlRef = null;
		}
	}

//    /**
//     * soundBeep() notifies user that a call was caught (it beeps to signalize that there's currently an ongoing call)
//     */
//	public static void soundBeep(){
//		MediaPlayer mp = MediaPlayer.create(ctx, com.condomitti.prisma.R.raw.call_handler);
//		mp.start();
//		mp.setOnCompletionListener(new OnCompletionListener() {
//
//			int count = 1;
//			int max = 2;
//
//
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				if(count < max){
//					count++;
//					mp.seekTo(0);
//					mp.start();
//				}
//			}
//		});
//	}

    /**
     * Adds an overlay to receive touches and control call state based on user interaction
     */
	public void addInvitePopup() {

		if (Tools.callHandlerLlRef == null) {

			// Get reference to WindowManager
			wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

			// Sets layoutparams for WindowManager
			WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
							| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            0, PixelFormat.OPAQUE);
			// params.x = 250;
			params.height = LayoutParams.MATCH_PARENT;
			params.width = LayoutParams.MATCH_PARENT;
			params.format = PixelFormat.OPAQUE;

			params.gravity = Gravity.CENTER;

			params.setTitle("Atendendo ligação");

			ly = new LinearLayout(ctx);
			ly.setOrientation(LinearLayout.VERTICAL);
			ly.setGravity(Gravity.CENTER);

     		ly.setOnTouchListener( touchListener );


			TextView tv = new TextView(ctx);
			tv.setText("TOQUE NA TELA 2 VEZES PARA ATENDER A LIGAÇÃO, OU 3 VEZES PARA IGNORÁ-LA");
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			p.gravity = Gravity.CENTER;
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
			tv.setLayoutParams(p);
			tv.setTypeface(null, Typeface.BOLD);
			ly.addView(tv);
			ly.setFocusable(true);
			ly.requestFocus();
			wm.addView(ly, params);

			Tools.callHandlerLlRef = ly;
		}
	}


	private void answerCall() {

        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                ctx.sendOrderedBroadcast(btnDown, enforcedPerm);
                ctx.sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
        }




    }


    private void disconnectCall() {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(
                    serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface",
                    IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

            Tools.phoneState = TelephonyManager.CALL_STATE_IDLE;
            wm.removeView(ly);
            Tools.callHandlerLlRef = null;

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
