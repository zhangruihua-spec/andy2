package com.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by WangYi on 12/3/19.
 */

public class KeyChainTool<T> {


    private String dirName = ".omg_ooxx";
    public boolean keyChainSave(String fileName, T bean) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File dirFile = new File(sdCardDir,dirName);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File sdFile = new File(dirFile, "." + fileName);
            Log.d("cocos2d-x","keyChainSave SD卡路径：" + sdFile.getAbsolutePath());
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(bean);//写入
                fos.close();
                oos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public T keyChainLoad(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //检测sd卡是否存在
            T bean;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File dirFile = new File(sdCardDir,dirName);
            if(dirFile.exists()){
                File sdFile = new File(dirFile, "." + fileName);
                Log.d("cocos2d-x","keyChainLoad SD卡路径：" + sdFile.getAbsolutePath());
                try {
                    if(sdFile.exists()){
                        FileInputStream fis = new FileInputStream(sdFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        bean = (T) ois.readObject();
                        fis.close();
                        ois.close();
                        return bean;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        } else {
            return null;
        }
    }

}
