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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.util.Log;

public class Snake
{
	//the snake knows where the edges of the screen are
	float Xlimit;
	float Ylimit;
	
	private boolean alive=true;
	private float segSize;
	
	//has an actual length and a required length, allowing it to grow and shrink
	private int length;
	private int requiredLength;
	
	//and a random number generator, for decision making
	private Random rnd;
	Canvas c;
	
	private String DEB_TAG = "Snake";
	
	//consists of a single 'head' segment
	Segment head;
	//and an arraylist of segments for the 'tail'
	List<Segment> tail = new ArrayList<Segment>();
	
	Snake (float Xlimit, float Ylimit, int length)
	{
		//Initialise values
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		this.Xlimit = Xlimit;
		this.Ylimit = Ylimit;
		
		//set segment size so that there will be 14 segments along the shortest side of the screen
		if(Xlimit < Ylimit) segSize = Xlimit / 7;
		else segSize = Ylimit / 7;
		
		//initialise the head with a random direction
		head = new Segment(0, 0, rnd.nextInt(3));
		
		//set up the length of the snake
		this.requiredLength = length;
		length = 1;
	}
	
	void setLength(int length)
	{
		if(length>0) this.requiredLength = length;
	}
	
	//move the entire snake by one frame. grow or shrink it if necessary, tidy it up if it is dead
	void Move()
	{
		if(alive)
		{
			//if the snake is longer than it needs to be
			if(length>requiredLength)
			{
				Log.d(DEB_TAG, "Length: " + length);
				Log.d(DEB_TAG, "requiredLength: " + requiredLength);
				Log.d(DEB_TAG, "tail.size(): " + Integer.toString(tail.size()));
				//lop off the end
				for(int i=0; i< length-requiredLength;i++)
				{
					tail.remove(tail.size()-1);
					length--;
				}
			}
			
			//save the head's current position and direction
			int lastXpos = head.getXpos();
			int lastYpos = head.getYpos();
			int lastDirection = head.getDirection();
			//then move it
			head.Move();
			
			//iterate thru the snake, saving each segment's position and direction before moving it..
			Iterator<Segment> itr = tail.iterator();
			while(itr.hasNext())
			{
				Segment currentSeg = itr.next();
				lastXpos = currentSeg.getXpos();
				lastYpos = currentSeg.getYpos();
				lastDirection = currentSeg.getDirection();
				currentSeg.Move();
			}
			//..so that now we have moved the entire snake, and..
			
			//..if we need to..
			if(length<requiredLength)
			{
				//..we can add a new segment at the end!
				tail.add(new Segment(lastXpos, lastYpos, lastDirection));
				length++;
			}
			
			//now iterate thru the snake for tail to tip
			for(int j=tail.size()-1; j>=0; j--)
			{
				//setting each segment's direction to the segment in front of it
				if(j>0) tail.get(j).setDirection(tail.get(j-1).getDirection());
				else tail.get(j).setDirection(head.getDirection());
			}
			
			int dirChange;
			
			//randomly change direction, to make things interesting
			if(rnd.nextInt(15)==1)
			{
				dirChange = rnd.nextInt(2) * 2 + 1;
				head.setDirection((head.getDirection()+dirChange)%4);
			}
			
			//check for collisions, avoiding them if possible
			if(gonnaCrash())
			{
				dirChange = rnd.nextInt(2) * 2 + 1;
				head.setDirection((head.getDirection()+dirChange)%4);
				if(gonnaCrash())
				{
					head.setDirection((head.getDirection()+2)%4);
					if(gonnaCrash())
					{
						//and dying if not
						Log.d(DEB_TAG, "Death!");
						alive=false;
					}
				}
			}
		//if the snake is dead
		} else {
			if(tail.size()>0)
			{
				//clean up the tail, one segment per frame
				tail.remove(tail.size()-1);
				length--;
			} else {
				//if the tail is all gone, reset the snake
				head.setXpos(0);
				head.setYpos(0);
				alive=true;
			}
		}
		
	}
	
	//check for imminent collisions
	private boolean gonnaCrash() {
		//check for collisions against the tail
		for(int i=0; i<tail.size();i++)
		{
			//using the segments' collision detection
			if(head.willCollide(tail.get(i)))
			{
				return true;
			}
		}
		//also check for edge collisions, using the snake's knowledge of the screen boundaries
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