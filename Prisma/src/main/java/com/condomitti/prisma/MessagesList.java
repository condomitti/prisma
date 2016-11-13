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
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.SuperDialog;
import com.condomitti.prisma.utils.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessagesList extends SuperActivity implements OnClickListener{
	LinearLayout listMessages = null;
	Cursor messages = null;
	boolean loading = true;
	ArrayList<HashMap<String, String>> messagesLog = new ArrayList<HashMap<String,String>>();
	int numberTocall = 0;
	Dialog dialog;
	int counter = 0;
	String currentBody;
	
	Button btnReadMessage, btnReplyMessage, btnCancel, btnDeleteMessage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.setScreenSettings(this);
		getMessages();
		makeUpScreen();
	}

	public void getMessages(){
		messages = getContentResolver().query(Uri.parse( "content://sms/inbox"), null, null, null, "date desc" );
		if(!messages.moveToFirst()){
			Tools.speak(getResources().getString(R.string.noMessages), true);
			finish();
		}else{
			StringBuilder sb = new StringBuilder();
			for(String s : messages.getColumnNames()){
				sb.append(s + ", " );
			}
			Log.i("PRISMA", "content://sms/inbox Indexes: " + sb.toString());
		}
	}

	public void makeUpScreen(){

		setContentView(R.layout.messages_list);
		

		listMessages = (LinearLayout)findViewById(R.id.listSMS);
		
		
		
		
		TextView tvBack = new TextView(getApplicationContext());
		tvBack.setText("Voltar");
		if(Build.VERSION.SDK_INT < 23){

			tvBack.setTextAppearance(this, R.style.ListsTextView);

		}else{

			tvBack.setTextAppearance(R.style.ListsTextView);

		}
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
		listMessages.addView(tvBack, counter, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		

		if(messages.moveToFirst()){
			do{
				counter++;
				HashMap<String, String> m = new HashMap<String, String>();

                String str = "";
				try{

					str = messages.getString(messages.getColumnIndex("person")) != null ? Tools.getPersonNameById(getApplicationContext(), Long.parseLong(messages.getString(messages.getColumnIndex("person")))) : Tools.handleNumber(messages.getString(messages.getColumnIndex("address")));

				}catch (Exception e){
					str = "Número desconhecido";
				}

				
				str = Tools.getSpokenFormattedDate(new Date(messages.getLong(messages.getColumnIndex("date")))) + " " + " " + getResources().getString(R.string.message_from) + " " + str;
				m.put("_id", Integer.toString(messages.getInt(messages.getColumnIndex("_id"))));
				m.put("body", messages.getString(messages.getColumnIndex("body")));
				m.put("strMessage", str);
				m.put("number", messages.getString(messages.getColumnIndex("address")));
				messagesLog.add(counter-1,m);

				TextView tv = new TextView(getApplicationContext());
				tv.setText(str);
				tv.setFocusable(true);
                if(Build.VERSION.SDK_INT < 23){

                    tv.setTextAppearance(this, R.style.ListsTextView);

                }else{

                    tv.setTextAppearance(R.style.ListsTextView);
                }
				tv.setFocusableInTouchMode(true);
				tv.setVisibility(TextView.GONE);
				final String _toSpeak = str; 
				tv.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(v.isFocused()){
							setMessageAsNotNew(Integer.parseInt(messagesLog.get(counter-1).get("_id")));
							Tools.speak(_toSpeak, false);
						}
					}
				});
				tv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Tools.speak("Selecionado" + ((TextView)v).getText().toString(), false);
							currentBody = messagesLog.get(counter-1).get("body");
							makeUpDialog();
					}
				});
				listMessages.addView(tv, listMessages.getChildCount(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

				
				
			}while(messages.moveToNext());
		}

		Tools.speak("Exibindo "+ messagesLog.size() + " mensagens", true);
		
		counter = 0;
		super.loadTouchables();
	}

	
	public void makeUpDialog(){
		
		Bundle b = getIntent().getExtras();
		
		String contactName = "Mensagem";
		
		dialog = new SuperDialog(this);
		dialog.setContentView(R.layout.sms_message_options);
		dialog.setCancelable(false);
		dialog.setTitle(contactName);
		
				
		/**
		 * Retrieves references
		 */
		btnReadMessage = (Button)dialog.findViewById(R.id.btnReadMessage);
		btnReplyMessage = (Button)dialog.findViewById(R.id.btnReplyMessage);
		btnDeleteMessage = (Button)dialog.findViewById(R.id.btnDeleteMessage);
		btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
		   
		/**
		 * Sets Listeners
		 */
		
		btnReadMessage.setOnClickListener(this);
		btnReplyMessage.setOnClickListener(this);
		btnDeleteMessage.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		dialog.show();
		Tools.speak("O que deseja fazer?", true);
	}
	
	
	
	public void setMessageAsNotNew(int _id){
		final Uri SMS_INBOX = Uri.parse("content://sms/");
		Log.i("PRISMA", "Setting as read...");
		ContentValues cv = new ContentValues();
		cv.put("read", true);
		getContentResolver().update(SMS_INBOX, cv, "_id = " + _id, 
				null);
		getContentResolver().notifyChange(SMS_INBOX, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Tools.SHOW_SPOKEN_DIALOG:
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED ) {
                if (resultCode == SpokenDialog.RESULT_YES) {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel://" + numberTocall));
                    startActivityForResult(i, Tools.CALL_PHONE_ACTIVITY);
                }
            }else{
                Tools.speak("Impossível efetuar ligação. Permissão não foi dada no início do aplicativo.",true);
            }
			break;
		}
	}


	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}
	
	@Override
	public void onClick(View v) {
		
		Tools.speak("Selecionado " + ((Button)v).getText().toString(), false);
		
		switch (v.getId()) {
		case R.id.btnReadMessage:
			Tools.speak(currentBody,true);
			break;
		case R.id.btnReplyMessage:
			break;
		case R.id.btnDeleteMessage:
			break;
		case R.id.btnCancel:
			dialog.cancel();
			//dialog.dismiss();
			break;

		}
	}
	public void changeVisibility(int direction){
		
		listMessages.getChildAt(counter).setVisibility(View.GONE);;
		if(direction == View.FOCUS_DOWN){
			counter++;
		}else{
			counter--;
		}
		listMessages.getChildAt(counter).setVisibility(View.VISIBLE);
		listMessages.getChildAt(counter).requestFocus();
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(counter < messagesLog.size()) {
                    changeVisibility(View.FOCUS_DOWN);
                }

			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			
			if(counter > 0)
				changeVisibility(View.FOCUS_UP);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}	

}
