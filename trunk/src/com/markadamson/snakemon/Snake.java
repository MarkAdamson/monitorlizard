package com.markadamson.snakemon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;

public class Snake
{
	float Xlimit;
	float Ylimit;
	int direction;
	private float segSize = 30;
	private int length;
	private int requiredLength;
	Canvas c;
	
	Segment head;
	List<Segment> tail = new ArrayList<Segment>();
	
	Snake (float Xlimit, float Ylimit, int length)
	{
		//Initialise values
		this.Xlimit = Xlimit;
		this.Ylimit = Ylimit;
		direction = new Random().nextInt(3);
		head = new Segment(0, 0, direction);
		this.requiredLength = length;
		length = 1;
	}
	
	void setLength(int length)
	{
		if(length>0) this.requiredLength = length;
	}
	
	void Move()
	{
		if(length>requiredLength)
		{
			for(int i=0; i< length-requiredLength;i++)
			{
				tail.remove(tail.size()-1);
			}
		}
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
	void Draw(Canvas c)
	{
		c.save();
		c.drawColor(0xff000000);
		c.translate(Xlimit, Ylimit);
		head.Draw(segSize, c);
		Iterator<Segment> itr = (Iterator<Segment>) tail.iterator();
		while(itr.hasNext())
		{
			itr.next().Draw(segSize, c);
		}
		c.restore();
	}
}