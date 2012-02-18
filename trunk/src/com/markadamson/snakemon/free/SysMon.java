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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class SysMon{
	public static int CPU = 0;
	public static int RAM = 0;
	public static int Processes = 0;
	
	private final static String DEB_TAG = "SysMon";
	
	private static Context context;
	private static int frequency;
	
	private final static Handler mHandler = new Handler();

    
    //periodically polls the system for usage stats
    private final static Runnable mPollSys = new Runnable() {
    	public void run() {
    		pollSys();
    	}
    };
	
	static void init(Context c, int f)
	{
		context = c;
		frequency = f;
		
		Log.d(DEB_TAG, "Init()");
		
		pollSys();
	}
	
	private static void pollSys()
	{
		CPU = readCPUUsage();
		RAM = readRAMUsage();
		Processes = getNumProcesses();
    	Log.d("CPU: ", Integer.toString(CPU));
    	Log.d("RAM: ", Integer.toString(RAM));
    	Log.d("Processes: ", Integer.toString(Processes));
		mHandler.removeCallbacks(mPollSys);
		mHandler.postDelayed(mPollSys, 1000 * frequency);
	}
	
	static void Stop()
	{
		mHandler.removeCallbacks(mPollSys);
	}
    
    private static int readCPUUsage() {
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
    
    private static int readRAMUsage() {
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
    
    private static int getNumProcesses()
    {
    	ActivityManager servMng = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<ActivityManager.RunningAppProcessInfo> list = servMng.getRunningAppProcesses();
    	return list.size();
    }
}