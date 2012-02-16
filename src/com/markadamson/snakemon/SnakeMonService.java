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

    class SnakeEngine extends Engine implements OnSharedPreferenceChangeListener {

        @SuppressWarnings("unused")
        private float mOffset;
        private float mCenterX;
        private float mCenterY;
        private int mCPU;
        private int mSpeed;
        
        private SharedPreferences prefs;
        
        private Snake mSnake;

        private final Runnable mDraw = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        
        private final Runnable mPollSys = new Runnable() {
        	public void run() {
        		pollSys();
        	}
        };
        
        private boolean mVisible;

        SnakeEngine(SharedPreferences prefs) {
        	this.prefs = prefs;
        	this.prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
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
            // store the center of the surface, so we can draw the cube in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            //prefs.getBoolean("keystring", true);
            mSpeed = 50;
            mSnake = new Snake(mCenterX, mCenterY, 50);
            pollSys();
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
         * by posting a delayed Runnable. You can do any drawing you want in
         * here. This example draws a wireframe cube.
         */
        void drawFrame() {
        	long startTime = System.currentTimeMillis();
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
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

            // Reschedule the next redraw
            if(mSnake.isAlive()) mSpeed = mCPU;
            else mSpeed=100;
            long sleepTime = 1000 / ((mSpeed / 3) + 5) - (System.currentTimeMillis() - startTime);
            mHandler.removeCallbacks(mDraw);
            if (mVisible) {
            	if(sleepTime < 0) mHandler.postDelayed(mDraw, 10);
            	else mHandler.postDelayed(mDraw, sleepTime);
            }
        }
        
        void pollSys() {
        	mCPU = readUsage();
        	Log.d("CPU: " ,Integer.toString(mCPU));
        	if(!mSnake.isAlive()) mSpeed = 100;
        	mHandler.removeCallbacks(mPollSys);
        	if(mVisible) mHandler.postDelayed(mPollSys, 10000);
        }

        @Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			/*if(key.equals("snake_speed"))
			{
				mSpeed = Integer.parseInt(prefs.getString(key, "50"));
			}*/
        }
        
        private int readUsage() {
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
    }
}
