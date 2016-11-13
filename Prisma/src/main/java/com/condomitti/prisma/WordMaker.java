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

import java.util.Locale;
import android.annotation.SuppressLint;
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

public class WordMaker extends SuperActivity implements OnClickListener{
	Button btnCheck = null;
	Button btnProceed = null;
	Button btnErase = null;
	Button btnDeleteLastLetter = null;
	Button btnCancel = null;
	Button btnToggleUppercase = null;
	Button btnPeriod, btnColon, btnComma, btnSemiColon, btnSlash, btnPlus,
	btnMinus, btnEquals, btnQuestion, btnExclamation, btnSingleQuote, btnDoubleQuote,
	btnSpace, btnA, btnB, btnC, btnD, btnE, btnF, btnG, btnH, btnI, btnJ,
	btnK, btnL, btnM, btnN, btnO, btnP, btnQ, btnR, btnS, btnT,
	btnU, btnV, btnW, btnX, btnY, btnZ, btn0, btn1, btn2, btn3,
	btn4, btn5, btn6, btn7, btn8, btn9 = null;

	ScrollView container = null;
	TextView txtTypedLetters = null;
	boolean isUppercaseEnabled = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tools.setScreenSettings(this);

		makeUpScreen();
	}

	public void makeUpScreen(){
		setContentView(R.layout.word_maker);
		super.loadTouchables();

		/**
		 * Retrieves references
		 */
		container = (ScrollView)findViewById(R.id.scrollContainer);
		btnCheck = (Button)findViewById(R.id.btnCheck);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnProceed = (Button)findViewById(R.id.btnContinue);
		btnErase = (Button)findViewById(R.id.btnErase);
		btnDeleteLastLetter = (Button)findViewById(R.id.btnDeleteLastLetter);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnToggleUppercase = (Button)findViewById(R.id.btnToggleUppercase);
		txtTypedLetters = (TextView)findViewById(R.id.txtTypedLetters);
		btnPeriod = (Button)findViewById(R.id.btnPeriod);
		btnColon = (Button)findViewById(R.id.btnColon);
		btnComma = (Button)findViewById(R.id.btnComma);
		btnSemiColon = (Button)findViewById(R.id.btnSemiColon);
		btnSlash = (Button)findViewById(R.id.btnSlash);
		btnPlus = (Button)findViewById(R.id.btnPlus);
		btnMinus = (Button)findViewById(R.id.btnMinus);
		btnEquals = (Button)findViewById(R.id.btnEquals);
		btnQuestion = (Button)findViewById(R.id.btnQuestion);
		btnExclamation = (Button)findViewById(R.id.btnExclamation);
		btnSingleQuote = (Button)findViewById(R.id.btnSingleQuote);
		btnDoubleQuote = (Button)findViewById(R.id.btnDoubleQuote);
		btnSpace = (Button)findViewById(R.id.btnSpace);
		btnA = (Button)findViewById(R.id.btnA);
		btnB = (Button)findViewById(R.id.btnB);
		btnC = (Button)findViewById(R.id.btnC);
		btnD = (Button)findViewById(R.id.btnD);
		btnE = (Button)findViewById(R.id.btnE);
		btnF = (Button)findViewById(R.id.btnF);
		btnG = (Button)findViewById(R.id.btnG);
		btnH = (Button)findViewById(R.id.btnH);
		btnI = (Button)findViewById(R.id.btnI);
		btnJ = (Button)findViewById(R.id.btnJ);
		btnK = (Button)findViewById(R.id.btnK);
		btnL = (Button)findViewById(R.id.btnL);
		btnM = (Button)findViewById(R.id.btnM);
		btnN = (Button)findViewById(R.id.btnN);
		btnO = (Button)findViewById(R.id.btnO);
		btnP = (Button)findViewById(R.id.btnP);
		btnQ = (Button)findViewById(R.id.btnQ);
		btnR = (Button)findViewById(R.id.btnR);
		btnS = (Button)findViewById(R.id.btnS);
		btnT = (Button)findViewById(R.id.btnT);
		btnU = (Button)findViewById(R.id.btnU);
		btnV = (Button)findViewById(R.id.btnV);
		btnW = (Button)findViewById(R.id.btnW);
		btnX = (Button)findViewById(R.id.btnX);
		btnY = (Button)findViewById(R.id.btnY);
		btnZ = (Button)findViewById(R.id.btnZ);
		btn0 = (Button)findViewById(R.id.btn0);
		btn1 = (Button)findViewById(R.id.btn1);
		btn2 = (Button)findViewById(R.id.btn2);
		btn3 = (Button)findViewById(R.id.btn3);
		btn4 = (Button)findViewById(R.id.btn4);
		btn5 = (Button)findViewById(R.id.btn5);
		btn6 = (Button)findViewById(R.id.btn6);
		btn7 = (Button)findViewById(R.id.btn7);
		btn8 = (Button)findViewById(R.id.btn8);
		btn9 = (Button)findViewById(R.id.btn9);

		/**
		 * Sets listeners
		 */
		btnCheck.setOnClickListener(this);
		btnProceed.setOnClickListener(this);
		btnErase.setOnClickListener(this);
		btnDeleteLastLetter.setOnClickListener(this);
		btnToggleUppercase.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnPeriod.setOnClickListener(this);
		btnColon.setOnClickListener(this);
		btnComma.setOnClickListener(this);
		btnSemiColon.setOnClickListener(this);
		btnSlash.setOnClickListener(this);
		btnPlus.setOnClickListener(this);
		btnMinus.setOnClickListener(this);
		btnEquals.setOnClickListener(this);
		btnQuestion.setOnClickListener(this);
		btnExclamation.setOnClickListener(this);
		btnSingleQuote.setOnClickListener(this);
		btnDoubleQuote.setOnClickListener(this);
		btnSpace.setOnClickListener(this);
		btnA.setOnClickListener(this);
		btnB.setOnClickListener(this);
		btnC.setOnClickListener(this);
		btnD.setOnClickListener(this);
		btnE.setOnClickListener(this);
		btnF.setOnClickListener(this);
		btnG.setOnClickListener(this);
		btnH.setOnClickListener(this);
		btnI.setOnClickListener(this);
		btnJ.setOnClickListener(this);
		btnK.setOnClickListener(this);
		btnL.setOnClickListener(this);
		btnM.setOnClickListener(this);
		btnN.setOnClickListener(this);
		btnO.setOnClickListener(this);
		btnP.setOnClickListener(this);
		btnQ.setOnClickListener(this);
		btnR.setOnClickListener(this);
		btnS.setOnClickListener(this);
		btnT.setOnClickListener(this);
		btnU.setOnClickListener(this);
		btnV.setOnClickListener(this);
		btnW.setOnClickListener(this);
		btnX.setOnClickListener(this);
		btnY.setOnClickListener(this);
		btnZ.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);

		btnPeriod.setOnFocusChangeListener(specialCharListener);
		btnColon.setOnFocusChangeListener(specialCharListener);
		btnComma.setOnFocusChangeListener(specialCharListener);
		btnSemiColon.setOnFocusChangeListener(specialCharListener);
		btnSlash.setOnFocusChangeListener(specialCharListener);
		btnPlus.setOnFocusChangeListener(specialCharListener);
		btnMinus.setOnFocusChangeListener(specialCharListener);
		btnEquals.setOnFocusChangeListener(specialCharListener);
		btnQuestion.setOnFocusChangeListener(specialCharListener);
		btnExclamation.setOnFocusChangeListener(specialCharListener);
		btnSingleQuote.setOnFocusChangeListener(specialCharListener);
		btnDoubleQuote.setOnFocusChangeListener(specialCharListener);

		String initialString = getIntent().getStringExtra("initialString");
		if(null != initialString) txtTypedLetters.setText(initialString);

	}


	
	@Override
	public void onClick(View v) {
		Tools.speak("Selecionado " + Tools.checkSpecialChars(((Button)v).getText().toString(),this) + (isUppercaseEnabled && ((Button)v).getText().toString().length() == 1 && ((int)((Button)v).getText().toString().charAt(0) >= 65 && (int)((Button)v).getText().toString().charAt(0) <= 127) ? " maiúsculo" : ""), false);


		switch (v.getId()) {
		case R.id.btnCheck:
			if(txtTypedLetters.getText().length() == 0){
				Tools.speak("Nada foi digitado", true);
			}else{
				Tools.speak(Tools.handleNumber(txtTypedLetters.getText().toString()), true, true, true);
			}
			break;
		case R.id.btnContinue:
			if(txtTypedLetters.getText().length() == 0){
				Tools.speak("Não é possível prosseguir sem digitar nenhuma palavra. Para voltar selecione cancelar", true);
			}else{
				Intent i = new Intent();
				i.putExtra("typedString", txtTypedLetters.getText().toString());
				setResult(RESULT_OK, i);
				finish();
			}
			break;
		case R.id.btnErase:
			txtTypedLetters.setText("");
			break;
		case R.id.btnDeleteLastLetter:
			StringBuilder currentText = new StringBuilder(txtTypedLetters.getText().toString());
			currentText.delete(currentText.length()-1, currentText.length());
			txtTypedLetters.setText(currentText);
			break;
		case R.id.btnCancel:
			setResult(RESULT_CANCELED, null);
			finish();
			break;
		case R.id.btnToggleUppercase:
			isUppercaseEnabled = !isUppercaseEnabled;
			if(isUppercaseEnabled) btnToggleUppercase.setText(getResources().getString(R.string.disable_uppercase));
			else btnToggleUppercase.setText(getResources().getString(R.string.enable_uppercase));
			
			break;
			/**
			 * Keys
			 */
		case R.id.btnPeriod:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + ".");
			break;
		case R.id.btnColon:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + ":");
			break;
		case R.id.btnComma:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + ",");
			break;
		case R.id.btnSemiColon:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + ";");
			break;
		case R.id.btnSlash:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "/");
			break;
		case R.id.btnPlus:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "+");
			break;
		case R.id.btnMinus:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "-");
			break;
		case R.id.btnEquals:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "=");
			break;
		case R.id.btnQuestion:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "?");
			break;
		case R.id.btnExclamation:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "!");
			break;
		case R.id.btnSingleQuote:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "'");
			break;
		case R.id.btnDoubleQuote:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "\"");
			break;
		case R.id.btnSpace:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + " ");
			break;
		case R.id.btnA:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("A"));
			break;
		case R.id.btnB:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("B"));
			break;
		case R.id.btnC:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("C"));
			break;
		case R.id.btnD:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("D"));
			break;
		case R.id.btnE:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("E"));
			break;
		case R.id.btnF:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("F"));
			break;
		case R.id.btnG:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("G"));
			break;
		case R.id.btnH:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("H"));
			break;
		case R.id.btnI:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("I"));
			break;
		case R.id.btnJ:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("J"));
			break;
		case R.id.btnK:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("K"));
			break;
		case R.id.btnL:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("L"));
			break;
		case R.id.btnM:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("M"));
			break;
		case R.id.btnN:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("N"));
			break;
		case R.id.btnO:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("O"));
			break;
		case R.id.btnP:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("P"));
			break;
		case R.id.btnQ:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("Q"));
			break;
		case R.id.btnR:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("R"));
			break;
		case R.id.btnS:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("S"));
			break;
		case R.id.btnT:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("T"));
			break;
		case R.id.btnU:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("U"));
			break;
		case R.id.btnV:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("V"));
			break;
		case R.id.btnW:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("W"));
			break;
		case R.id.btnX:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("X"));
			break;
		case R.id.btnY:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("Y"));
			break;
		case R.id.btnZ:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + checkLetterCase("Z"));
			break;
		case R.id.btn0:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "0");
			break;
		case R.id.btn1:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "1");
			break;
		case R.id.btn2:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "2");
			break;
		case R.id.btn3:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "3");
			break;
		case R.id.btn4:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "4");
			break;
		case R.id.btn5:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "5");
			break;
		case R.id.btn6:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "6");
			break;
		case R.id.btn7:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "7");
			break;
		case R.id.btn8:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "8");
			break;
		case R.id.btn9:
			txtTypedLetters.setText(txtTypedLetters.getText().toString() + "9");
			break;
		}

	}
	
	@SuppressLint("DefaultLocale")
	public String checkLetterCase(String str){
		if(isUppercaseEnabled){
			return str.toUpperCase(Locale.getDefault());
		}else{
			return str.toLowerCase(Locale.getDefault());
		}
	}
	
	
	
	OnFocusChangeListener specialCharListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v.isFocused()){
				String s = null;
				if(v instanceof Button)
					s = ((Button)v).getText().toString();
				
				Tools.speak(Tools.checkSpecialChars(s,WordMaker.this), false);
			}
		}
	};
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return true;
	}

}
