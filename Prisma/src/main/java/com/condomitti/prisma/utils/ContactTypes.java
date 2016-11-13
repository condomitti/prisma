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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.condomitti.prisma.R;

public class ContactTypes extends Activity implements OnClickListener{
	public static final String TYPE_EMAIL = "email"; 
	public static final String TYPE_PHONE = "phone"; 
	private Button btnCancel,btnTypeHome, btnTypeWork, btnTypeOther, btnTypeMobile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);
		
		makeUpScreen();
		
	}
	
	public void makeUpScreen(){
		
		SuperDialog dialog = new SuperDialog(this);
		dialog.setContentView(R.layout.contact_type);
		
		dialog.setCancelable(false);
		dialog.setTitle(getResources().getString(R.string.select_type));

		/**
		 * Retrieves references
		 */
		btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
		//listContactTypes = (LinearLayout)dialog.findViewById(R.id.listContactTypes);
		btnTypeHome = (Button)dialog.findViewById(R.id.btnTypeHome);
		btnTypeWork = (Button)dialog.findViewById(R.id.btnTypeWork);
		btnTypeOther = (Button)dialog.findViewById(R.id.btnTypeOther);
		btnTypeMobile = (Button)dialog.findViewById(R.id.btnTypeMobile);
		
		
		

		/**
		 * Sets listeners
		 */
		btnCancel.setOnClickListener(this);
		btnTypeHome.setOnClickListener(this);
		btnTypeWork.setOnClickListener(this);
		btnTypeOther.setOnClickListener(this);
		btnTypeMobile.setOnClickListener(this);
		

		if(getIntent().getStringExtra("type").equals(ContactTypes.TYPE_EMAIL)){
			btnTypeMobile.setVisibility(Button.GONE);
		}

		dialog.show();


	}

	@Override
	public void onClick(View v) {
		
		String type = getIntent().getStringExtra("type");
		Tools.speak("Selecionado " + ((Button)v).getText(), false);
		Intent data = new Intent();
		int cType = 0;
		
		switch (v.getId()) {
		case R.id.btnCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.btnTypeHome:
			if(type.equals(TYPE_EMAIL)){
				cType = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
			}else{
				cType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
			}
			break;
		case R.id.btnTypeWork:
			if(type.equals(TYPE_EMAIL)){
				cType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
			}else{
				cType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
			}
			break;
		case R.id.btnTypeOther:
			if(type.equals(TYPE_EMAIL)){
				cType = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
			}else{
				cType = ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
			}
			break;
		case R.id.btnTypeMobile:
			if(type.equals(TYPE_EMAIL)){
				cType = ContactsContract.CommonDataKinds.Email.TYPE_MOBILE;
			}else{
				cType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
			}
			break;

		}
		data.putExtra("contactType", cType);
		setResult(RESULT_OK, data);
		finish();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_SEARCH:
		case KeyEvent.KEYCODE_CAMERA:
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}
}
