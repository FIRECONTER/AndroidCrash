package com.utils.crash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
public class crash implements UncaughtExceptionHandler {

	//常用的域
	private static final String TAG = "CrashHandler";  
    private static final boolean DEBUG = true;  
    
    //获取实际的外设存储路径。
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/ryg_test/log/";  
    private static final String FILE_NAME = "crash";  
  
    //log文件的后缀名  
    private static final String FILE_NAME_SUFFIX = ".trace";  
  
    
  
    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）  
    private UncaughtExceptionHandler mDefaultCrashHandler;  
  
    private Context mContext;  
    
    
	private static final UncaughtExceptionHandler myhandler = new crash();
	private crash() {
		// TODO Auto-generated constructor stub
	}
	
	public static UncaughtExceptionHandler getDefaultException()
	{
		return myhandler;
	}
	
	//这里主要完成初始化工作  
    public void init(Context context) {  
        //获取系统默认的异常处理器  
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();  
        //将当前实例设为系统默认的异常处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);  
        //获取Context，方便内部使用  
        mContext = context.getApplicationContext();//获取整个应用程序的context
        
    }  
    
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {  
		
		//选择上传服务器，还是选择存储于本地。
		//存储于本地涉及到IO 需要异常捕获。
		//这两个过程就是比较关键的过程决定crash 的捕获问题。
		try {
			dumpExceptionToSDCard(ex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ex.printStackTrace();
		//如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己  
        if (mDefaultCrashHandler != null) {  
            mDefaultCrashHandler.uncaughtException(thread, ex); 
            //如果默认的UncaughtExcpetion 不为空，那么用默认的异常处理清除。
        } else {  
            Process.killProcess(Process.myPid());//这个Proceed使用的是Android.os.Process
            //返回的是当前线程的pid 。可以用Process 的方法kill掉。
        }  
		
	}
	
	 private void dumpExceptionToSDCard(Throwable ex) throws IOException {  
	        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡  
	        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
	            if (DEBUG) {  
	                Log.w(TAG, "sdcard unmounted,skip dump exception");  
	                return;  
	            }  
	        }  
	  
	        File dir = new File(PATH);  
	        if (!dir.exists()) {  
	            dir.mkdirs();  
	        }  
	        long current = System.currentTimeMillis();  
	        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));  
	        //以当前时间创建log文件  
	        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);  
	  
	        try {  
	            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));  
	            //导出发生异常的时间  
	            pw.println(time);  
	  
	            //导出手机信息  
	            dumpPhoneInfo(pw);  
	  
	            pw.println();  
	            //导出异常的调用栈信息  
	            ex.printStackTrace(pw);  
	  
	            pw.close();  
	        } catch (Exception e) {  
	            Log.e(TAG, "dump crash info failed");  
	        }  
	    }  
	  
	    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {  
	        //应用的版本名称和版本号  
	        PackageManager pm = mContext.getPackageManager();//显示版本相关的信息。 
	        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);  
	        pw.print("App Version: ");  
	        pw.print(pi.versionName);  
	        pw.print('_');  
	        pw.println(pi.versionCode);  
	  
	        //android版本号  
	        pw.print("OS Version: ");  
	        pw.print(Build.VERSION.RELEASE);  
	        pw.print("_");  
	        pw.println(Build.VERSION.SDK_INT);  
	  
	        //手机制造商  
	        pw.print("Vendor: ");  
	        pw.println(Build.MANUFACTURER);  
	  
	        //手机型号  
	        pw.print("Model: ");  
	        pw.println(Build.MODEL);  
	  
	        //cpu架构  
	        pw.print("CPU ABI: ");  
	        pw.println(Build.CPU_ABI);  
	    }  
	  
	    private void uploadExceptionToServer(Throwable th) {  
	        //TODO Upload Exception Message To Your Web Server  
	    	//代码上传至服务器中。
	    	
	    }  

}
