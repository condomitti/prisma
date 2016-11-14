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

import java.util.ArrayList;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.condomitti.prisma.utils.ContactDataActions;
import com.condomitti.prisma.utils.ContactTypes;
import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class Contact extends SuperActivity implements OnClickListener {

    private static final int WORD_MAKER_INSERT_NAME = 10658;
    private static final int WORD_MAKER_INSERT_EMAIL = 10508;
    private static final int WORD_MAKER_EDIT_EMAIL = 10509;
    private static final int NUMERIC_KEYBOARD_INSERT_PHONE = 10825;
    private static final int NUMERIC_KEYBOARD_EDIT_PHONE = 10826;
    private static final int CONTACT_TYPE_EMAIL = 57433;
    private static final int CONTACT_TYPE_PHONE = 57434;
    private static final int CONTACT_DATA_ACTIONS_PHONE = 86525;
    private static final int CONTACT_DATA_ACTIONS_EMAIL = 86526;
    private static final int CONTACT_DATA_ACTIONS_PHONE_EDIT = 865023;
    private static final int CONTACT_DATA_ACTIONS_EMAIL_EDIT = 865024;

    private ArrayList<ArrayList<String>> contactEmails = new ArrayList<ArrayList<String>>();
    private ArrayList<String> contactEmailsToRemove = new ArrayList<String>();
    private ArrayList<ArrayList<String>> contactPhones = new ArrayList<ArrayList<String>>();
    private ArrayList<String> contactPhonesToRemove = new ArrayList<String>();
    private int contactType;
    private View vForMaintenance = null;
    private int cDataIndexForMaintenance = 0;
    private Bean_Contact contact = null;

    Button btnInsertName = null;
    Button btnInsertPhone = null;
    Button btnInsertEmail = null;
    Button btnSave = null;
    Button btnCancel = null;
    TextView txtTitle = null;
    TextView txtName = null;
    LinearLayout llUserPhones = null;
    LinearLayout llUserEmails = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);
        makeUpScreen();
        handleInitValues();

        super.loadTouchables();

    }

    public void makeUpScreen() {
        setContentView(R.layout.contact);

        /**
         * Retrieves references
         */
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnInsertName = (Button) findViewById(R.id.btnInsertName);
        btnInsertPhone = (Button) findViewById(R.id.btnInsertPhone);
        btnInsertEmail = (Button) findViewById(R.id.btnInsertEmail);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtName = (TextView) findViewById(R.id.txtName);
        llUserEmails = (LinearLayout) findViewById(R.id.llUserEmails);
        llUserPhones = (LinearLayout) findViewById(R.id.llUserPhones);

        /**
         * Sets listeners
         */
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnInsertName.setOnClickListener(this);
        btnInsertPhone.setOnClickListener(this);
        btnInsertEmail.setOnClickListener(this);

//		btnCancel.setOnFocusChangeListener(Tools.pfListener);
//		btnSave.setOnFocusChangeListener(Tools.pfListener);
//		btnInsertEmail.setOnFocusChangeListener(Tools.pfListener);
//		btnInsertName.setOnFocusChangeListener(Tools.pfListener);
//		btnInsertPhone.setOnFocusChangeListener(Tools.pfListener);
//		txtName.setOnFocusChangeListener(Tools.pfListener);

        txtTitle.setText("Inserir novo contato");
        txtTitle.requestFocus();
    }

    public void handleInitValues() {
        Bundle b = getIntent().getExtras();
        //We have got a Contact selection from the previous screen
        if (b != null && b.containsKey("contact")) {
            txtTitle.setText("Editar contato");
            contact = (Bean_Contact) b.get("contact");
            txtName.setText(contact.getName());
            contactEmails = contact.getEmails();
            contactPhones = contact.getPhones();

            btnInsertName.setText(getResources().getString(R.string.change_name));
            processInitValues();
        }
    }

    public void processInitValues() {
        for (ArrayList<String> phone : contactPhones) {
            addButtonContactData(Integer.parseInt(phone.get(2)), phone.get(1), ContactTypes.TYPE_PHONE);
        }
        for (ArrayList<String> email : contactEmails) {
            addButtonContactData(Integer.parseInt(email.get(2)), email.get(1), ContactTypes.TYPE_EMAIL);
        }
    }


    @Override
    public void onClick(View v) {
        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);


        Intent i;
        String initString;

        switch (v.getId()) {
            case R.id.btnInsertEmail:
                i = new Intent(this, ContactTypes.class);
                i.putExtra("type", ContactTypes.TYPE_EMAIL);
                startActivityForResult(i, CONTACT_TYPE_EMAIL);
                break;
            case R.id.btnInsertName:
                i = new Intent(this, WordMaker.class);
                initString = txtName.getText().toString();
                if (initString.length() > 0) {
                    i.putExtra("initialString", initString);
                }
                startActivityForResult(i, WORD_MAKER_INSERT_NAME);
                break;
            case R.id.btnInsertPhone:
                i = new Intent(this, ContactTypes.class);
                i.putExtra("type", ContactTypes.TYPE_PHONE);
                startActivityForResult(i, CONTACT_TYPE_PHONE);
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSave:
                saveContact();
                break;

        }
    }

    public void saveContact() {

        Account[] accounts;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            AccountManager manager = AccountManager.get(this);
            accounts = manager.getAccountsByType("com.google");

        } else {
            Tools.speak("Não é possível salvar o contato. A permissão não foi dada.", true);

            return;
        }

        //Updating an existing contact
        if (contact != null) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DISABLED)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accounts[0].type)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accounts[0].name)
                    .build());

            // add name
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
            builder.withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + " = ?", new String[]{Integer.toString(contact.get_raw_id()), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, txtName.getText().toString());
            ops.add(builder.build());

            // phones
            for (ArrayList<String> phone : contactPhones) {

                //New phone
                if (phone.size() == 3) {
                    Log.i("[PRISMA]", "Adding new phone number");
                    ContentProviderOperation.Builder phonesBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                    phonesBuilder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    phonesBuilder.withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.get_raw_id());
                    phonesBuilder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.get(1));
                    phonesBuilder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phone.get(2));
                    ops.add(phonesBuilder.build());

                    //Updating phone
                } else {
                    Log.i("[PRISMA]", "Modifying phone");
                    ContentProviderOperation.Builder phonesBuilder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                    phonesBuilder.withSelection(ContactsContract.Data._ID + "= ? AND " + ContactsContract.Data.RAW_CONTACT_ID + " = ?", new String[]{phone.get(3), Integer.toString(contact.get_raw_id())});
                    phonesBuilder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    phonesBuilder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.get(1));
                    phonesBuilder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phone.get(2));
                    ops.add(phonesBuilder.build());
                }
            }
            for (String removePhone : contactPhonesToRemove) {
                ContentProviderOperation.Builder phonesBuilder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI);
                phonesBuilder.withSelection(ContactsContract.Data._ID + "=?", new String[]{removePhone});
                ops.add(phonesBuilder.build());
            }


            // emails
            for (ArrayList<String> email : contactEmails) {

                //New email
                if (email.size() == 3) {
                    Log.i("[PRISMA]", "Adding new e-mail");
                    ContentProviderOperation.Builder emailsBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                    emailsBuilder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    emailsBuilder.withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.get_raw_id());
                    emailsBuilder.withValue(ContactsContract.CommonDataKinds.Email.DATA1, email.get(1));
                    emailsBuilder.withValue(ContactsContract.CommonDataKinds.Email.TYPE, email.get(2));
                    ops.add(emailsBuilder.build());

                    //Updating email
                } else {
                    Log.i("[PRISMA]", "Modifying e-mail");
                    ContentProviderOperation.Builder emailsBuilder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
                    emailsBuilder.withSelection(ContactsContract.Data._ID + "= ? AND " + ContactsContract.Data.RAW_CONTACT_ID + " = ?", new String[]{email.get(3), Integer.toString(contact.get_raw_id())});
                    emailsBuilder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    emailsBuilder.withValue(ContactsContract.CommonDataKinds.Email.DATA1, email.get(1));
                    emailsBuilder.withValue(ContactsContract.CommonDataKinds.Email.TYPE, email.get(2));
                    ops.add(emailsBuilder.build());
                }
            }
            for (String removeEmail : contactEmailsToRemove) {
                ContentProviderOperation.Builder emailsBuilder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI);
                emailsBuilder.withSelection(ContactsContract.Data._ID + "=?", new String[]{removeEmail});
                ops.add(emailsBuilder.build());
            }


            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Tools.speak("Alterações salvas com sucesso!", true);
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Creating new contact
        } else {

            String displayName = txtName.getText().toString();

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accounts[0].type)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accounts[0].name)
                    .build());

            //------------------------------------------------------ Names
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName).build());
            //------------------------------------------------------ Mobile Number
            for (ArrayList<String> phone : contactPhones) {

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.get(1))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                phone.get(2))
                        .build());
            }

            //------------------------------------------------------ Email
            for (ArrayList<String> email : contactEmails) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.get(1))
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, email.get(2))
                        .build());
            }

            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Tools.speak("Contato salvo com sucesso!", true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("PRISMA", e.toString());
            }
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent i;
        String typedString;

        switch (requestCode) {
            case WORD_MAKER_INSERT_NAME:
                if (resultCode == RESULT_OK) {
                    typedString = data.getStringExtra("typedString");
                    txtName.setText(typedString);
                    btnInsertName.setText(getResources().getString(R.string.change_name));
                    txtTitle.requestFocus();
                }
                break;
            case CONTACT_TYPE_EMAIL:
                if (resultCode == RESULT_OK) {
                    contactType = data.getIntExtra("contactType", 1);
                    Tools.speak("Digite o email", true);
                    i = new Intent(this, WordMaker.class);
                    startActivityForResult(i, WORD_MAKER_INSERT_EMAIL);
                }
                break;
            case CONTACT_TYPE_PHONE:
                if (resultCode == RESULT_OK) {
                    contactType = data.getIntExtra("contactType", 1);
                    Log.i("PRISMA", "ContactType? " + contactType);
                    Tools.speak("Digite o número", true);
                    i = new Intent(this, NumericKeyboard.class);
                    startActivityForResult(i, NUMERIC_KEYBOARD_INSERT_PHONE);
                }
                break;
            case WORD_MAKER_INSERT_EMAIL:
                if (resultCode == RESULT_OK) {
                    typedString = data.getStringExtra("typedString");
                    addContactData(contactType, typedString, ContactTypes.TYPE_EMAIL);
                    addButtonContactData(contactType, typedString, ContactTypes.TYPE_EMAIL);
                }
                break;
            case NUMERIC_KEYBOARD_INSERT_PHONE:
                if (resultCode == RESULT_OK) {
                    typedString = data.getStringExtra("typedNumber");
                    addContactData(contactType, typedString, ContactTypes.TYPE_PHONE);
                    addButtonContactData(contactType, typedString, ContactTypes.TYPE_PHONE);
                }
                break;
            case CONTACT_DATA_ACTIONS_EMAIL:
                if (resultCode == ContactDataActions.ACTION_DELETE) {
                    if (contactEmails.get(cDataIndexForMaintenance).size() >= 4) {
                        contactEmailsToRemove.add(contactEmails.get(cDataIndexForMaintenance).get(3));
                    }
                    contactEmails.remove(cDataIndexForMaintenance);
                    llUserEmails.removeView(vForMaintenance);

                } else if (resultCode == ContactDataActions.ACTION_EDIT) {
                    i = new Intent(this, ContactTypes.class);
                    i.putExtra("type", ContactTypes.TYPE_EMAIL);
                    startActivityForResult(i, CONTACT_DATA_ACTIONS_EMAIL_EDIT);
                }
                break;
            case CONTACT_DATA_ACTIONS_PHONE:
                if (resultCode == ContactDataActions.ACTION_DELETE) {
                    if (contactPhones.get(cDataIndexForMaintenance).size() >= 4) {
                        contactPhonesToRemove.add(contactPhones.get(cDataIndexForMaintenance).get(3));
                    }
                    contactPhones.remove(cDataIndexForMaintenance);
                    llUserPhones.removeView(vForMaintenance);
                } else if (resultCode == ContactDataActions.ACTION_EDIT) {
                    i = new Intent(this, ContactTypes.class);
                    i.putExtra("type", ContactTypes.TYPE_PHONE);
                    startActivityForResult(i, CONTACT_DATA_ACTIONS_PHONE_EDIT);
                }
                break;
            case CONTACT_DATA_ACTIONS_PHONE_EDIT:
                if (resultCode == RESULT_OK) {
                    contactType = data.getIntExtra("contactType", 1);
                    Tools.speak("Edite o número", true);
                    i = new Intent(this, NumericKeyboard.class);
                    i.putExtra("number", contactPhones.get(cDataIndexForMaintenance).get(1));
                    startActivityForResult(i, NUMERIC_KEYBOARD_EDIT_PHONE);
                }
                break;
            case CONTACT_DATA_ACTIONS_EMAIL_EDIT:
                if (resultCode == RESULT_OK) {
                    contactType = data.getIntExtra("contactType", 1);
                    Tools.speak("Edite o email", true);
                    i = new Intent(this, WordMaker.class);
                    i.putExtra("initialString", contactEmails.get(cDataIndexForMaintenance).get(1));
                    startActivityForResult(i, WORD_MAKER_EDIT_EMAIL);
                }
                break;
            case NUMERIC_KEYBOARD_EDIT_PHONE:
                if (resultCode == RESULT_OK) {
                    typedString = data.getStringExtra("typedNumber");
                    contactPhones.get(cDataIndexForMaintenance).set(0, getStringType(contactType, ContactTypes.TYPE_PHONE));
                    contactPhones.get(cDataIndexForMaintenance).set(1, typedString);
                    if (contactPhones.get(cDataIndexForMaintenance).size() > 2)
                        contactPhones.get(cDataIndexForMaintenance).remove(2);
                    contactPhones.get(cDataIndexForMaintenance).add(2, String.valueOf(contactType));
                    ((Button) vForMaintenance).setText("Telefone " + getStringType(contactType, ContactTypes.TYPE_PHONE) + ": " + Tools.handleNumber(typedString));
                }
                break;
            case WORD_MAKER_EDIT_EMAIL:
                if (resultCode == RESULT_OK) {
                    typedString = data.getStringExtra("typedString");
                    contactEmails.get(cDataIndexForMaintenance).set(0, getStringType(contactType, ContactTypes.TYPE_EMAIL));
                    contactEmails.get(cDataIndexForMaintenance).set(1, typedString);
                    if (contactEmails.get(cDataIndexForMaintenance).size() > 2)
                        contactEmails.get(cDataIndexForMaintenance).remove(2);
                    contactEmails.get(cDataIndexForMaintenance).add(2, String.valueOf(contactType));
                    ((Button) vForMaintenance).setText("Email " + getStringType(contactType, ContactTypes.TYPE_EMAIL) + ": " + typedString);
                }
                break;
        }

        super.loadTouchables();
    }

    public void addContactData(int contactType, String typedString, String type) {
        ArrayList<String> contactData = new ArrayList<String>();
        contactData.add(getStringType(contactType, type));
        contactData.add(typedString);
        contactData.add(Integer.toString(contactType));

        if (type.equals(ContactTypes.TYPE_EMAIL)) {
            contactEmails.add(contactData);
        } else if (type.equals(ContactTypes.TYPE_PHONE)) {
            contactPhones.add(contactData);
        }

    }

    public void addButtonContactData(int contactDataType, String contactDataString, String contactType) {

        final Button btn = new Button(this);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        if (contactType.equals(ContactTypes.TYPE_EMAIL)) {
            btn.setText("Email " + getStringType(contactDataType, ContactTypes.TYPE_EMAIL) + ": " + contactDataString);
            btn.setOnFocusChangeListener(Tools.pfListenerQueue);
            llUserEmails.addView(btn);
            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);
                    vForMaintenance = btn;
                    cDataIndexForMaintenance = getObjIndex(v, CONTACT_TYPE_EMAIL);
                    Intent i = new Intent(Contact.this, ContactDataActions.class);
                    startActivityForResult(i, CONTACT_DATA_ACTIONS_EMAIL);
                }
            });
        } else if (contactType.equals(ContactTypes.TYPE_PHONE)) {
            btn.setText("Telefone " + getStringType(contactDataType, ContactTypes.TYPE_PHONE) + ": " + Tools.handleNumber(contactDataString));
            btn.setOnFocusChangeListener(Tools.pfListenerQueue);
            llUserPhones.addView(btn);
            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);
                    vForMaintenance = btn;
                    cDataIndexForMaintenance = getObjIndex(v, CONTACT_TYPE_PHONE);
                    Intent i = new Intent(Contact.this, ContactDataActions.class);
                    startActivityForResult(i, CONTACT_DATA_ACTIONS_PHONE);
                }
            });
        }
    }

    public String getStringType(int contactType, String type) {
        if (type.equals(ContactTypes.TYPE_EMAIL)) {
            switch (contactType) {
                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    return getResources().getString(R.string.personal);
                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    return getResources().getString(R.string.work);
                case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                    return getResources().getString(R.string.others);
            }
        } else if (type.equals(ContactTypes.TYPE_PHONE)) {
            switch (contactType) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    return getResources().getString(R.string.home);
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    return getResources().getString(R.string.work);
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    return getResources().getString(R.string.others);
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    return getResources().getString(R.string.mobile);
            }
        }
        return null;
    }

    public int getObjIndex(View v, int contactType) {
        int objIndex = -1;

        if (contactType == CONTACT_TYPE_EMAIL) {
            for (int i = 0; i < llUserEmails.getChildCount(); i++) {
                if (llUserEmails.getChildAt(i) == v) objIndex = i;
            }
        } else if (contactType == CONTACT_TYPE_PHONE) {
            for (int i = 0; i < llUserPhones.getChildCount(); i++) {
                if (llUserPhones.getChildAt(i) == v) objIndex = i;
            }
        }
        return objIndex;
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }

}
