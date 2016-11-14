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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

import java.util.ArrayList;
import java.util.Calendar;

public class Welcome extends SuperActivity implements OnClickListener {
    private static final int NUMERIC_KEYBOARD = 45634;
    private static final int AIRPLANE_MODE = 939487;

    String dialNumber;
    Button btnNumericKeyboard = null;
    Button btnContacs = null;
    Button btnMissedCalls = null;
    Button btnCheckBatteryLevel = null;
    Button btnDateTime = null;
    Button btnSMS = null;
    Button btnAirplaneMode = null;

    // Button btnEmail = null;
    // Button btnNotes = null;
    // Button btnSettings = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);

        makeUpScreen();
        greetings();
        getWarnings();
    }

    public void getWarnings() {
        if (getIntent().hasExtra("warnings")) {

            ArrayList<String> warnings = getIntent().getStringArrayListExtra("warnings");

            if (null != warnings) {

                for (String s : warnings) {
                    Tools.speak(s, true);
                }

            }
        }
    }

    public void greetings() {
        long waitingTime = 1000;
        Calendar c = Calendar.getInstance();
        long nowHours = c.get(Calendar.HOUR_OF_DAY);
        if (nowHours > 4 && nowHours < 12) {
            Tools.speak("Bom dia!", waitingTime);
        } else if (nowHours >= 12 && nowHours < 18) {
            Tools.speak("Boa tarde!", waitingTime);
        } else {
            Tools.speak("Boa noite!", waitingTime);
        }
    }

    public void makeUpScreen() {
        setContentView(R.layout.welcome);
        super.loadTouchables();

        TextView title = (TextView) findViewById(R.id.titleWelcome);
        title.setFocusable(true);
        title.requestFocus();

		/*
         * Retrieves references
		 */
        btnContacs = (Button) findViewById(R.id.btnContacts);
        btnNumericKeyboard = (Button) findViewById(R.id.btnNumericKeyboard);
        btnMissedCalls = (Button) findViewById(R.id.btnLostCalls);
        btnDateTime = (Button) findViewById(R.id.btnDateTime);
        btnSMS = (Button) findViewById(R.id.btnSMS);
        // btnEmail = (Button)findViewById(R.id.btnEmail);
        // btnNotes = (Button)findViewById(R.id.btnNotes);
        // btnSettings = (Button)findViewById(R.id.btnSettings);
        btnCheckBatteryLevel = (Button) findViewById(R.id.btnCheckBatteryLevel);
        btnAirplaneMode = (Button) findViewById(R.id.btnAirplaneMode);

		/*
		 * Sets Listeners
		 */
        btnNumericKeyboard.setOnClickListener(this);
        btnContacs.setOnClickListener(this);
        btnMissedCalls.setOnClickListener(this);
        btnDateTime.setOnClickListener(this);
        btnSMS.setOnClickListener(this);
        // btnEmail.setOnClickListener(this);
        // btnNotes.setOnClickListener(this);
        // btnSettings.setOnClickListener(this);
        btnCheckBatteryLevel.setOnClickListener(this);
        btnAirplaneMode.setOnClickListener(this);

        // btnNumericKeyboard.setOnFocusChangeListener(Tools.pfListener);
        // btnContacs.setOnFocusChangeListener(Tools.pfListener);
        // btnMissedCalls.setOnFocusChangeListener(Tools.pfListener);
        // btnDateTime.setOnFocusChangeListener(Tools.pfListener);
        // btnSMS.setOnFocusChangeListener(Tools.pfListener);
        // btnEmail.setOnFocusChangeListener(Tools.pfListener);
        // btnNotes.setOnFocusChangeListener(Tools.pfListener);
        // btnSettings.setOnFocusChangeListener(Tools.pfListener);
        // btnCheckBatteryLevel.setOnFocusChangeListener(Tools.pfListener);

        if (Settings.System.getInt(getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
            btnAirplaneMode.setText(getResources().getString(
                    R.string.enable_airplane_mode));
        } else {
            btnAirplaneMode.setText(getResources().getString(
                    R.string.disable_airplane_mode));
        }

    }

    @Override
    public void onClick(View v) {

        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

        Intent i;

        switch (v.getId()) {
            case R.id.btnNumericKeyboard:
                i = new Intent(this, NumericKeyboard.class);
                startActivityForResult(i, NUMERIC_KEYBOARD);
                break;
            case R.id.btnDateTime:
                i = new Intent(this, DateTime.class);
                startActivity(i);
                break;
            case R.id.btnContacts:
                i = new Intent(this, Contacts.class);
                startActivity(i);
                break;
            case R.id.btnCheckBatteryLevel:
                checkBatteryLevel();
                break;
            case R.id.btnLostCalls:
                i = new Intent(this, MissedCalls.class);
                startActivity(i);
                break;
            case R.id.btnSMS:
                i = new Intent(this, MessagesHome.class);
                startActivity(i);
                break;
            case R.id.btnAirplaneMode:
                if (Settings.System.getInt(getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
                    Tools.showConfirm(
                            this,
                            "Ao habilitar o modo avião não será possível fazer nem receber ligações ou mensagens. Deseja prosseguir?",
                            AIRPLANE_MODE);
                } else {
                    Tools.showConfirm(
                            this,
                            "Ao desabilitar o modo avião você voltará a receber chamadas e mensagens normalmente. Deseja prosseguir?",
                            AIRPLANE_MODE);
                }
                break;
        }
    }

    private void checkBatteryLevel() {
        int level = Tools.checkBatteryLevel(this);
        String strBattery = String.format(
                getResources().getString(R.string.batteryLevel), level);
        Tools.speak(strBattery, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case NUMERIC_KEYBOARD:
                if (resultCode == RESULT_OK) {
                    dialNumber = data.getStringExtra("typedNumber");
                    String s = String.format(
                            getResources().getString(R.string.dialConfirmation),
                            Tools.handleNumber(dialNumber));
                    Tools.showConfirm(this, s);
                }
                break;
            case Tools.SHOW_SPOKEN_DIALOG:

                if (data.getIntExtra("prismaDialogReqCode", -1) == AIRPLANE_MODE) {

                    if (resultCode == SpokenDialog.RESULT_YES) {
                        toggleAirplaneMode();
                    }

                } else {
                    if (resultCode == SpokenDialog.RESULT_YES) {
                        Intent i = new Intent(Intent.ACTION_CALL);
                        Log.i("PRISMA", "Going to call " + dialNumber);
                        i.setData(Uri.parse("tel://" + dialNumber));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(i, Tools.CALL_PHONE_ACTIVITY);
                        }
                    }
                }

                break;
        }
    }

    public void toggleAirplaneMode() {
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);

        if (Settings.System.getInt(getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 1);
            intent.putExtra("state", true);// true; indicates that state of
            // Airplane mode is ON.
        } else {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
            intent.putExtra("state", false);// false; indicates that state of
            // Airplane mode is OFF.
        }

        sendBroadcast(intent);

        if (Settings.System.getInt(getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
            btnAirplaneMode.setText(getResources().getString(
                    R.string.enable_airplane_mode));
        } else {
            btnAirplaneMode.setText(getResources().getString(
                    R.string.disable_airplane_mode));
        }

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }
}