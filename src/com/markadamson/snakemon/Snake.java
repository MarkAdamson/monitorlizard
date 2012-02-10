package com.markadamson.snakemon;

import java.util.Random;

import android.graphics.Canvas;
import android.os.SystemClock;

public class Snake
{
	float Xlimit;
	float Ylimit;
    private long now;
    private int remainder;
	int direction;
	int speed;
	Canvas c;
	
	Segment head;
	
	Snake (float Xlimit, float Ylimit)
	{
		//Initialise values
		this.Xlimit = Xlimit;
		this.Ylimit = Ylimit;
		direction = new Random().nextInt(3);
        now = SystemClock.elapsedRealtime();
		
		head = new Segment(0, 0, direction);
	}
	void Move()
	{
		long dt = SystemClock.elapsedRealtime() - now + remainder;
		int steps = (int) (dt/50);
		now += steps * 50;
		remainder = (int) (dt % 50);
		for(int i=0;i<steps;i++)
		{
			boolean moved = false;
			while(!moved)
			{
				int newXpos;
				int newYpos;
				switch(direction)
				{
				case 0:
					newXpos = head.Xpos - 1;
					if(Math.abs(newXpos * 10)>Xlimit)
					{
						direction = 1;
						break;
					}	
					moved = true;
					break;
				case 1:
					newYpos = head.Ypos - 1;
					if(Math.abs(newYpos * 10)>Ylimit)
					{
						direction = 2;
						break;
					}
					moved = true;
					break;
				case 2:
					newXpos = head.Xpos + 1;
					if(newXpos * 10 + 10 > Xlimit)
					{
						direction = 3;
						break;
					}
					moved = true;
					break;
				case 3:
					newYpos = head.Ypos + 1;
					if(newYpos * 10 + 10 > Ylimit)
					{
						direction = 0;
						break;
					}
					moved = true;
					break;
				}
			}
			head.setDirection(direction);
			head.Move();
		}
	}
	void Draw(Canvas c)
	{
		c.save();
		c.drawColor(0xff000000);
		c.translate(Xlimit, Ylimit);
		head.Draw(c);
		c.restore();
	}
}