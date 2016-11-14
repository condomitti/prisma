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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

import java.util.ArrayList;
import java.util.LinkedList;

public class ContactsList extends SuperActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ALL_CONTACTS = 333444;
    private static final int LOADER_SEARCH_CONTACT = 333445;
    private static final int OPEN_CONTACT = 45354;
    LinkedList<Bean_Contact> foundContacts = null;
    LinearLayout listContacts = null;
    boolean loading = true;
    TextView title;
    int counter = 0;
    View viewingContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);

        loadContacts(getIntent().getStringExtra("searchCriterion"));

    }

    /**
     * If searchCriterion is null then query all contacts
     *
     * @param searchCriterion
     */
    public void loadContacts(String searchCriterion) {

        Tools.speak("Aguarde, estou pensando...", true);

        //List all contacts
        if (searchCriterion == null) {

            getLoaderManager().initLoader(LOADER_ALL_CONTACTS, null, this);

        } else {

            Bundle b = new Bundle();
            b.putString("searchCriterion", searchCriterion);
            getLoaderManager().initLoader(LOADER_SEARCH_CONTACT, b, this);
//            searchContact(searchCriterion);
        }

    }

    @SuppressWarnings("unchecked")
    public void makeUpScreen() {

        setContentView(R.layout.contacts_list);
        super.loadTouchables();


        title = (TextView) findViewById(R.id.txtTitle);
        title.setText("Resultado da busca por: " + getIntent().getStringExtra("searchString"));


        listContacts = (LinearLayout) findViewById(R.id.listContacts);


        TextView tvBack = new TextView(getApplicationContext());
        tvBack.setText("Voltar");
        tvBack.setTextAppearance(this, R.style.ListsTextView);
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
        listContacts.addView(tvBack, counter, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        for (final Bean_Contact contact : foundContacts) {
            final TextView tv = new TextView(getApplicationContext());
            tv.setText(contact.getName());
            tv.setFocusable(true);
            tv.setTextAppearance(this, R.style.ListsTextView);
            tv.setFocusableInTouchMode(true);
            tv.setVisibility(TextView.GONE);
            tv.setOnFocusChangeListener(Tools.pfListenerQueue);
            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Tools.speak("Selecionado " + contact.getName(), true);
                    Intent i = new Intent(ContactsList.this, ContactInformation.class);
                    i.putExtra("contact", contact);
                    viewingContact = tv;
                    startActivityForResult(i, OPEN_CONTACT);
                }
            });
            counter++;
            listContacts.addView(tv, counter, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        }
        counter = 0;


        tvBack.requestFocus();


    }


    public void displayAllContacts(Cursor c) {

        foundContacts = new LinkedList<Bean_Contact>();

        if (c.moveToFirst()) {
            processContactsCursor(c);
            Tools.speak("Exibindo " + foundContacts.size() + " contatos.", true);
        } else {
            Tools.speak("Você não tem contatos na sua agenda telefônica.", true);
        }

        makeUpScreen();

    }


    public void processContactsCursor(final Cursor c) {

        do {
            int _raw_id = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));

            ArrayList<ArrayList<String>> phones = getPhonesFromContactId(_raw_id);
            ArrayList<ArrayList<String>> emails = getEmailsFromContactId(_raw_id);
            Bean_Contact bc = new Bean_Contact(0, _raw_id, c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)), emails, phones);
            foundContacts.add(bc);


        } while (c.moveToNext());

        c.close();

    }


    public ArrayList<ArrayList<String>> getEmailsFromContactId(int _id) {
        ArrayList<ArrayList<String>> emails = new ArrayList<ArrayList<String>>();

        Cursor emailCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = ? ", new String[]{Integer.toString(_id)}, null);

        if (emailCursor != null) {

            for (emailCursor.moveToFirst(); !emailCursor.isAfterLast(); emailCursor.moveToNext()) {

                // Get an e-mail
                String emailAddress = emailCursor.getString(emailCursor
                        .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA1));
                int row_id = emailCursor.getInt(emailCursor
                        .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email._ID));
                String emailType = "";
                int type = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                        emailType = "Residência";
                        break;
                    case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                        emailType = "Outros";
                        break;
                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                        emailType = "Trabalho";
                        break;
                }


                ArrayList<String> email = new ArrayList<String>();
                email.add(emailType);
                email.add(emailAddress);
                email.add(String.valueOf(type));
                email.add(String.valueOf(row_id));

                emails.add(email);

            }
        }
        emailCursor.close();
        return emails;
    }


    public ArrayList<ArrayList<String>> getPhonesFromContactId(int _id) {

        ArrayList<ArrayList<String>> phones = new ArrayList<ArrayList<String>>();

        Cursor phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ? ", new String[]{Integer.toString(_id)}, null);

        if (phoneCursor != null) {

            for (phoneCursor.moveToFirst(); !phoneCursor.isAfterLast(); phoneCursor.moveToNext()) {

                // Get a phone number
                String phoneNumber = phoneCursor.getString(phoneCursor
                        .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                int row_id = phoneCursor.getInt(phoneCursor
                        .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
                String phoneType = "";
                int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        phoneType = "Residência";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        phoneType = "Celular";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                        phoneType = "Outros";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        phoneType = "Trabalho";
                        break;
                }

                ArrayList<String> phone = new ArrayList<String>();
                phone.add(phoneType);
                phone.add(phoneNumber);
                phone.add(String.valueOf(type));
                phone.add(String.valueOf(row_id));

                phones.add(phone);
            }

        }
        phoneCursor.close();
        return phones;
    }

    public void searchContact(Cursor c) {
        foundContacts = new LinkedList<Bean_Contact>();

        if (c.moveToFirst()) {

            processContactsCursor(c);

            if (foundContacts.size() == 1) {
                Bundle b = new Bundle();
                Tools.speak(String.format(getResources().getString(R.string.found), foundContacts.get(0).getName()), true);
                b.putSerializable("contact", foundContacts.get(0));
                Intent foundContact = new Intent(this, ContactInformation.class);
                foundContact.putExtras(b);
                startActivity(foundContact);
                finish();
            } else {
                Tools.speak("Mais de um contato corresponde à esse nome. Exibindo lista, escolha um.", true);
                makeUpScreen();
            }

        } else
            Tools.speak("O contato " + getIntent().getStringExtra("searchCriterion") + " não foi encontrado na sua agenda.", true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onClick(View v) {
        Tools.speak("Selecionado " + ((Button) v).getText().toString(), false);

        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case OPEN_CONTACT:
                if (resultCode == ContactInformation.CONTACT_DELETED) {
                    listContacts.removeView(viewingContact);
                }
                finish();
                super.loadTouchables();
                //Tools.speak("Exibindo " + foundContacts.size() + " contatos.", true);
                break;

        }
    }

    public void changeVisibility(int direction) {

        listContacts.getChildAt(counter).setVisibility(View.GONE);
        if (direction == View.FOCUS_DOWN) {
            counter++;
        } else {
            counter--;
        }
        listContacts.getChildAt(counter).setVisibility(View.VISIBLE);
        listContacts.getChildAt(counter).requestFocus();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (counter < foundContacts.size())
                    changeVisibility(View.FOCUS_DOWN);

                break;
            case KeyEvent.KEYCODE_VOLUME_UP:

                if (counter > 0)
                    changeVisibility(View.FOCUS_UP);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_ALL_CONTACTS:

                return new CursorLoader(
                        ContactsList.this, // Parent activity context
                        ContactsContract.RawContacts.CONTENT_URI, // Table to query
                        null, // Projection to return
                        ContactsContract.RawContacts.DELETED + " = '0'", // Selection clause
                        null, // No selection arguments
                        ContactsContract.Data.DISPLAY_NAME // Default sort order
                );
            case LOADER_SEARCH_CONTACT:
                String contact = args.getString("searchCriterion");
                return new CursorLoader(
                        ContactsList.this, // Parent activity context
                        ContactsContract.RawContacts.CONTENT_URI, // Table to query
                        null, // Projection to return
                        ContactsContract.Data.DISPLAY_NAME + " like '%" + contact + "%' AND " + ContactsContract.RawContacts.DELETED + " = '0'", // Selection clause
                        null, // No selection arguments
                        ContactsContract.Data.DISPLAY_NAME // Default sort order
                );

            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LOADER_ALL_CONTACTS:
                displayAllContacts(data);
                break;
            case LOADER_SEARCH_CONTACT:
                searchContact(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
