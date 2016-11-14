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

import java.util.Calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.condomitti.prisma.utils.SuperActivity;
import com.condomitti.prisma.utils.Tools;

public class DateTime extends SuperActivity implements OnClickListener {

    Button btnDate, btnTime, btnBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tools.setScreenSettings(this);

        makeUpScreen();
    }

    public void makeUpScreen() {
        setContentView(R.layout.date_time);
        super.loadTouchables();

        TextView title = (TextView) findViewById(R.id.titleDateTime);
        title.setFocusable(true);
        title.requestFocus();

        /**
         * Retrieves references
         */
        btnDate = (Button) findViewById(R.id.btnDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnBack = (Button) findViewById(R.id.btnBack);

        /**
         * Sets Listeners
         */
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    //THIS SHOULD BE CHANGED TO REFLECT LOCALE SETTINGS
    public void getTime() {
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder("Agora são ");
        sb.append(c.get(Calendar.HOUR_OF_DAY));
        sb.append(" e ");
        sb.append(c.get(Calendar.MINUTE));
        sb.append(" minutos ");

        Tools.speak(sb.toString(), true);

    }

    public void getDate() {
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder("Hoje é ");
        sb.append(Tools.daysOfWeek[c.get(Calendar.DAY_OF_WEEK) - 1]);
        sb.append(c.get(Calendar.DAY_OF_MONTH));
        sb.append(" de ");
        sb.append(Tools.months[c.get(Calendar.MONTH)]);
        sb.append(" de ");
        sb.append(c.get(Calendar.YEAR));

        Tools.speak(sb.toString(), true);
    }


    @Override
    public void onClick(View v) {

        Tools.speak("Selecionado " + ((Button) v).getText().toString(), true);

        switch (v.getId()) {
            case R.id.btnBack:
                finish();

                break;
            case R.id.btnTime:
                getTime();

                break;
            case R.id.btnDate:
                getDate();

                break;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }
}
