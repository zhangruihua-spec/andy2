package com.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class MobileDeviceInfo {
	private static String SHARE_DEVICE = "gold.teenpatti.json";
    //获取硬件版API版本号
    public static String getUniqueDeviceIDVersion(){
        return "0.0.0";
    }

	/*
	 * 获取DEVICE_ID,ANDROID_ID,MAC这三个值合并为一个字符串进行MD5作为设备唯一标识
	 * */
	public static String getUniqueDeviceID (Activity activity){
		String uniqueID = "";

        try {
            // 优先读取本地db
            String dbFile = LocalPackageBufDir.GetShareDBPath(activity, "com.gold.id") + SHARE_DEVICE;
//            Log.e("----->uniqueID dbFile: ", dbFile);
            File dbf = new File(dbFile);
            if (dbf.exists()) {
            	uniqueID = LocalPackageBufDir.readFile(dbFile);
            }

//            Log.e("----->uniqueID A：", uniqueID);
            // 没取到uniqueID重新生成
            if (!IsValidString(uniqueID)) {
        		String device_id = getIMEI(activity);
        		String androidid = getAndroidId(activity);
        		String mac = getMacAddress(activity);
        		
        		uniqueID = device_id + androidid + mac;
//                Log.e("----->uniqueID E：", uniqueID);
        		if(uniqueID.equals("")){
        			//为空的话生成随机字符串
        			uniqueID = getMd5(UUID.randomUUID().toString());
//                    Log.e("----->uniqueID B：", uniqueID);
        		}
        		else{
        			uniqueID = getMd5(uniqueID);
//                    Log.e("----->uniqueID C：", uniqueID);
        		}
            	
        		//存储到本地
        		LocalPackageBufDir.writeFile(dbFile, uniqueID);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }

//        Log.e("----->uniqueID ：", uniqueID);
		return uniqueID;
	};
	
    public static boolean IsValidString(String s) {
        return (null != s) && (!s.equals(""));
    }

    
    private static String getMd5(String str) {
        if (!IsValidString(str)) {
            return "";
        }
        
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            char[] charArray = str.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++)
                byteArray[i] = (byte) charArray[i];
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = (md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            //SDKLog.i("device id is " + hexValue.toString());
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    private static String getAndroidId(Activity activity) {
        String androidId = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
        if(androidId == null){
        	androidId = "";
        }
        //统一使用小写，是否存在不同API获取的结果大小写不同的情况未知
        androidId = androidId.toLowerCase();

        
        return androidId;
    }
    
    private static boolean checkPermission(Context context, String permission) {
		boolean result = false;
		if (Build.VERSION.SDK_INT >= 23) {
		    try {
		        Class<?> clazz = Class.forName("android.content.Context");
		        Method method = clazz.getMethod("checkSelfPermission", String.class);
		        int rest = (Integer) method.invoke(context, permission);
		        if (rest == PackageManager.PERMISSION_GRANTED) {
		            result = true;
		        } else {
		            result = false;
		        }
		    } catch (Exception e) {
		        result = false;
		    }
		} else {
		    PackageManager pm = context.getPackageManager();
		    if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
		        result = true;
		    }
		}
		return result;
    }
 
    public static String getIMEI(Activity activity) {
        try {
            if (checkPermission(activity, Manifest.permission.READ_PHONE_STATE)) {
                TelephonyManager telephonyManager = (android.telephony.TelephonyManager) activity
                        .getSystemService(Context.TELEPHONY_SERVICE);
                
                String IMEI = telephonyManager.getDeviceId();
                if(IMEI == null){
                	 return "";
                }

                return IMEI;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
	
    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private static boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
//            Log.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:" + "access wifi state is enabled");
            return true;
        } else
            return false;
    }
    
    /**
     * android 6.0以下mac地址获取
     */
    private static String getMacAddressFirst(Context context) {
    	if (Build.VERSION.SDK_INT < 23) {
	        if (isAccessWifiStateAuthorized(context)) {
	            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	            WifiInfo wifiInfo = null;
	            try {
	                wifiInfo = wifiMgr.getConnectionInfo();
	                return wifiInfo.getMacAddress();
	            } catch (Exception e) {
//	                Log.e("----->" + "NetInfoManager", "getMacAddressFirst:" + e.toString());
	            }
	
	        }
    	}
    	
        return "";

    }

    /**
     * 4.0一直到6.0，7.0系统都可以获取得到Mac地址
     */
    private static String getMacAddressSecond() {
        try {
//        	Log.e("----->" + "NetInfoManager", "getMacAddressSecond" );
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
        return "";
    }
    /*
     * 获取MAC地址方法
     * return:统一使用小写， 如果获取不到返回""
     * */
    private static String getMacAddress(Activity activity){
        String mac = getMacAddressFirst(activity.getApplicationContext());
        if(mac.equals(null) || mac.equals("") || mac.equals("02:00:00:00:00:00")){
        	mac = getMacAddressSecond();
        }
        
        if(mac.equals("02:00:00:00:00:00")){
        	mac = "";
        }
        
        mac = mac.toLowerCase();

        return mac;
    }

}
