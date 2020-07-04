package com.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.List;


public class Device {
	public static String androidid = "";
	public String country = "";
	public String language = "";
	public int sdkint;
	public String versionrelease = "";
	public static String versionname = "";
	public static int versionCode = -1;
	public String channel = "";
	private static String device_info = "";
	public String modelType = "";
	public static String uninstall_device_info = "";
	public static Context s_Context = null;
	static public Device createDevicFromPhone(Context context) {
		Device device = new Device();

		String aaid = AppConfigUtils.getStringFromPrefenence(context, "configid");
		if (TextUtils.equals("02:00:00:00:00:00", aaid) || TextUtils.isEmpty(aaid)) {
			aaid = generatorAndroidId(context);
		}
		if (TextUtils.equals("02:00:00:00:00:00", aaid) || TextUtils.isEmpty(aaid)) {
			aaid = generatorAndroidIds(context);
		}
		if (TextUtils.isEmpty(aaid)) {
			aaid = GameIDUtil.createUUIDString(36);
		}
		AppConfigUtils.saveStringToPrefenence(context, "configid", aaid);

		device.androidid = aaid;




		device.country = Locale.getDefault().getCountry();
		device.language = Locale.getDefault().getLanguage();
		device.sdkint = Build.VERSION.SDK_INT;  //系统的版本
		device.versionrelease = Build.VERSION.RELEASE;
		device.versionname = getVersionName(context);
		device.versionCode = AppConfigUtils.getVersionCode(context);
		device.channel = AppUtil.getChannel(context);
		//Log.e("channel=",device.channel);
		device.modelType = Build.MODEL;
		return device;
	}

	public void toDeviceInfoString() {
		StringBuilder builder = new StringBuilder();
		builder.append("aid=")
		.append(androidid)
		.append("&code=")
		.append(country)
		.append("&lan=")
		.append(language)
		.append("&svc=")
		.append(sdkint)
		.append("&svn=")
		.append(versionrelease)
		.append("&cvn=")
		//.append(versionname)
		.append("{0}")
		.append("&cvc=")
		.append(versionCode)
		.append("&chn=")
		.append(channel);
		//.append("]");
		device_info = builder.toString();
		uninstall_device_info = String.format("aid=%s&type=%s", androidid, modelType);
		uninstall_device_info = uninstall_device_info.replace(" ", "");
	}

	public static String getDeviceInfo() {
		return device_info;
	}
	public static String getDeviceID() {
		return androidid;
	}
	public static String getVersionCode() {
		return String.valueOf(versionCode);
	}
	public static String getVersionName() {
		return versionname;
	}
	public static String getUninstallDeviceInfo() {
		return uninstall_device_info;
	}

	 static private String generatorAndroidIds(Context context) {
	 	String androidId = Secure.getString(context.getContentResolver(),
	 			Secure.ANDROID_ID);
	 	return androidId;
	 }


	private static String getMacAddrOld(Context context)
	{
		String macString = "";
		WifiManager wifimsg = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if (wifimsg != null)
		{
			if (wifimsg.getConnectionInfo() != null)
			{
				if (wifimsg.getConnectionInfo().getMacAddress() != null)
				{
					macString = wifimsg.getConnectionInfo().getMacAddress();
				}
			}
		}
		return macString;
	}

	public static String getMacAddr() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:",b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				return res1.toString();
			}
		} catch (Exception ex) {
		}
		return "02:00:00:00:00:00";
	}

	static private String generatorAndroidId(Context context) {
		String addr = getMacAddrOld(context);
		if(addr.equals("02:00:00:00:00:00"))
		{
			addr = getMacAddr();
		}
		return addr;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	static private String generatorSerial(Context context) {
		if (Build.VERSION.SDK_INT > 8) {
			return Build.SERIAL;
		} else {
			return "";
		}
	}

	public static String getVersionName(Context context) {
		String versionname = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			versionname = packInfo.versionName;
		} catch (Exception e) {
		}

		return versionname;
	}
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			versionCode = packInfo.versionCode;
		} catch (Exception e) {
		}
		
		return versionCode;
	}

	public static String getMemory(Context context, String type) {
		if (TextUtils.isEmpty(type)) {
			type = "total";
		}

		try {
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Activity.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
			if (manager == null) {
				return "0.00";
			}
			manager.getMemoryInfo(info);
			if (TextUtils.equals(type, "total")) {
				return Device.byteToMBString(info.totalMem);
			} else {
				return Device.byteToMBString(info.availMem);
			}
		} catch (Exception e) {
			Log.e("Device", e.getMessage());
		}
		return "0.00";
	}

	public static String byteToMBString(long size) {

		long MB = 1024 * 1024;//定义MB的计算常量
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数
		String resultSize = "";
		if (size / MB >= 1) {
			//如果当前Byte的值大于等于1MB
			resultSize = df.format(size / (float) MB);
		}
		return resultSize;
	}
}
