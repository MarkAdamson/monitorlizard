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

package com.markadamson.snakemon.free;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Segment
{
	//each segment has a position and direction
	int Xpos;
	int Ypos;
	int direction;
	boolean glow = false;
	//and can draw itself
	private final Paint mPaint = new Paint();
	private final Paint mGlow = new Paint();
	
	Segment(int Xpos, int Ypos, int direction, int colour)
	{
		this.Xpos = Xpos;
		this.Ypos = Ypos;
		this.direction = direction;

        // Create a Paint to draw the segment
        final Paint paint = mPaint;
        paint.setColor(colour);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        
        final Paint glow = mGlow;
        glow.set(paint);
        glow.setAlpha(127);
        glow.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
	}
	
	//check whether in it's next frame, this segment will collide with another
	boolean willCollide(Segment s)
	{
		if(getXdest()==s.getXdest() && getYdest()==s.getYdest()) return true;
		else return false;
	}
	
	void setGlow(boolean glow)
	{
		this.glow = glow;
	}
	
	//the rest of this is all pretty straightforward...

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
		/*float rounding = size/8;
		RectF rect = new RectF(1, 1, size-1, size-1);
		c.drawRoundRect(rect, rounding, rounding, mPaint);
		if(glow)
		{
			rect = new RectF(-(size*0.2f), -(size*0.2f), size*1.2f, size*1.2f);
			c.drawRoundRect(rect, rounding, rounding, mGlow);
		}*/
		c.drawRect(1, 1, size - 1, size - 1, mPaint);
		if(glow) c.drawRect(-(size*0.2f), -(size*0.2f), size*1.2f, size*1.2f, mGlow);
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
	
	public void setColour(int c){
		mPaint.setColor(c);
		mGlow.setColor(c);
	}
}