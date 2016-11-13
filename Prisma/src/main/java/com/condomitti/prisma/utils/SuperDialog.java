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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.condomitti.prisma.R;

public class SuperDialog extends Dialog {

	boolean gonnaPerformAction;
	ArrayList<View> dialogFocusables;

	
	
	public SuperDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadTouchables(findViewById(R.id.parentLayout));
	}
	
	protected void loadTouchables() {
		View parent = findViewById(R.id.parentLayout);
		dialogFocusables = parent.getFocusables(View.FOCUS_DOWN);
		Log.i("PRISMA", "[Dialog] Focusables: " + dialogFocusables.size());
	}
	
	protected void loadTouchables(View parent, View... extras) {
		dialogFocusables = parent.getFocusables(View.FOCUS_DOWN);
		for(View v : extras){
			dialogFocusables.addAll(v.getFocusables(View.FOCUS_DOWN));
		}
		Log.i("PRISMA", "[Dialog] Focusables: " + dialogFocusables.size());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_SEARCH:
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_POWER:
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:

			changeFocus(View.FOCUS_DOWN);

			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:

			changeFocus(View.FOCUS_UP);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void changeFocus(int direction) {
		int focused = getCurrentFocused();

		if (direction == View.FOCUS_DOWN) {
			if (focused < dialogFocusables.size() - 1) {
				dialogFocusables.get(focused + 1).requestFocus();
			}
		} else {
			if (focused > 1) {
				dialogFocusables.get(focused - 1).requestFocus();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		
		if (event.getPointerCount() == 2) {
			gonnaPerformAction = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP
				&& event.getPointerCount() == 1 && gonnaPerformAction) {
			getCurrentFocus().performClick();
			gonnaPerformAction = false;
		}

		return true;
	}

	private int getCurrentFocused() {
		int i = 0;
		for (View v : dialogFocusables) {
			if (v.isFocused())
				return i;
			i++;
		}

		return i;
	}

}
