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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.telephony.TelephonyManager;

import com.condomitti.prisma.UnlockScreen;
import com.condomitti.prisma.utils.Tools;

/**
 * ScreenHandler class launches the very first screen (UnlockScreen) whenever screen is turned ON or sounds a beep otherwise
 */
public class ScreenHandler extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if(Tools.phoneState == TelephonyManager.CALL_STATE_IDLE) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                Intent i = new Intent(ctx, UnlockScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(i);

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
                tg.startTone(ToneGenerator.TONE_PROP_ACK);

            }

        }

	}

}