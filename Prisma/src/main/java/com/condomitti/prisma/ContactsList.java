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

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

import java.util.ArrayList;

public class ContactsList extends SuperActivity implements OnClickListener{

	private static final int OPEN_CONTACT = 45354;
	ArrayList<Bean_Contact> foundContacts = null;
	LinearLayout listContacts = null;
	boolean loading = true;
	TextView title;
	int counter = 0;
	View viewingContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);

		makeUpScreen();

	}

	@SuppressWarnings("unchecked")
	public void makeUpScreen(){

		setContentView(R.layout.contacts_list);
		
		
		
		title = (TextView)findViewById(R.id.txtTitle);
		title.setText("Resultado da busca por: " + getIntent().getStringExtra("searchString"));



		listContacts = (LinearLayout)findViewById(R.id.listContacts);
		
		
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
		listContacts.addView(tvBack, counter, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		foundContacts = (ArrayList<Bean_Contact>)getIntent().getSerializableExtra("foundContacts");
		for(final Bean_Contact contact : foundContacts){
			final TextView tv = new TextView(getApplicationContext());
			tv.setText(contact.getName());
			tv.setFocusable(true);
			tv.setTextAppearance(this, R.style.ListsTextView);
			tv.setFocusableInTouchMode(true);
			tv.setVisibility(TextView.GONE);
			tv.setOnFocusChangeListener(Tools.pfListenerQueue);
			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Tools.speak("Selecionado " + contact.getName(), true);
					Intent i = new Intent(ContactsList.this, ContactInformation.class);
					i.putExtra("contact", contact);
					viewingContact = tv;
					startActivityForResult(i,OPEN_CONTACT);
				}
			});
			counter++;
			listContacts.addView(tv, counter, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		}
		counter = 0;

		
		
		tvBack.requestFocus();

		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		super.loadTouchables(listContacts);
	}


	@Override
	public void onClick(View v) {
		Tools.speak("Selecionado " + ((Button)v).getText().toString(), false);

		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case OPEN_CONTACT:
			if(resultCode == ContactInformation.CONTACT_DELETED){
				listContacts.removeView(viewingContact);
			}
			finish();
			super.loadTouchables();
			//Tools.speak("Exibindo " + foundContacts.size() + " contatos.", true);
			break;

		}
	}
	public void changeVisibility(int direction){
		
		listContacts.getChildAt(counter).setVisibility(View.GONE);
		if(direction == View.FOCUS_DOWN){
			counter++;
		}else{
			counter--;
		}
		listContacts.getChildAt(counter).setVisibility(View.VISIBLE);
		listContacts.getChildAt(counter).requestFocus();
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if(counter < foundContacts.size())
				changeVisibility(View.FOCUS_DOWN);

			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			
			if(counter > 0)
				changeVisibility(View.FOCUS_UP);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}

}
