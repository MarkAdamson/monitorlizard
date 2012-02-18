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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Random;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
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

        private String DEB_TAG = "SnakeEngine";
		@SuppressWarnings("unused")
        private float mOffset;
        private float mCenterX;
        private float mCenterY;
        private int mCPU;
        private int mRAM;
        private int mProcs;
        private int mSpeed;
        
        private int ticker;
        
        private SharedPreferences prefs;
        
        private Snake mSnake;

        //periodically draws a new frame
        private final Runnable mDraw = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        
        //periodically polls the system for usage stats
        private final Runnable mPollSys = new Runnable() {
        	public void run() {
        		pollSys();
        	}
        };
        
        private boolean mVisible;

        //listen for preference changes
        SnakeEngine(SharedPreferences prefs) {
        	this.prefs = prefs;
        	this.prefs.registerOnSharedPreferenceChangeListener(this);
        	ticker = 0;
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
            mHandler.removeCallbacks(mPollSys);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                pollSys();
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDraw);
                mHandler.removeCallbacks(mPollSys);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // store the center of the surface, so we can draw the snake in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            //get initial system usage values
            pollSys();
            //initialise the snake
            mSnake = new Snake(mCenterX, mCenterY, mRAM);
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
            mHandler.removeCallbacks(mPollSys);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            mOffset = xOffset;
            pollSys();
            drawFrame();
        }

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable.
         */
        void drawFrame() {
        	//get the current time, so we can calculate how long it took to draw this frame
        	long startTime = System.currentTimeMillis();
        	
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

            //if the the snake is dead, tidy it up at full speed
            mSpeed = mCPU;
            
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
        
        void pollSys() {
        	mCPU = readCPUUsage();
        	mRAM = readRAMUsage();
        	mProcs = getNumProcesses();
        	Log.d("CPU: ", Integer.toString(mCPU));
        	Log.d("RAM: ", Integer.toString(mRAM));
        	Log.d("Processes: ", Integer.toString(mProcs));
        	if(mSnake!=null) mSnake.setLength(mRAM);
        	
        	//reschedule the next system poll for 10 seconds time
        	mHandler.removeCallbacks(mPollSys);
        	if(mVisible) mHandler.postDelayed(mPollSys, 5000);
        }

        @Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			/*if(key.equals("snake_speed"))
			{
				mSpeed = Integer.parseInt(prefs.getString(key, "50"));
			}*/
        }
        
        private int readCPUUsage() {
            try {
                RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
                String load = reader.readLine();

                String[] toks = load.split(" ");

                long idle1 = Long.parseLong(toks[5]);
                long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                      + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                try {
                    Thread.sleep(360);
                } catch (Exception e) {}

                reader.seek(0);
                load = reader.readLine();
                reader.close();

                toks = load.split(" ");

                long idle2 = Long.parseLong(toks[5]);
                long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                float usage = (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
                return (int)(usage*100f);
                //return 0;

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return 0;
        } 
        
        private int readRAMUsage() {
            try {
                RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
                String line = reader.readLine();
                		
                long memTotal = Long.parseLong(line.substring(9, line.length()-3).trim());
                
                line = reader.readLine();
                
                long memFree = Long.parseLong(line.substring(8, line.length()-3).trim());
                
                reader.close();
                
                int usage = (int) (100f / memTotal * (memTotal - memFree));
                return usage;

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return 0;
        }
        
        private int getNumProcesses()
        {
        	ActivityManager servMng = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        	List<ActivityManager.RunningAppProcessInfo> list = servMng.getRunningAppProcesses();
        	return list.size();
        }
    }
}