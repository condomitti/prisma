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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

import java.util.ArrayList;
import java.util.LinkedList;

public class Contacts extends SuperActivity implements OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 10001;

    Button btnSearchByVoice, btnSearchByTyping, btnAllContacts, btnInsertNewContact, btnBack = null;
    LinkedList<Bean_Contact> foundContacts = null;


    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);

        makeUpScreen();
    }

    public void makeUpScreen() {

        setContentView(R.layout.contacts);
        super.loadTouchables();

        TextView title = (TextView) findViewById(R.id.titleContacts);
        title.setFocusable(true);
        title.requestFocus();


        /**
         * Retrieves references
         */
        btnAllContacts = (Button) findViewById(R.id.btnAllContacts);
        btnInsertNewContact = (Button) findViewById(R.id.btnInsertNewContact);
        btnSearchByTyping = (Button) findViewById(R.id.btnSearchByTyping);
        btnSearchByVoice = (Button) findViewById(R.id.btnSearchByVoice);
        btnBack = (Button) findViewById(R.id.btnBack);


        /**
         * Sets listeners
         */
        btnInsertNewContact.setOnClickListener(this);
        btnSearchByTyping.setOnClickListener(this);
        btnSearchByVoice.setOnClickListener(this);
        btnAllContacts.setOnClickListener(this);
        btnBack.setOnClickListener(this);
//		btnAllContacts.setOnFocusChangeListener(Tools.pfListener);
//		btnInsertNewContact.setOnFocusChangeListener(Tools.pfListener);
//		btnSearchByTyping.setOnFocusChangeListener(Tools.pfListener);
//		btnSearchByVoice.setOnFocusChangeListener(Tools.pfListener);
//		btnBack.setOnFocusChangeListener(Tools.pfListener);
    }

    public void doVoiceSearch() {
        if (Tools.checkInternetConnection(this)) {

            Handler h = new Handler();
            h.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (Tools.isSpeaking()) {
                        while (Tools.isSpeaking()) {

                        }
                    }
                    execVoiceSearch();

                }
            }, 2000);

        } else {
            Tools.speak("Atenção, você não está conectado à internet. Sem internet não é possível fazer busca por voz.", false);
        }

    }

    public void contactsList(String searchCriterion) {
        Intent i = new Intent(this, ContactsList.class);
        i.putExtra("searchCriterion", searchCriterion);
        startActivity(i);
    }

    public void execVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Reconhecedor de voz");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String contact = "";
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            contact = matches.get(0);
            Tools.speak("Buscando por " + contact, true);
        } else if (requestCode == Tools.DISPLAY_WORD_MAKER && resultCode == RESULT_OK) {
            contact = data.getStringExtra("typedString");
            Tools.speak("Buscando por " + contact, true);
        }

        if (resultCode == RESULT_OK) {
            contactsList(contact);
        }
    }


    @Override
    public void onClick(View v) {

        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

        Intent i;

        switch (v.getId()) {
            case R.id.btnAllContacts:

                contactsList(null);


                break;
            case R.id.btnInsertNewContact:
                i = new Intent(this, Contact.class);
                startActivity(i);
                break;
            case R.id.btnSearchByTyping:
                i = new Intent(this, WordMaker.class);
                startActivityForResult(i, Tools.DISPLAY_WORD_MAKER);
                break;
            case R.id.btnSearchByVoice:
                doVoiceSearch();
                break;
            case R.id.btnBack:
                finish();

                break;

        }
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }


}
