package com.markadamson.snakemon;

import java.util.Random;

import android.graphics.Canvas;

public class Snake
{
	int Xpos;
	int Ypos;
	int direction;
	Canvas c;
	
	Segment head;
	
	Snake (Canvas c)
	{
		//Initialise values
		this.c = c;
		Xpos = 0;
		Ypos = 0;
		direction = new Random().nextInt(3);
		
		head = new Segment(Xpos, Ypos, direction);
	}
	void Move()
	{
		
	}
	void Draw()
	{
		
	}
}