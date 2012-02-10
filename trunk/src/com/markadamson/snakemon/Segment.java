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
        paint.setStrokeWidth(2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
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
	
	void Draw(Canvas c)
	{
		//c.save();
		c.translate(Xpos * 10.0f, Ypos * 10.0f);
		c.drawRect(0, 0, 10, 10, mPaint);
		//c.restore();
	}
}