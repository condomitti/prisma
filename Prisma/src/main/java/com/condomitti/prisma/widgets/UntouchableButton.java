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

package com.condomitti.prisma.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.condomitti.prisma.utils.Tools;

public class UntouchableButton extends Button {

	public UntouchableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnFocusChangeListener(Tools.pfListenerQueue);
	}
	public UntouchableButton(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setOnFocusChangeListener(Tools.pfListenerQueue);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

}
