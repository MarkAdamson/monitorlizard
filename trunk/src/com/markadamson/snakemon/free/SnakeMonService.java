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

package com.markadamson.snakemon.free;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;


public class SnakeMonService extends WallpaperService {

    public static final String SHARED_PREFS_NAME="snakemonsettings";
    public static final int DIR_LEFT = 0;
    public static final int DIR_UP = 1;
    public static final int DIR_RIGHT = 2;
    public static final int DIR_DOWN = 3;

    private final Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
    	SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME,0);
        return new SnakeEngine(prefs);
    }

    //runs the snake - controls drawable area, and snake speed
    class SnakeEngine extends Engine implements OnSharedPreferenceChangeListener {

        @SuppressWarnings("unused")
		private String DEB_TAG = "SnakeEngine";
		@SuppressWarnings("unused")
        private float mOffset;
        private float mCenterX;
        private float mCenterY;
        private String lengthVal="";
        private String speedVal="";
        private boolean invertSpeed=false;
        private int mLength=1;
        private int mSpeed=1;
        
        private SharedPreferences prefs;
        
        private Snake mSnake;

        //periodically draws a new frame
        private final Runnable mDraw = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        
        private boolean mVisible;

        //listen for preference changes
        SnakeEngine(SharedPreferences prefs) {
        	this.prefs = prefs;
        	this.prefs.registerOnSharedPreferenceChangeListener(this);
        	SysMon.init(getApplicationContext(), Integer.parseInt(prefs.getString("poll_frequency", "10")));
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //stop polling the system and drawing frames
            mHandler.removeCallbacks(mDraw);
            SysMon.Stop();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                SysMon.init(getApplicationContext(), Integer.parseInt(prefs.getString("poll_frequency", "10")));
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDraw);
                SysMon.Stop();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // store the center of the surface, so we can draw the snake in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            //get initial system usage values
            SysMon.init(getApplicationContext(), Integer.parseInt(prefs.getString("poll_frequency", "10")));
            //initialise the snake
            speedVal = prefs.getString("snake_speed", "cpu");
            lengthVal = prefs.getString("snake_length", "ram");
            if(lengthVal.equalsIgnoreCase("cpu")) mLength = SysMon.CPU;
            else if(lengthVal.equalsIgnoreCase("ram")) mLength = SysMon.RAM;
            else if(lengthVal.equalsIgnoreCase("procs")) mLength = SysMon.Processes;
            
            mSnake = new Snake(mCenterX, mCenterY, mLength, prefs.getInt("snake_colour", 0xff00ff00));
            //draw the first frame
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            //stop polling the system and drawing frames
            mHandler.removeCallbacks(mDraw);
            SysMon.Stop();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            mOffset = xOffset;
            SysMon.init(getApplicationContext(), Integer.parseInt(prefs.getString("poll_frequency", "10")));
            drawFrame();
        }

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable.
         */
        void drawFrame() {
        	//get the current time, so we can calculate how long it took to draw this frame
        	long startTime = System.currentTimeMillis();

            if(lengthVal.equalsIgnoreCase("cpu")) mLength = SysMon.CPU;
            else if(lengthVal.equalsIgnoreCase("ram")) mLength = SysMon.RAM;
            else if(lengthVal.equalsIgnoreCase("procs")) mLength = SysMon.Processes;
            mSnake.setLength(mLength);
            
            if(speedVal.equalsIgnoreCase("cpu")) mSpeed = SysMon.CPU;
            else if(speedVal.equalsIgnoreCase("ram")) mSpeed = SysMon.RAM;
            else if(speedVal.equalsIgnoreCase("procs")) mSpeed = SysMon.Processes;
            
            if(invertSpeed) mSpeed = 100 - mSpeed;
        	
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            
            //move, then draw the snake
            try {
                c = holder.lockCanvas();
                if(c != null)
                {
                	mSnake.Move();
                	mSnake.Draw(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
            
            //sleeptime = 1000 / framerate - time take drawing this frame
            //framerate = speed (0-100) / 3 + 5 (gives a range of 5-38 fps)
            long sleepTime = 1000 / ((mSpeed / 3) + 5) - (System.currentTimeMillis() - startTime);
            mHandler.removeCallbacks(mDraw);
            if (mVisible) {
            	if(mSnake.isAlive())
            	{
            		if(sleepTime < 0) mHandler.postDelayed(mDraw, 10);
            		else mHandler.postDelayed(mDraw, sleepTime);
            	} else {
            		mHandler.post(mDraw);
            	}
            }
        }

        @Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			/*if(key.equals("snake_speed"))
			{
				mSpeed = Integer.parseInt(prefs.getString(key, "50"));
			}*/
        	if(key.equals("snake_colour"))
        	{
        		mSnake.setColor(prefs.getInt("snake_colour", 0x00ff00));
        	} else if (key.equals("snake_speed")) {
        		speedVal = prefs.getString("snake_speed", "cpu");
        	} else if (key.equals("snake_length")) {
        		lengthVal = prefs.getString("snake_length", "ram");
        	} else if (key.equals("invert_speed")) {
        		invertSpeed = prefs.getBoolean("invert_speed", false);
        	}
        }
    }
}
