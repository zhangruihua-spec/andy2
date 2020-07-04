package org.cocos2dx.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import org.cocos2dx.javascript.AppActivity;

import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class DeviceInfo {
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
	public static String gpref = "";
	public static String adjustadid = "";
	public static String adjust_gps_adid = "";
	public static String adjust_androidid = "";
	public static String adjust_macshortmd5 = "";

	public static Context s_Context = null;
	static public DeviceInfo createDevicFromPhone(Context context) {
		DeviceInfo device = new DeviceInfo();

		String aaid = AppConfigUtils.getStringFromPrefenence(context, "configid");
		if (TextUtils.equals("02:00:00:00:00:00", aaid) || TextUtils.isEmpty(aaid)) {
			aaid = generatorAndroidId(context);
		}
		if (TextUtils.equals("02:00:00:00:00:00", aaid) || TextUtils.isEmpty(aaid)) {
			aaid = generatorAndroidIds(context);
		}
		if (TextUtils.isEmpty(aaid)) {
			aaid = GameIDCreateUtil.createUUIDString(36);
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
		String gprefurl = AppConfigUtils.getStringFromPrefenence(context,AppConfigUtils.GP_REFFERERURL);
		if(!TextUtils.isEmpty(gprefurl)){
			device.gpref = gprefurl;
		}
		String adjustadid = AppConfigUtils.getStringFromPrefenence(context,AppConfigUtils.ADJUST_ADID);
		if(!TextUtils.isEmpty(adjustadid)){
			device.adjustadid = adjustadid;
		}
		String adjust_gps_adid = AppConfigUtils.getStringFromPrefenence(context,AppConfigUtils.ADJUST_GPS_ADID);
		if(!TextUtils.isEmpty(adjust_gps_adid)){
			device.adjust_gps_adid = adjust_gps_adid;
		}

		String adjust_androidid = AppConfigUtils.getStringFromPrefenence(context,AppConfigUtils.ADJUST_ANDROIDID);
		if(!TextUtils.isEmpty(adjust_androidid)){
			device.adjust_androidid = adjust_androidid;
		}
		String adjust_macshortmd5 = AppConfigUtils.getStringFromPrefenence(context,AppConfigUtils.ADJUST_MACSHORTMD5);
		if(!TextUtils.isEmpty(adjust_macshortmd5)){
			device.adjust_macshortmd5 = adjust_macshortmd5;
		}
		Log.e("device.gpref=",device.gpref);
		Log.e("device.adjustadid=",device.adjustadid);
		Log.e("device.adjust_gps_adid=",device.adjust_gps_adid);
		Log.e("device.adjust_android=",device.adjust_androidid);
		Log.e("adjust_macshortmd5=",device.adjust_macshortmd5);
		return device;
	}

	public void toDeviceInfoString() {
		StringBuilder builder = new StringBuilder();
		String gprefencode =   String.format("{%s}",Uri.encode(gpref));
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
		.append(channel)
		.append("&gpref=")
		.append(gprefencode)
		.append("&adid=")
		.append(adjustadid)
		.append("&gps_adid=")
		.append(adjust_gps_adid)
		.append("&android_id=")
		.append(adjust_androidid)
		.append("&mac=")
		.append(adjust_macshortmd5);
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
				return byteToMBString(info.totalMem);
			} else {
				return byteToMBString(info.availMem);
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
