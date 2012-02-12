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
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
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
		//c.save();
		c.translate(Xpos * size, Ypos * size);
		c.drawRect(0, 0, size, size, mPaint);
		//c.restore();
	}
}