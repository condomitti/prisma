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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.condomitti.prisma.GeneralExec;
import com.condomitti.prisma.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Tools {
	public static final int SHOW_SPOKEN_DIALOG = 57890;
	public static final int CALL_PHONE_ACTIVITY = 6522346;
	public static final int DISPLAY_WORD_MAKER = 7643443;
	public static final int DISPLAY_NUMERIC_KEYBOARD = 7643758;
	public static final int INITIAL_REQUESTS = 100;
	public static boolean ttsReady = false;

	public static BroadcastReceiver screenHandler;

	public static LinearLayout callHandlerLlRef;
	public static int phoneState = 0;
	private static TextToSpeech mTts;
	public static PrismaFListenerQueue pfListenerQueue = new PrismaFListenerQueue();


	public static String[] daysOfWeek = {"Domingo", "Segunda-feira",
			"Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira",
			"Sábado"};
	public static String[] months = {"Janeiro", "Fevereiro", "Março", "Abril",
			"Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
			"Novembro", "Dezembro"};
	public static String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z"};
	public static String[] numbers = {"white_space", "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9"};

	public static void initTts(Context c, OnInitListener l) {
		mTts = new TextToSpeech(c, l);
	}

	public static String getSpokenFormattedDate(Date d) {
		//This should be changed to local time format
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm",
				Locale.getDefault());
		return sdf.format(d);
	}

	public static void showConfirm(Context ctx, String msg, int reqCode) {

		Intent i = new Intent(ctx, SpokenDialog.class);
		i.putExtra("prismaDialogMessage", msg);
		i.putExtra("prismaDialogReqCode", reqCode);
		((Activity) ctx).startActivityForResult(i, SHOW_SPOKEN_DIALOG);
	}

	public static void showConfirm(Context ctx, String msg) {
		Tools.showConfirm(ctx, msg, -1);
	}

	public static boolean checkVoiceRecognition(Context c) {
		PackageManager pm = c.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		//Is there available resources?
		return activities.size() > 0;
	}


	public static boolean getMissedCalls(Activity actvt){

		if (ContextCompat.checkSelfPermission(actvt, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

			boolean hasMissedCalls;
			Cursor c = actvt.getApplicationContext().getContentResolver().query(
					CallLog.Calls.CONTENT_URI,
					null,
					CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.NEW + " = ?",
					new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE),
							Calls.NEW}, Calls.DATE + " DESC");
			hasMissedCalls = c.moveToFirst();

			c.close();

			return hasMissedCalls;

		}

		return false;

	}

	public static boolean getUnreadSMS(Activity acvt) {

		if (ActivityCompat.checkSelfPermission(acvt.getApplicationContext().getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(acvt.getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
			final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
            Cursor c = acvt.getContentResolver().query(SMS_INBOX, null, "read = 0",
					null, null);
            return c.moveToFirst();
        }

        return false;

    }

	public static boolean checkInternetConnection(Context ctx) {

		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (conMgr.getActiveNetworkInfo() != null && conMgr
				.getActiveNetworkInfo().isConnected());

	}

	public static void speak(final String msg, long waitingTime) {
		CountDownTimer cdt = new CountDownTimer(waitingTime, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				speak(msg, true);
			}
		};
		cdt.start();
	}

	public static void speak(String msg, boolean appendToQueue) {
		speak(msg, false, appendToQueue, null, false);
	}

	public static void speak(String msg, boolean isNumber, boolean appendToQueue) {
		speak(msg, isNumber, appendToQueue, null, false);
	}

	public static void speak(String msg, boolean isNumber,
			boolean appendToQueue, boolean slower) {
		speak(msg, isNumber, appendToQueue, null, slower);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void speak(String msg, boolean isNumber,
			boolean appendToQueue, final GeneralExec exec, boolean slower) {
		if (mTts != null) {
			int append = appendToQueue ? TextToSpeech.QUEUE_ADD
					: TextToSpeech.QUEUE_FLUSH;

			if (isNumber) {
				msg = handleNumber(msg);
			}

			if (slower) {
				mTts.setSpeechRate(0.7f);
			}
			HashMap<String, String> ttsParams = new HashMap<String, String>();
			ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
					String.valueOf(AudioManager.STREAM_MUSIC));
			ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
					"prismaID");

			mTts.speak(msg, append, ttsParams);

		}

        if (Build.VERSION.SDK_INT >= 15) {

            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onError(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    mTts.setSpeechRate(1.0f);
                    if (exec != null) {
                        exec.exec();
                    }

                }
            });
        } else {
            mTts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {

                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    if (exec != null) {
                        exec.exec();
                    }
                }
            });
        }

	}

	public static String handleNumber(String msg) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < msg.length(); i++) {
			sb.append(msg.charAt(i) + " ");
		}
		return sb.toString();
	}

	public static boolean stopSpeaking() {
		return mTts.stop() == TextToSpeech.SUCCESS ? true : false;
	}

	public static boolean isSpeaking() {
		return mTts.isSpeaking();
	}

	public static void setUpSpeakBehavior(ViewGroup vg) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			View view = vg.getChildAt(i);
			if (view instanceof Button || view instanceof TextView) {
				setFocusListener(view);
			} else if (view instanceof ViewGroup) {
				setUpSpeakBehavior((ViewGroup) view);
			}
		}
	}


    /**
     * Used to pronounce special characters in a clearer manner
     * @param s String to be enhanced
     * @param ctx The context from which to execute
     * @return String
     */
	public static String checkSpecialChars(String s, Context ctx){

		if(s.equals(".")) return ctx.getResources().getString(R.string.toSpeakPeriod);
		else if(s.equals(":")) return ctx.getResources().getString(R.string.toSpeakColon);
		else if(s.equals(",")) return ctx.getResources().getString(R.string.toSpeakComma);
		else if(s.equals(";")) return ctx.getResources().getString(R.string.toSpeakSemiColon);
		else if(s.equals("/")) return ctx.getResources().getString(R.string.toSpeakSlash);
		else if(s.equals("+")) return ctx.getResources().getString(R.string.toSpeakPlus);
		else if(s.equals("-")) return ctx.getResources().getString(R.string.toSpeakMinus);
		else if(s.equals("=")) return ctx.getResources().getString(R.string.toSpeakEquals);
		else if(s.equals("?")) return ctx.getResources().getString(R.string.toSpeakQuestion);
		else if(s.equals("!")) return ctx.getResources().getString(R.string.toSpeakExclamation);
		else if(s.equals("'")) return ctx.getResources().getString(R.string.toSpeakSingleQuote);
		else if(s.equals("\"")) return ctx.getResources().getString(R.string.toSpeakDoubleQuote);
		else if(s.equals("#")) return ctx.getResources().getString(R.string.toSpeakHash);
		else return s;
		
	}

	public static void setFocusListener(View v) {
		v.setFocusable(true);
		v.setOnFocusChangeListener(pfListenerQueue);
	}

	public static void setScreenSettings(Activity a) {
		a.getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		a.getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		a.requestWindowFeature(Window.FEATURE_NO_TITLE);
		a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	public static int checkBatteryLevel(Context c) {
		Intent batteryIntent = c.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra("level", 0);
		return level;
	}

	public static String getPersonNameById(Context ctx, long id) {
		Cursor c = ctx.getContentResolver().query(
				ContactsContract.RawContacts.CONTENT_URI,
				null,
				ContactsContract.RawContacts.DELETED + " = '0'" + " and "
						+ ContactsContract.RawContacts._ID + " = " + id, null,
				ContactsContract.Data.DISPLAY_NAME);
		if (c.moveToFirst()) {
			return c.getString(c
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}

		return "desconhecido";
	}

	public static void resetAudioLevels(Context ctx){
		AudioManager am = 
			    (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			
		int notificationLevel = (int) (am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) * 0.8);
		int musicLevel = (int) (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.8);
		int ringLevel = (int) (am.getStreamMaxVolume(AudioManager.STREAM_RING) * 0.8);
		int voiceCallLevel = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
			am.setStreamVolume(
			    AudioManager.STREAM_MUSIC,
			    musicLevel,
			    0);
			
			am.setStreamVolume(
					AudioManager.STREAM_NOTIFICATION,
					notificationLevel,
					0);
			
			am.setStreamVolume(
					AudioManager.STREAM_RING,
					ringLevel,
					0);
			
			am.setStreamVolume(
					AudioManager.STREAM_VOICE_CALL,
					voiceCallLevel,
					0);
			
	}

}
