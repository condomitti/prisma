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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.condomitti.prisma.utils.SpokenDialog;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.SuperDialog;
import com.condomitti.prisma.utils.Tools;

public class ContactTelephoneOptions extends SuperActivity implements OnClickListener {

    Button btnCallThisNumber, btnSendSMS, btnCancel = null;
    String contactPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);

        makeUpDialog();
    }


    public void makeUpDialog() {

        Bundle b = getIntent().getExtras();

        String contactName = b.getString("contactName");
        contactPhone = b.getString("contactPhone");

        Dialog dialog = new SuperDialog(this);
        dialog.setContentView(R.layout.contact_telephone_options);
        super.loadTouchables(dialog.findViewById(R.id.parentLayout));
        dialog.setCancelable(false);
        dialog.setTitle(contactName);

        TextView title = (TextView) dialog.findViewById(R.id.titleContactTelephoneOptions);
        title.setFocusable(true);
        title.requestFocus();

        /**
         * Retrieves references
         */
        btnCallThisNumber = (Button) dialog.findViewById(R.id.btnCallThisNumber);
        btnSendSMS = (Button) dialog.findViewById(R.id.btnSendSMSToThisNumber);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        /**
         * Sets Listeners
         */

        btnCallThisNumber.setOnClickListener(this);
        btnSendSMS.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
//		btnCallThisNumber.setOnFocusChangeListener(Tools.pfListener);
//		btnSendSMS.setOnFocusChangeListener(Tools.pfListener);
//		btnCancel.setOnFocusChangeListener(Tools.pfListener);

        dialog.show();
        Tools.speak("O que deseja fazer?", true);
    }

    @Override
    public void onClick(View v) {

        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

        switch (v.getId()) {
            case R.id.btnCallThisNumber:
                Tools.showConfirm(this, "Confirma ligar para " + Tools.handleNumber(contactPhone) + "?");
                break;
            case R.id.btnSendSMSToThisNumber:
                break;
            case R.id.btnCancel:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Tools.SHOW_SPOKEN_DIALOG:
                if (resultCode == SpokenDialog.RESULT_YES) {

                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel://" + contactPhone));
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                        startActivityForResult(i, Tools.CALL_PHONE_ACTIVITY);

                    }
                }

                break;
        }
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }
}
