package com.util;

/**
 * Created by WangYi on 23/8/17.
 */

public class GameIDUtil {


    public static final String[] z = new String[]{
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "a", "b", "c", "d", "e", "f", "g",
            "6", "7", "8", "9", ":", "-", "_",
            "U", "V", "W", "X", "Y", "Z",
            "o", "p", "q", "r", "s", "t",
            "0", "1", "2", "3", "4", "5",
            "O", "P", "Q", "R", "S", "T",
            "h", "i", "j", "k", "l", "m", "n"
    };

    public static String createUUIDString(int bitCount) {


        StringBuilder builder = new StringBuilder();

        for (int i=0; i<bitCount; i++) {
            int value = (int)(Math.random()*(64));
            builder.append(z[value]);
        }

        android.util.Log.e("UUID", builder.toString());


        return builder.toString();
    }
}
