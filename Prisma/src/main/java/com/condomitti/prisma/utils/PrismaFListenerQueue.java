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

import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.TextView;


public class PrismaFListenerQueue implements OnFocusChangeListener {

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.isFocused()) {
            String s;
            if (v instanceof Button)
                s = ((Button) v).getText().toString();
            else if (v instanceof TextView) {
                s = ((TextView) v).getText().toString();
            } else {
                return;
            }

            Tools.speak(s, false);
        }
    }

}
