package com.util;

import android.content.Context;
import android.os.Environment;
import java.io.*;

public class LocalPackageBufDir {
	static String TAG = LocalPackageBufDir.class.getSimpleName();
	

	public static void writeFile(String fileName, String message) {
		writeFileSdcard(fileName, message);
	}

	public static String readFile(String fileName) {
		return readFileSdcard(fileName);
	}
	
	public static void CreateDir(String dir) {
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * 获取db文件所在的文件夹
	 *
	 * @param ctx
	 * @param productDir
	 * @return
	 */
	public static String GetShareDBPath(Context ctx, String productDir) {
		String dbpath = ctx.getCacheDir().getAbsolutePath();
		try {
			String sdStatus = Environment.getExternalStorageState();
			if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				// 共享数据跟目录
				dbpath = Environment.getExternalStorageDirectory() + File.separator + "data"
					+ File.separator + "data" + File.separator + productDir + File.separator;
				CreateDir(dbpath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//SDKLog.d(TAG, "DB dir path is " + dbpath);
		return dbpath;
	}
	
	
	static void writeFileSdcard(String fileName, String message) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 文件不存在或者读取有问题，返回NULL
	static String readFileSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			//res = EncodingUtils.getString(buffer, "UTF-8");
			res = new String(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return res;
	}
}
