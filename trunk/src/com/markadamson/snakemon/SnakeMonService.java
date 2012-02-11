package com.markadamson.snakemon;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
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

    class SnakeEngine extends Engine implements OnSharedPreferenceChangeListener{

        private float mOffset;
        private float mCenterX;
        private float mCenterY;
        private float mWidth;
        private float mHeight;
        
        private SharedPreferences prefs;
        
        private Snake mSnake;

        private final Runnable mDraw = new Runnable() {
            public void run() {
                drawFrame();
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
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDraw);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // store the center of the surface, so we can draw the cube in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            mWidth = width;
            mHeight = height;
            prefs.getBoolean("keystring", true);
            mSnake = new Snake(mCenterX, mCenterY, Integer.parseInt(prefs.getString("snake_speed", "50")));
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
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            mOffset = xOffset;
            drawFrame();
        }

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable. You can do any drawing you want in
         * here. This example draws a wireframe cube.
         */
        void drawFrame() {
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
            mHandler.removeCallbacks(mDraw);
            if (mVisible) {
                mHandler.postDelayed(mDraw, 1000 / 25);
            }
        }

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences prefs, String key) {
			if(key.equals("snake_speed"))
			{
				mSnake.SetSpeed(Integer.parseInt(prefs.getString(key, "50")));
			}
			
		}

    }
}