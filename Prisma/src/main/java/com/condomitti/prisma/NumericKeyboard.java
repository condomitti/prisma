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
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class NumericKeyboard extends SuperActivity implements OnClickListener {

	TextView telephoneDisplay = null;
	Button btnNum0, btnNum1, btnNum2, btnNum3, btnNum4, btnNum5, btnNum6,
			btnNum7, btnNum8, btnNum9, btnNumAst, btnNumHash = null;
	Button btnCheckNumber, btnErase, btnDeleteLastNumber, btnProceed,
			btnBack = null;
	ScrollView container = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);

		makeUpScreen();
		setupInitValue();

	}

	public void setupInitValue() {
		String number = getIntent().getStringExtra("number");
		if (number != null) {
			telephoneDisplay.setText(number);
		}
	}

	public void makeUpScreen() {
		setContentView(R.layout.numeric_keyboard);
		super.loadTouchables();

		/**
		 * Retrieves references
		 */
		container = (ScrollView) findViewById(R.id.scrollNumKeyboard);
		btnNum0 = (Button) findViewById(R.id.btnNum0);
		btnNum1 = (Button) findViewById(R.id.btnNum1);
		btnNum2 = (Button) findViewById(R.id.btnNum2);
		btnNum3 = (Button) findViewById(R.id.btnNum3);
		btnNum4 = (Button) findViewById(R.id.btnNum4);
		btnNum5 = (Button) findViewById(R.id.btnNum5);
		btnNum6 = (Button) findViewById(R.id.btnNum6);
		btnNum7 = (Button) findViewById(R.id.btnNum7);
		btnNum8 = (Button) findViewById(R.id.btnNum8);
		btnNum9 = (Button) findViewById(R.id.btnNum9);
		btnNumHash = (Button) findViewById(R.id.btnNumHash);
		btnNumAst = (Button) findViewById(R.id.btnNumAst);
		btnErase = (Button) findViewById(R.id.btnErase);
		btnDeleteLastNumber = (Button) findViewById(R.id.btnDeleteLastNumber);
		btnCheckNumber = (Button) findViewById(R.id.btnCheckNumber);
		btnProceed = (Button) findViewById(R.id.btnProceed);
		btnBack = (Button) findViewById(R.id.btnBack);
		telephoneDisplay = (TextView) findViewById(R.id.telephoneDisplay);

		/**
		 * Sets Listeners
		 */
		btnNum0.setOnClickListener(this);
		btnNum1.setOnClickListener(this);
		btnNum2.setOnClickListener(this);
		btnNum3.setOnClickListener(this);
		btnNum4.setOnClickListener(this);
		btnNum5.setOnClickListener(this);
		btnNum6.setOnClickListener(this);
		btnNum7.setOnClickListener(this);
		btnNum8.setOnClickListener(this);
		btnNum9.setOnClickListener(this);
		btnNumHash.setOnClickListener(this);
		btnNumAst.setOnClickListener(this);
		btnCheckNumber.setOnClickListener(this);
		btnErase.setOnClickListener(this);
		btnDeleteLastNumber.setOnClickListener(this);
		btnProceed.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		btnNumHash.setOnFocusChangeListener(specialCharListener);

	}

	public void addToDisplay(char s) {
		StringBuilder strNumber = new StringBuilder(telephoneDisplay.getText());
		strNumber.append(s);
		telephoneDisplay.setText(strNumber.toString());
	}

	public void eraseDisplay() {
		telephoneDisplay.setText(null);
	}

	public void proceed() {
		if (telephoneDisplay.getText().toString().length() == 0) {
			Tools.speak("Impossível continuar. Nenhum número foi digitado.",
					false);
		} else {
			Intent data = new Intent();
			data.putExtra("typedNumber", telephoneDisplay.getText().toString());
			setResult(RESULT_OK, data);
			finish();
		}
	}

	public void removeLastDigit() {
		StringBuilder currentNumber = new StringBuilder(telephoneDisplay
				.getText().toString());
		currentNumber
				.delete(currentNumber.length() - 1, currentNumber.length());

		telephoneDisplay.setText(currentNumber);
	}

	@Override
	public void onClick(View v) {

		Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

		switch (v.getId()) {
		case R.id.btnNum0:
			addToDisplay('0');

			break;
		case R.id.btnNum1:
			addToDisplay('1');

			break;
		case R.id.btnNum2:
			addToDisplay('2');

			break;
		case R.id.btnNum3:
			addToDisplay('3');

			break;
		case R.id.btnNum4:
			addToDisplay('4');

			break;
		case R.id.btnNum5:
			addToDisplay('5');

			break;
		case R.id.btnNum6:
			addToDisplay('6');

			break;
		case R.id.btnNum7:
			addToDisplay('7');

			break;
		case R.id.btnNum8:
			addToDisplay('8');

			break;
		case R.id.btnNum9:
			addToDisplay('9');

			break;
		case R.id.btnNumHash:
			addToDisplay('#');

			break;
		case R.id.btnNumAst:
			addToDisplay('*');

			break;
		case R.id.btnProceed:
			proceed();

			break;
		case R.id.btnCheckNumber:
			checkNumber();

			break;
		case R.id.btnErase:
			eraseDisplay();

			break;
		case R.id.btnDeleteLastNumber:
			removeLastDigit();

			break;
		case R.id.btnBack:
			setResult(RESULT_CANCELED);
			finish();

			break;
		}

		telephoneDisplay.requestFocus();

	}

	public void checkNumber() {
		if (telephoneDisplay.getText().toString().length() == 0)
			Tools.speak("Nenhum número foi digitado", true);
		else
			Tools.speak(telephoneDisplay.getText().toString(), true, true, true);
	}

	OnFocusChangeListener specialCharListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.isFocused()) {
				String s = null;
				if (v instanceof Button)
					s = ((Button) v).getText().toString();

				Tools.speak(Tools.checkSpecialChars(s, NumericKeyboard.this), false);
			}
		}
	};

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}
}
