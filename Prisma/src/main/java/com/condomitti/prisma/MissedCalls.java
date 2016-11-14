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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class MissedCalls extends SuperActivity {
    LinearLayout listMissedCalls = null;
    Cursor missedCalls = null;
    boolean loading = true;
    ArrayList<HashMap<String, String>> callLog = new ArrayList<HashMap<String, String>>();
    String numberTocall = "";
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.setScreenSettings(this);
        getMissedCalls();
        makeUpScreen();
    }

    public void getMissedCalls() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            missedCalls = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, CallLog.Calls.TYPE + " = ?",
                    new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE)},
                    Calls.DATE + " DESC");
            if (!missedCalls.moveToFirst()) {
                Tools.speak(getResources().getString(R.string.noMissedCalls), true);
                finish();
            }
        }
    }

    public void makeUpScreen() {

        setContentView(R.layout.missed_calls);

        TextView title = (TextView) findViewById(R.id.txtTitle);
        title.setText("Chamadas perdidas");
        title.requestFocus();

        listMissedCalls = (LinearLayout) findViewById(R.id.listMissedCalls);

        TextView tvBack = new TextView(getApplicationContext());
        tvBack.setText("Voltar");
        tvBack.setTextAppearance(this, R.style.ListsTextView);
        tvBack.setFocusable(true);
        tvBack.setFocusableInTouchMode(true);
        tvBack.setVisibility(TextView.VISIBLE);
        tvBack.setOnFocusChangeListener(Tools.pfListenerQueue);
        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Tools.speak("Selecionado voltar", true);
                finish();
            }
        });
        listMissedCalls.addView(tvBack, counter, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (missedCalls != null && missedCalls.moveToFirst()) {
            do {
                counter++;
                HashMap<String, String> m = new HashMap<String, String>();
                String str = missedCalls.getString(missedCalls
                        .getColumnIndex(Calls.CACHED_NAME));
                if (str == null)
                    str = Tools.handleNumber(missedCalls.getString(missedCalls
                            .getColumnIndex(Calls.NUMBER)));
                if (str == null)
                    str = "número privado";
                str = Tools.getSpokenFormattedDate(new Date(missedCalls
                        .getLong(missedCalls.getColumnIndex(Calls.DATE))))
                        + " "
//						+ getResources().getString(R.string.minutes)
                        + " "
                        + getResources().getString(R.string.call_from)
                        + " " + str;
                m.put("_id", Integer.toString(missedCalls.getInt(missedCalls
                        .getColumnIndex(Calls._ID))));
                m.put("strMissedCall", str);
                m.put("number", missedCalls.getString(missedCalls
                        .getColumnIndex(Calls.NUMBER)));
                callLog.add(m);

                TextView tv = new TextView(getApplicationContext());
                tv.setText(str);
                tv.setFocusable(true);
                tv.setTextAppearance(this, R.style.ListsTextView);
                tv.setFocusableInTouchMode(true);
                tv.setVisibility(TextView.GONE);
                final String _toSpeak = str;
                tv.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (v.isFocused()) {
                            setMissedCallAsNotNew(Integer.parseInt(callLog.get(
                                    counter - 1).get("_id")));
                            Log.i("PRISMA",
                                    "ID: "
                                            + callLog.get(counter - 1).get(
                                            "_id"));
                            Tools.speak(_toSpeak, false);
                        }
                    }
                });
                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (callLog.get(counter - 1).get("number").trim()
                                .length() == 0) {
                            Tools.speak("Não há como responder à essa chamada de número privado.", true);
                            return;
                        }
                        Tools.speak("Selecionado"
                                + ((TextView) v).getText().toString(), false);
                        numberTocall = callLog
                                .get(counter - 1).get("number");
                        String s = String.format(
                                getResources().getString(
                                        R.string.dialConfirmation), Tools
                                        .handleNumber(numberTocall));
                        Tools.showConfirm(MissedCalls.this, s);
                    }
                });
                listMissedCalls.addView(tv, counter,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

            } while (missedCalls.moveToNext());
        }

        counter = 0;
        Tools.speak("Exibindo " + callLog.size() + " chamadas perdidas", true);

        super.loadTouchables();
    }

    public void setMissedCallAsNotNew(int _id) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            ContentValues cv = new ContentValues();
            cv.put(CallLog.Calls.NEW, 0);
            getContentResolver().update(CallLog.Calls.CONTENT_URI, cv,
                    CallLog.Calls._ID + " = ? ",
                    new String[]{Integer.toString(_id)});
            getContentResolver().notifyChange(CallLog.Calls.CONTENT_URI, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Tools.SHOW_SPOKEN_DIALOG:
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    if (resultCode == SpokenDialog.RESULT_YES) {
                        Intent i = new Intent(Intent.ACTION_CALL);
                        i.setData(Uri.parse("tel://" + numberTocall));
                        startActivityForResult(i, Tools.CALL_PHONE_ACTIVITY);
                    }

                }
                break;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }

    public void changeVisibility(int direction) {

        listMissedCalls.getChildAt(counter).setVisibility(View.GONE);

        if (direction == View.FOCUS_DOWN) {
            counter++;
        } else {
            counter--;
        }
        listMissedCalls
                .getChildAt(counter).setVisibility(View.VISIBLE);
        listMissedCalls.getChildAt(counter).requestFocus();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (counter < callLog.size() - 1)
                    changeVisibility(View.FOCUS_DOWN);

                break;
            case KeyEvent.KEYCODE_VOLUME_UP:

                if (counter > 0)
                    changeVisibility(View.FOCUS_UP);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
