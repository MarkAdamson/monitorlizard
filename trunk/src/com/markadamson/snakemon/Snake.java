package com.markadamson.snakemon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.util.Log;

public class Snake
{
	float Xlimit;
	float Ylimit;
	//int direction;
	private boolean alive=true;
	private float segSize;
	private int length;
	private int requiredLength;
	private Random rnd;
	Canvas c;
	
	private String DEB_TAG = "Snake";
	
	Segment head;
	List<Segment> tail = new ArrayList<Segment>();
	
	Snake (float Xlimit, float Ylimit, int length)
	{
		//Initialise values
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		this.Xlimit = Xlimit;
		this.Ylimit = Ylimit;
		if(Xlimit < Ylimit) segSize = Xlimit / 7;
		else segSize = Ylimit / 7;
		head = new Segment(0, 0, rnd.nextInt(3));
		this.requiredLength = length;
		length = 1;
	}
	
	void setLength(int length)
	{
		if(length>0) this.requiredLength = length;
	}
	
	void Move()
	{
		if(alive)
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
			
			int dirChange;
			
			if(rnd.nextInt(15)==1)
			{
				dirChange = rnd.nextInt(2) * 2 + 1;
				head.setDirection((head.getDirection()+dirChange)%4);
			}
			
			if(gonnaCrash())
			{
				//Log.d(DEB_TAG, "Gonna Crash!");
				dirChange = rnd.nextInt(2) * 2 + 1;
				//Log.d(DEB_TAG, "dirChange:" + Integer.toString(dirChange));
				head.setDirection((head.getDirection()+dirChange)%4);
				if(gonnaCrash())
				{
					//Log.d(DEB_TAG, "Gonna Crash!");
					head.setDirection((head.getDirection()+2)%4);
					if(gonnaCrash())
					{
						Log.d(DEB_TAG, "Death!");
						alive=false;
					}
				}
			}
		} else {
			if(tail.size()>0)
			{
				tail.remove(tail.size()-1);
				length--;
			} else {
				head.setXpos(0);
				head.setYpos(0);
				alive=true;
			}
		}
		
	}
	
	private boolean gonnaCrash() {
		for(int i=0; i<tail.size();i++)
		{
			if(head.willCollide(tail.get(i)))
			{
				return true;
			}
		}
		switch(head.getDirection()){
		case 0: if(Math.abs(head.getXdest()) * segSize>Xlimit) return true; break;
		case 1: if(Math.abs(head.getYdest()) * segSize>Ylimit) return true; break;
		case 2: if(head.getXdest() * segSize + segSize > Xlimit) return true; break;
		case 3: if(head.getYdest() * segSize + segSize > Ylimit) return true; break;
		}
		return false;
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

	public boolean isAlive() {
		return alive;
	}
}