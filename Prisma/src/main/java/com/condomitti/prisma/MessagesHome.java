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

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class MessagesHome extends SuperActivity implements OnClickListener {

    String toSendNumber;
    String toSendMessage;

    Button btnReadInboxMessages = null;
    Button btnReadOutboxMessages = null;
    Button btnSendSMS = null;
    Button btnBack = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Tools.setScreenSettings(this);

        makeUpScreen();
        getWarnings();
    }

    public void getWarnings() {
        if (getIntent().hasExtra("warnings")) {
            for (String s : getIntent().getStringArrayListExtra("warnings")) {
                Tools.speak(s, true);
            }
        }
    }

    public void makeUpScreen() {
        setContentView(R.layout.messages_home);
        super.loadTouchables();

    	/*
    	 * Retrieves references
    	 */
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReadInboxMessages = (Button) findViewById(R.id.btnReceivedMessages);
        btnReadOutboxMessages = (Button) findViewById(R.id.btnSentMessages);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
    	
    	
    	/*
    	 * Sets Listeners
    	 */
        btnBack.setOnClickListener(this);
        btnReadInboxMessages.setOnClickListener(this);
        btnReadOutboxMessages.setOnClickListener(this);
        btnSendSMS.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

        Intent i = null;

        switch (v.getId()) {
            case R.id.btnReceivedMessages:
                i = new Intent(this, MessagesList.class);
                startActivity(i);
                break;
            case R.id.btnSentMessages:
                Tools.speak("Função ainda não implementada", true);
                break;
            case R.id.btnSendSMS:
                Tools.speak(getString(R.string.type_the_number), true);
                i = new Intent(this, NumericKeyboard.class);
                startActivityForResult(i, Tools.DISPLAY_NUMERIC_KEYBOARD);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Tools.DISPLAY_NUMERIC_KEYBOARD:
                if (resultCode == RESULT_OK) {

                    Tools.speak(getString(R.string.number_chosen_now_type_message), true);
                    toSendNumber = data.getStringExtra("typedNumber");
                    Intent i = new Intent(this, WordMaker.class);
                    startActivityForResult(i, Tools.DISPLAY_WORD_MAKER);

                }

                break;
            case Tools.DISPLAY_WORD_MAKER:
                if (resultCode == RESULT_OK) {

                    toSendMessage = data.getStringExtra("typedString");
                    Tools.speak(getString(R.string.message_was_typed_now_sending), true);

                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(toSendNumber, null, toSendMessage, pi, null);

                }

                break;
        }
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }
}