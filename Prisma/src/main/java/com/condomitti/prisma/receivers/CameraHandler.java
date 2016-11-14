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

package com.condomitti.prisma.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.condomitti.prisma.UnlockScreen;


/**
 * CameraHandler used to prevent camera app being called and kept above Prisma when camera button (present on some devices) is pressed.
 */
public class CameraHandler extends BroadcastReceiver {
    Intent i;
    Context context;

    @SuppressLint("HandlerLeak")
    Handler h = new Handler() {
        public void handleMessage(android.os.Message msg) {

            //Calls UnlockScreen whenever Camera app is called
            i = new Intent(context, UnlockScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(i);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("[PRISMA]", "CameraHandler being called");
        Message msg = new Message();
        h.sendMessageDelayed(msg, 800);
    }

}
