/*
 *  Copyright 2012 Mark Adamson
 *  
 *  This file is part of Monitor Lizard.
 * 
 *  Monitor Lizard is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Monitor Lizard is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Monitor Lizard.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.markadamson.snakemon;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Segment
{
	int Xpos;
	int Ypos;
	int direction;
	private final Paint mPaint = new Paint();
	
	Segment(int Xpos, int Ypos, int direction)
	{
		this.Xpos = Xpos;
		this.Ypos = Ypos;
		this.direction = direction;

        // Create a Paint to draw the lines for our cube
        final Paint paint = mPaint;
        paint.setColor(0xff00ff00);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	boolean willCollide(Segment s)
	{
		int Xdest = Xpos;
		int Ydest = Ypos;
		switch(direction)
		{
		case 0: Xdest--; break;
		case 1: Ydest--; break;
		case 2: Xdest++; break;
		case 3: Ydest++; break;
		}
		
		if(Xdest==s.getXpos() && Ydest==s.getYpos()) return true;
		else return false;
	}

	int getXpos()
	{
		return Xpos;
	}
	
	int getYpos()
	{
		return Ypos;
	}
	
	int getXdest()
	{
		if(direction==0) return Xpos-1;
		else if(direction==2) return Xpos+1;
		else return Xpos;
	}
	
	int getYdest()
	{
		if(direction==1) return Ypos-1;
		else if(direction==3) return Ypos+1;
		else return Ypos;
	}
	
	void Move()
	{
		switch(direction)
		{
		case 0:
			Xpos--;
			break;
		case 1:
			Ypos--;
			break;
		case 2:
			Xpos++;
			break;
		case 3:
			Ypos++;
			break;
		}
	}
	
	void setDirection(int d)
	{
		direction = d;
	}
	
	void Draw(float size, Canvas c)
	{
		c.save();
		c.translate(Xpos * size, Ypos * size);
		c.drawRect(0, 0, size - 2, size - 2, mPaint);
		c.restore();
	}

	public int getDirection() {
		// TODO Auto-generated method stub
		return direction;
	}

	public void setXpos(int i) {
		Xpos = i;
	}

	public void setYpos(int i) {
		Ypos = i;
	}
}