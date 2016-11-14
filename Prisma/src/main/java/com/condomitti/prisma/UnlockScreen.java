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

package com.condomitti.prisma;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.condomitti.prisma.receivers.PhoneCallHandler;
import com.condomitti.prisma.receivers.Receiver_BatteryStatus;
import com.condomitti.prisma.receivers.ScreenHandler;
import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnlockScreen extends Activity implements OnLongClickListener,
        OnInitListener {
    private static final int CHECK_TTS_DATA = 10000;
    private static final int CONFIRM_SETTINGS_SCREEN = 104552;
    private static final int RESULT_MANAGE_OVERLAY_PERMISSION = 293847;
    MediaPlayer mp;
    private int unlockCounter;
    private int settingsCounter;
    boolean gotOrMadeCall = false;
    ArrayList<String> warnings = new ArrayList<String>();
    BroadcastReceiver batteryReceiver = new Receiver_BatteryStatus();
    NK_PhoneStateListener nkPhoneListener = new NK_PhoneStateListener();
    private BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Tools.speak(getString(R.string.sms_successfully_sent), true);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Tools.speak(getString(R.string.sms_unsuccessfully_sent), true);

                    break;
            }

        }

    };
    @SuppressLint("HandlerLeak")
    private Handler h = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);


                    break;
            }

        }
    };

    CountDownTimer cdtUnlocker = new CountDownTimer(3000, 2000) {

        @Override
        public void onTick(long millisUntilFinished) {


        }

        @Override
        public void onFinish() {

            unlockCounter = 0;
        }
    };

    CountDownTimer cdtSettings = new CountDownTimer(1000, 2000) {

        @Override
        public void onTick(long millisUntilFinished) {


        }

        @Override
        public void onFinish() {

            settingsCounter = 0;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

//        checkPermissions();
        mp = new MediaPlayer();
        AssetFileDescriptor afd = getResources().openRawResourceFd(
                R.raw.unlocker);
        try {
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            mp.prepare();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }


        settingsCounter = 0;
        unlockCounter = 0;

        Tools.resetAudioLevels(getApplicationContext());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisters();
    }

    private void unregisters() {
        Log.i("PRISMA", "Unregistering RECEIVERS");
        unRegisterLowBatteryReceiver();
        unRegisterSMSSentReceiver();
        unRegisterScreenReceiver();
        setupPhoneStateListener(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.i("PRISMA", "UnlockScreen.java onCreate() called");

        checkPermissions();
        performInitChecks();

        setupPhoneStateListener(true);
        registerScreenReceiver();
        registerLowBatteryReceiver();
        registerSMSSentReceiver();

        unlockCounter = 0;

        setContentView(R.layout.unlock_screen);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.unlockerLayout);
        rl.setOnLongClickListener(this);

        checkTtsLib();

    }

    public void checkBatteryLevel() {
        int bLevel = Tools.checkBatteryLevel(this);
        if (bLevel <= 20) {
            warnings.add(getResources().getString(R.string.low_battery_warning));
        }
    }

    public void checkAirplaneMode() {
        if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
            warnings.add(getResources().getString(R.string.airplane_mode_is_on));
        }
    }

    public void setupPhoneStateListener(boolean listen) {
        Tools.phoneState = TelephonyManager.CALL_STATE_IDLE;
        TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (listen) {
            teleMgr.listen(nkPhoneListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            teleMgr.listen(nkPhoneListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void registerScreenReceiver() {
        if (Tools.screenHandler == null) {
            Log.i("PRISMA", "Registering screenReceiver");
            Tools.screenHandler = new ScreenHandler();
        }
        IntentFilter on_filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(Tools.screenHandler, on_filter);
        IntentFilter off_filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(Tools.screenHandler, off_filter);
    }

    public void unRegisterScreenReceiver() {
        unregisterReceiver(Tools.screenHandler);
    }

    public void registerLowBatteryReceiver() {
        IntentFilter inf = new IntentFilter();
        inf.addAction("android.intent.action.BATTERY_LOW");
        registerReceiver(batteryReceiver, inf);
    }

    public void unRegisterLowBatteryReceiver() {
        unregisterReceiver(batteryReceiver);
    }

    public void registerSMSSentReceiver() {
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
    }

    public void unRegisterSMSSentReceiver() {
        unregisterReceiver(smsSentReceiver);
    }

    public void checkTaps() {
        cdtUnlocker.cancel();
        cdtUnlocker.start();
        if (unlockCounter >= 3) {
            cdtUnlocker.cancel();
            Intent i = new Intent(this, Welcome.class);
            i.putExtra("warnings", warnings);
            startActivity(i);
        }
    }

    public void checkSettingsUnlock() {
        cdtSettings.cancel();
        cdtSettings.start();
        if (settingsCounter >= 10) {
            Intent i = new Intent(this, SpokenDialog.class);
            i.putExtra(
                    "prismaDialogMessage",
                    getResources().getString(
                            R.string.settings_screen_confirmation));
            i.putExtra("prismaDialogHasCloseTimer", true);
            startActivityForResult(i, CONFIRM_SETTINGS_SCREEN);
            cdtSettings.cancel();
        }
    }

    @Override
    public boolean onLongClick(View arg0) {


        if (mp.isPlaying()) {
            mp.seekTo(0);
        }

        mp.start();
        unlockCounter++;
        checkTaps();

        return true;
    }

    public void checkTtsLib() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, CHECK_TTS_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHECK_TTS_DATA) {
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                    // tts is up and running
                    Tools.initTts(getApplicationContext(), this);
                    break;
                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
                    Intent installIntent = new Intent();
                    installIntent
                            .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                    break;
                default:
            }
        } else if (requestCode == CONFIRM_SETTINGS_SCREEN
                && resultCode == SpokenDialog.RESULT_YES) {
            Intent i = new Intent();
            i.setClassName("com.android.settings",
                    "com.android.settings.Settings");
            startActivity(i);
        } else if (requestCode == RESULT_MANAGE_OVERLAY_PERMISSION) {

            if (resultCode != RESULT_OK) {
                Log.i("[PRISMA]", "WARNING: Permission for MANAGE_OVERLAY not granted");
            }

        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.i("PRISMA", "TTS is ok to start working =)");
            Tools.ttsReady = true;
            // if (getIntent().hasExtra("justTurnedScreenOn")
            // && getIntent().getBooleanExtra("justTurnedScreenOn", false)) {
            Tools.speak(
                    "Bem vindo ao prisma fone! Espero que você se divirta enquanto trabalhamos em conjunto!",
                    false);
            // }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tools.speak(getResources().getString(R.string.screenOn), false);
        PhoneCallHandler.removeCallHandlerPopup();

        warnings = new ArrayList<String>();
        performInitChecks();
        Log.i("PRISMA", "UnlockScreen.java onNewIntent() called");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case Tools.INITIAL_REQUESTS: {

                Log.i("[PRISMA]", "Permissions: " + Arrays.toString(permissions));
                Log.i("[PRISMA]", "GRANTS: " + Arrays.toString(grantResults));

                checkBatteryLevel();
                checkAirplaneMode();
                PackageManager pm = getPackageManager();
                @SuppressWarnings("rawtypes")
                List activities = pm.queryIntentActivities(new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
                if (activities.size() == 0) {
                    warnings.add("Atenção, recurso de reconhecimento de voz não está instalado em seu celular. Para fazer uso do Prisma, peça que alguém instale o software VoiceRecognition para você.");
                }


            }
        }


    }

    public void performInitChecks() {

        checkForMissedCalls();
        checkForSMS();

    }

    public void checkPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS}, Tools.INITIAL_REQUESTS);

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 3333);
        }
    }

    public void checkForMissedCalls() {
        if (Tools.getMissedCalls(this)) {
            warnings.add("Existem chamadas perdidas!");
        }
    }

    public void checkForSMS() {
        if (Tools.getUnreadSMS(this)) {
            warnings.add("Existem mensagens novas!");
        }
    }

    class NK_PhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:

                    Tools.phoneState = TelephonyManager.CALL_STATE_IDLE;
                    PhoneCallHandler.removeCallHandlerPopup();

                    if (gotOrMadeCall) {

                        gotOrMadeCall = false;
                        Tools.speak("Ligação encerrada", true);
                        Tools.resetAudioLevels(getApplicationContext());
                        Message msg = new Message();
                        msg.what = 1;
                        h.sendMessageDelayed(msg, 1500);

                    }

                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    Tools.phoneState = TelephonyManager.CALL_STATE_RINGING;

                    Log.i("PRISMA", "CALL_STATE_RINGING: " + incomingNumber);

                    final String msg;
                    if (incomingNumber.trim().length() > 0) {

                        Uri uri = Uri.withAppendedPath(
                                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                Uri.encode(incomingNumber));
                        Cursor c = getContentResolver()
                                .query(uri,
                                        new String[]{android.provider.ContactsContract.Contacts.DISPLAY_NAME},
                                        null, null, null);

                        if (c != null && c.moveToFirst()) {
                            msg = "Recebendo chamada de "
                                    + c.getString(c
                                    .getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
                        } else {
                            msg = "Recebendo Chamada de "
                                    + Tools.handleNumber(incomingNumber);
                        }
                    } else {
                        msg = "Recebendo chamada de número privado";
                    }

                    Tools.speak(msg, false, false, null, false);

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Tools.stopSpeaking();
                    Tools.phoneState = TelephonyManager.CALL_STATE_OFFHOOK;
                    gotOrMadeCall = true;

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                checkSettingsUnlock();
                settingsCounter++;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_SEARCH:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }

}
