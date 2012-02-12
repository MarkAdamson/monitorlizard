package com.markadamson.snakemon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	private int speed;
	private float segSize = 30;
	private int length;
	private int requiredLength;
	Canvas c;
	
	Segment head;
	List<Segment> tail = new ArrayList();
	
	Snake (float Xlimit, float Ylimit, int speed, int length)
	{
		//Initialise values
		this.Xlimit = Xlimit;
		this.Ylimit = Ylimit;
		direction = new Random().nextInt(3);
        now = SystemClock.elapsedRealtime();
		this.speed = speed;
		head = new Segment(0, 0, direction);
		this.requiredLength = length;
		length = 1;
	}
	
	void SetSpeed(int speed)
	{
		this.speed = speed;
	}
	void Move()
	{
		int dt = (int) (SystemClock.elapsedRealtime() - now + remainder);
		float millisPerStep = 200 - (speed  * 2f - 10);
		int steps = (int) (dt/millisPerStep);
		now += steps * millisPerStep;
		remainder = (int) (dt % millisPerStep);
		for(int i=0;i<steps;i++)
		{
			int lastXpos = head.getXpos();
			int lastYpos = head.getYpos();
			int lastDirection = head.getDirection();
			head.Move();
			Iterator<Segment> itr = tail.iterator();
			while(itr.hasNext())
			{
				Segment currentSeg = itr.next();
				lastXpos = currentSeg.getXpos();
				lastYpos = currentSeg.getYpos();
				lastDirection = currentSeg.getDirection();
				currentSeg.Move();
			}
			
			if(length<requiredLength)
			{
				tail.add(new Segment(lastXpos, lastYpos, lastDirection));
				length++;
			}
			
			for(int j=tail.size()-1; j>=0; j--)
			{
				if(j>0) tail.get(j).setDirection(tail.get(j-1).getDirection());
				else tail.get(j).setDirection(head.getDirection());
			}
			
			boolean moved = false;
			while(!moved)
			{
				int newXpos;
				int newYpos;
				switch(direction)
				{
				case 0:
					newXpos = head.Xpos - 1;
					if(Math.abs(newXpos * segSize)>Xlimit)
					{
						direction = 1;
						break;
					}	
					moved = true;
					break;
				case 1:
					newYpos = head.Ypos - 1;
					if(Math.abs(newYpos * segSize)>Ylimit)
					{
						direction = 2;
						break;
					}
					moved = true;
					break;
				case 2:
					newXpos = head.Xpos + 1;
					if(newXpos * segSize + segSize > Xlimit)
					{
						direction = 3;
						break;
					}
					moved = true;
					break;
				case 3:
					newYpos = head.Ypos + 1;
					if(newYpos * segSize + segSize > Ylimit)
					{
						direction = 0;
						break;
					}
					moved = true;
					break;
				}
			}
			head.setDirection(direction);
		}
	}
	void Draw(Canvas c)
	{
		c.save();
		c.drawColor(0xff000000);
		c.translate(Xlimit, Ylimit);
		head.Draw(segSize, c);
		Iterator<Segment> itr = (Iterator) tail.iterator();
		while(itr.hasNext())
		{
			itr.next().Draw(segSize, c);
		}
		c.restore();
	}
}