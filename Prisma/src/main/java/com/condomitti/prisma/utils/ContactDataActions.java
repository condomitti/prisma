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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.condomitti.prisma.R;

public class ContactDataActions extends SuperActivity implements
		OnClickListener{
	public static final int ACTION_EDIT = 63452;
	public static final int ACTION_DELETE = 75634;

	private Button btnCancel, btnRemove, btnEdit = null;
	private List<HashMap<String, Object>> actions = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);

		Tools.speak(getResources().getString(R.string.select_action), true);

		setupContents();
		makeUpScreen();

	}

	public void makeUpScreen() {
		Dialog dialog = new SuperDialog(this);
		dialog.setContentView(R.layout.contact_data_action);
		dialog.setCancelable(false);
		dialog.setTitle(getResources().getString(R.string.select_type));

		/**
		 * Retrieves references
		 */
		btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnRemove = (Button) dialog.findViewById(R.id.btnRemove);
		btnEdit = (Button) dialog.findViewById(R.id.btnEdit);

		/**
		 * Sets listeners
		 */
		btnCancel.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
		btnRemove.setOnClickListener(this);

		dialog.show();

	}

	public void setupContents() {
		actions = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> t1 = new HashMap<String, Object>();
		t1.put("txtAction", getResources().getString(R.string.edit));
		t1.put("action", ACTION_EDIT);

		HashMap<String, Object> t2 = new HashMap<String, Object>();
		t2.put("txtAction", getResources().getString(R.string.remove));
		t2.put("action", ACTION_DELETE);

		actions.add(t1);
		actions.add(t2);

	}

	@Override
	public void onClick(View v) {
		int cAction = 0;
		Tools.speak("Selecionado " + ((Button)v).getText(), false);

		switch (v.getId()) {
		case R.id.btnCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.btnRemove:
			cAction = ACTION_DELETE;
			break;
		case R.id.btnEdit:
			cAction = ACTION_EDIT;
			break;
		}
		
		setResult(cAction);
		finish();
	}


	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}
}
