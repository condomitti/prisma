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
import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class Contacts extends SuperActivity implements OnClickListener{
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

	public void makeUpScreen(){

		setContentView(R.layout.contacts);
		super.loadTouchables();
		
		TextView title = (TextView)findViewById(R.id.titleContacts);
		title.setFocusable(true);
		title.requestFocus();


		/**
		 * Retrieves references
		 */
		btnAllContacts = (Button)findViewById(R.id.btnAllContacts);
		btnInsertNewContact = (Button)findViewById(R.id.btnInsertNewContact);
		btnSearchByTyping = (Button)findViewById(R.id.btnSearchByTyping);
		btnSearchByVoice = (Button)findViewById(R.id.btnSearchByVoice);
		btnBack = (Button)findViewById(R.id.btnBack);


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

	public void doVoiceSearch(){
		if(Tools.checkInternetConnection(this)){

			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					if(Tools.isSpeaking()){
						while(Tools.isSpeaking()){

						}
					}
					execVoiceSearch();

				}
			}, 2000);

		}else{
			Tools.speak("Atenção, você não está conectado à internet. Sem internet não é possível fazer busca por voz.", false);
		}

	}

	public void execVoiceSearch(){
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
		}else if(requestCode == Tools.DISPLAY_WORD_MAKER && resultCode == RESULT_OK){
			contact = data.getStringExtra("typedString");
		}
		
		if(resultCode == RESULT_OK){
			Tools.speak("Buscando por " + contact, true);
			searchContact(contact);
		}
	}

	public void displayAllContacts(){

		foundContacts = new LinkedList<Bean_Contact>();

		Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, ContactsContract.RawContacts.DELETED + " = '0'", null, ContactsContract.Data.DISPLAY_NAME);
		if(c.moveToFirst()){
			processContactsCursor(c);
			Tools.speak("Exibindo " + foundContacts.size() + " contatos.", true);
			displayContactsChooser("Todos");
		}else{
			Tools.speak("Você não tem contatos na sua agenda telefônica.", true);
		}
	}

	
	public void processContactsCursor(final Cursor c){
		Tools.speak("Aguarde, estou pensando...", true);
		final StringBuilder contact = new StringBuilder();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (c) {
					do{
						contact.delete(0, contact.length());
						//int _id = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
						int _raw_id = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
					

						
						ArrayList<ArrayList<String>> phones = getPhonesFromContactId(_raw_id);
						ArrayList<ArrayList<String>> emails = getEmailsFromContactId(_raw_id);
						Bean_Contact bc = new Bean_Contact(0, _raw_id, c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)), emails, phones);
						foundContacts.add(bc);
						

					}while(c.moveToNext());
					
					c.close();
					c.notify();
				}

			}
		});
		t.start();

		synchronized (c) {
			try {
				c.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				pd.dismiss();
				Tools.speak("Algo deu errado. Tente novamente.", false);
			}
		}


	}

	public void searchContact(String contact){
		foundContacts = new LinkedList<Bean_Contact>();

		Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null, ContactsContract.Data.DISPLAY_NAME + " like '%" + contact + "%' AND " + ContactsContract.RawContacts.DELETED + " = '0'", null, null);
		if(c.moveToFirst()){

			processContactsCursor(c);


			if(foundContacts.size() == 1){
				Bundle b = new Bundle();
				Tools.speak(String.format(getResources().getString(R.string.found), foundContacts.get(0).getName()), true);
				b.putSerializable("contact", foundContacts.get(0));
				Intent foundContact = new Intent(this, ContactInformation.class);
				foundContact.putExtras(b);
				startActivity(foundContact);
			}
			else {
				displayContactsChooser(contact);
				Tools.speak("Mais de um contato corresponde à esse nome. Exibindo lista, escolha um.", true);
			}

		}
		else Tools.speak("O contato " + contact + " não foi encontrado na sua agenda.", true);
	}

	public void displayContactsChooser(String contact){
		Bundle b = new Bundle();
		b.putSerializable("foundContacts", foundContacts);
		b.putString("searchString", contact);
		Intent listFoundContacts = new Intent(this, ContactsList.class);
		listFoundContacts.putExtras(b);
		startActivity(listFoundContacts);
	}


	public ArrayList<ArrayList<String>> getPhonesFromContactId(int _id){
		
		ArrayList<ArrayList<String>> phones = new ArrayList<ArrayList<String>>();

		Cursor phoneCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ? ", new String[]{ Integer.toString(_id) }, null);

		if(phoneCursor != null){

			for (phoneCursor.moveToFirst(); !phoneCursor.isAfterLast(); phoneCursor.moveToNext()) {

				// Get a phone number
				String phoneNumber = phoneCursor.getString(phoneCursor
						.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

				int row_id = phoneCursor.getInt(phoneCursor
						.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
				String phoneType = "";
				int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				switch (type) {
				case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
					phoneType = "Residência";
					break;
				case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
					phoneType = "Celular";
					break;
				case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
					phoneType = "Outros";
					break;
				case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
					phoneType = "Trabalho";
					break;
				}
				
				ArrayList<String> phone = new ArrayList<String>();
				phone.add(phoneType);
				phone.add(phoneNumber);
				phone.add(String.valueOf(type));
				phone.add(String.valueOf(row_id));

				phones.add(phone);
			}

		}
		phoneCursor.close();
		return phones;
	}

	public ArrayList<ArrayList<String>> getEmailsFromContactId(int _id){
		ArrayList<ArrayList<String>> emails = new ArrayList<ArrayList<String>>();

		Cursor emailCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = ? ", new String[]{ Integer.toString(_id) }, null);

		if(emailCursor != null){

			for (emailCursor.moveToFirst(); !emailCursor.isAfterLast(); emailCursor.moveToNext()) {

				// Get an e-mail
				String emailAddress = emailCursor.getString(emailCursor
						.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA1));
				int row_id = emailCursor.getInt(emailCursor
						.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email._ID));
				String emailType = "";
				int type = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				switch (type) {
				case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
					emailType = "Residência";
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
					emailType = "Outros";
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
					emailType = "Trabalho";
					break;
				}

				
				ArrayList<String> email = new ArrayList<String>();
				email.add(emailType);
				email.add(emailAddress);
				email.add(String.valueOf(type));
				email.add(String.valueOf(row_id));

				emails.add(email);

			}
		}
		emailCursor.close();
		return emails;
	}

	@Override
	public void onClick(View v) {

		Tools.speak("Selecionado " + ((Button)v).getText().toString(), false);

		Intent i = null;
		
		switch (v.getId()) {
		case R.id.btnAllContacts:
			displayAllContacts();
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
