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
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.condomitti.prisma.GeneralExec;
import com.condomitti.prisma.R;

public class SpokenDialog extends SuperActivity implements OnClickListener {
	public static final int RESULT_YES = 123455432;
	public static final int RESULT_NO = 123455433;
    SuperDialog dialog;

	CountDownTimer cdtCloser = new CountDownTimer(5000, 2000) {

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			finish();
		}
	};

	Button btnYes, btnNo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);
		makeUpDialog(savedInstanceState);

	}

	private void makeUpDialog(Bundle savedInstanceState) {

        String dialogMessage = getIntent()
				.getStringExtra("prismaDialogMessage");
        boolean prismaDialogHasCloseTimer = getIntent().getBooleanExtra(
				"prismaDialogHasCloseTimer", false);

        setContentView(R.layout.dialog);
        super.loadTouchables();

		TextView tv = (TextView) findViewById(R.id.txtDialogText);
		tv.setFocusable(true);
		tv.setText(dialogMessage);
		tv.requestFocus();

		btnYes = (Button) findViewById(R.id.btnDialogYes);
		btnNo = (Button) findViewById(R.id.btnDialogNo);

		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		btnYes.setOnFocusChangeListener(Tools.pfListenerQueue);
		btnNo.setOnFocusChangeListener(Tools.pfListenerQueue);


		if (prismaDialogHasCloseTimer) {
			GeneralExec exec = new GeneralExec() {
				@Override
				public void exec() {
					cdtCloser.start();
				}
			};
			Tools.speak(dialogMessage, false, false, exec, false);
		} else {
			Tools.speak(dialogMessage, true);
		}
	}

	private void makeUpDialog2(Bundle savedInstanceState) {

        destroyDialog();
        dialog = new SuperDialog(this);
        String dialogMessage = getIntent()
				.getStringExtra("prismaDialogMessage");
        boolean prismaDialogHasCloseTimer = getIntent().getBooleanExtra(
				"prismaDialogHasCloseTimer", false);

		dialog.setContentView(R.layout.dialog);
		dialog.setCancelable(false);
		dialog.setTitle("Confirmar ação");

		TextView tv = (TextView) dialog.findViewById(R.id.txtDialogText);
		tv.setFocusable(true);
		tv.setText(dialogMessage);
		tv.requestFocus();

		btnYes = (Button) dialog.findViewById(R.id.btnDialogYes);
		btnNo = (Button) dialog.findViewById(R.id.btnDialogNo);

		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		btnYes.setOnFocusChangeListener(Tools.pfListenerQueue);
		btnNo.setOnFocusChangeListener(Tools.pfListenerQueue);


		dialog.show();

		if (prismaDialogHasCloseTimer) {
			GeneralExec exec = new GeneralExec() {
				@Override
				public void exec() {
					cdtCloser.start();
				}
			};
			Tools.speak(dialogMessage, false, false, exec, false);
		} else {
			Tools.speak(dialogMessage, true);
		}
	}

    @Override
    protected void onStop() {
        super.onStop();
        destroyDialog();

    }

    private void destroyDialog(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
	public void onClick(View v) {
		Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

		Intent data = new Intent();
		data.putExtra("prismaDialogReqCode",
				getIntent().getExtras().getInt("prismaDialogReqCode", -1));

		switch (v.getId()) {
		case R.id.btnDialogYes:
			setResult(RESULT_YES, data);
			break;
		case R.id.btnDialogNo:
			setResult(RESULT_NO, data);
			break;
		}

		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_SEARCH:
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}
}
