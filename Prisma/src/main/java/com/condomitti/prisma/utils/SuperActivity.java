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

import android.app.Activity;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.condomitti.prisma.R;

/**
 * This class is responsible for controlling user interaction with on screen components (mainly buttons) and navigation
 * This class reads all child's focusable elements. Every child class should call loadFocusable([parent]) after setContentView(...) was called. Not calling it will make the screen not browseable.
 */
public class SuperActivity extends Activity {
	boolean gonnaPerformAction;
	ArrayList<View> focusables;
	InputMethodService inputMethodService = new InputMethodService();

	protected void loadTouchables() {
		View parent = findViewById(R.id.parentLayout);
		focusables = parent.getFocusables(View.FOCUS_DOWN);
	}

	protected void loadTouchables(View parent) {
		focusables = parent.getFocusables(View.FOCUS_DOWN);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

        //Control buttons (back, menu, search, camera, power) disabled by default
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_SEARCH:
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_POWER:
			return true;

        //Volume control buttons are used to navigate throughout screen components
		case KeyEvent.KEYCODE_VOLUME_DOWN:

			changeFocus(View.FOCUS_DOWN);

			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:

			changeFocus(View.FOCUS_UP);
			return true;
		}
		return true;
		//return super.onKeyDown(keyCode, event);
	}

    /**
     * Changes the focus based on known components (read by loadTouchables())
     * @param direction
     */
	private void changeFocus(int direction) {

		int focused = getCurrentFocused();
		Log.i("PRISMA","Changing focus");
		if (direction == View.FOCUS_DOWN) {

			//FOCUS_DOWN
			if (focused < focusables.size() - 1) {
				focusables.get(focused + 1).requestFocus();
			}
		} else {

			//FOCUS_UP
			if (focused > 1) {
				focusables.get(focused - 1).requestFocus();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		if (event.getPointerCount() == 2) {
			gonnaPerformAction = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP
				&& event.getPointerCount() == 1 && gonnaPerformAction) {
			
			if(getCurrentFocus() instanceof ListView) Log.i("PRISMA", "listView");
			else getCurrentFocus().performClick();

			gonnaPerformAction = false;
			
		}

		return true;
	}

	private int getCurrentFocused() {
		int i = 0;
		for (View v : focusables) {
			if (v.isFocused())
				return i;
			i++;
		}

		return i;
	}
	
	

}
