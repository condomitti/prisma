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

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;
import com.condomitti.prisma.widgets.UntouchableButton;

public class ContactInformation extends SuperActivity implements OnClickListener{
	private static final int CONFIRM_DELETION = 346521;
	public static final int CONTACT_DELETED = 376534;
	Button btnEditContact, btnDeleteContact, btnBack = null;
	LinearLayout llContactData = null;
	Bean_Contact contact = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);
		
		retrieveContact();
		makeUpScreen();
	}

	public void retrieveContact(){
		contact = (Bean_Contact)getIntent().getSerializableExtra("contact");
		Tools.speak("Exibindo informações do contato " + contact.getName(), true);
	}
	
	

	public void makeUpScreen(){
		setContentView(R.layout.contact_information);
		
		
		
		TextView title = (TextView)findViewById(R.id.titleContactInformation);
		title.setFocusable(true);
		title.requestFocus();
		title.setText(contact.getName());


		/**
		 * Retrieves references
		 */
		btnEditContact = (Button)findViewById(R.id.btnEditContact);
		btnDeleteContact = (Button)findViewById(R.id.btnDeleteContact);
		btnBack = (Button)findViewById(R.id.btnBack);
		llContactData = (LinearLayout)findViewById(R.id.llContactData);

		/**
		 * Sets Listeners
		 */
		btnEditContact.setOnClickListener(this);
		btnDeleteContact.setOnClickListener(this);
		btnBack.setOnClickListener(this);
//		btnEditContact.setOnFocusChangeListener(Tools.pfListener);
//		btnDeleteContact.setOnFocusChangeListener(Tools.pfListener);
//		btnBack.setOnFocusChangeListener(Tools.pfListener);

		setUpContactData();
		
		
		super.loadTouchables();

	}

	public void setUpContactData(){
		for(ArrayList<String> phone : contact.getPhones()){
			Button newPhone = new UntouchableButton(this);
			newPhone.setText(getResources().getString(R.string.telephone) + " " + phone.get(0) + ": " + Tools.handleNumber(phone.get(1)));

			newPhone.setOnClickListener(this);
//			newPhone.setOnFocusChangeListener(Tools.pfListener);

			llContactData.addView(newPhone);
		}

		for(ArrayList<String> email : contact.getEmails()){
			Button newEmail = new UntouchableButton(this);
			newEmail.setText(getResources().getString(R.string.email) + " " + email.get(0) + ": " + email.get(1));

//			newEmail.setOnFocusChangeListener(Tools.pfListener);

			llContactData.addView(newEmail);
		}
	}
	
	public void handleDataButton(Button button){
		/**
		 * disassemble the label to retrieve the number
		 */
		String s = button.getText().toString(); 
		s = s.substring(s.indexOf(":") + 1);
		s = s.replace(" ", "");
		
		Intent i = new Intent(this, ContactTelephoneOptions.class);
		i.putExtra("contactName", contact.getName());
		i.putExtra("contactPhone", s);
		
		startActivity(i);
		
	}

	@Override
	public void onClick(View v) {

		Tools.speak("Selecionado " + ((Button)v).getText().toString(), false);
		Intent i = null;
		
		if(v.getId() > 0){

			switch (v.getId()) {
			case R.id.btnEditContact:
				i = new Intent(this, Contact.class);
				i.putExtra("contact", contact);
				startActivity(i);
				finish();
				break;
			case R.id.btnDeleteContact:
				i = new Intent(this, SpokenDialog.class);
				String confirmationMessage = String.format(getResources().getString(R.string.deletion_confirmation), contact.getName());
				i.putExtra("prismaDialogMessage", confirmationMessage);
				startActivityForResult(i, CONFIRM_DELETION);
				break;

			case R.id.btnBack:
				finish();

				break;
			}
		} else {
			handleDataButton((Button)v);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case CONFIRM_DELETION:
			if(resultCode == SpokenDialog.RESULT_YES){
				deleteContact();
				setResult(CONTACT_DELETED);
				finish();
			}
			break;
		}
	}
	
	public void deleteContact(){
		int deleted = getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts._ID + " = ?", new String[]{ Integer.toString(contact.get_raw_id()) });
		Toast.makeText(this, "Apagados: " + deleted, Toast.LENGTH_LONG).show();
		String msg = String.format(getResources().getString(R.string.deletion_completed), contact.getName());
		Tools.speak(msg, true);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}

}
