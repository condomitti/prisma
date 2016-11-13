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

import com.condomitti.prisma.R;
import com.condomitti.prisma.Welcome;
import com.condomitti.prisma.utils.Tools;

public class Receiver_BatteryStatus extends BroadcastReceiver {

	/**
	 * This BroadcastReceiver is called whenever battery status is low (warned by the system)
	 * @param ctx
	 * @param data
     */
	@Override
	public void onReceive(Context ctx, Intent data) {
		Tools.speak(ctx.getResources().getString(R.string.low_battery_warning), true);
		Intent i = new Intent(ctx, Welcome.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(i);
	}

}
