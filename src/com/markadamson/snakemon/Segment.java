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
        paint.setColor(0xffff00ff);
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
		c.save();
		c.translate(Xpos * 5.0f, Ypos * 5.0f);
		c.drawRect(0, 0, 5, 5, mPaint);
		c.restore();
	}
}