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

	int getXpos()
	{
		return Xpos;
	}
	
	int getYpos()
	{
		return Ypos;
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
}