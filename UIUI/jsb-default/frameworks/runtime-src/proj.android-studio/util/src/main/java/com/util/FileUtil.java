package com.util;

import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class FileUtil {

    private static final int READ_CACHE_LENGTH = 8192;

    public static void deleteFileIfExists(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static void ensureFile(File file) {
        if (file != null && !file.exists()) {
            ensureDirectory(file.getParentFile());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ensureDirectory(File directory) {
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void copyFile(InputStream src, String dst) throws IOException {
        copyFile(src, new File(dst));
    }

    public static void copyFile(InputStream src, File dst) throws IOException {

        BufferedOutputStream ou = null;
        try {
            ou = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buffer = new byte[READ_CACHE_LENGTH];
            int read = 0;
            while ((read = src.read(buffer)) != -1) {
                ou.write(buffer, 0, read);
            }
        } finally {
            IoUtil.close(src);
            IoUtil.close(ou);
        }
    }

    public static void copyFile(String src, String dst) throws IOException {
        BufferedInputStream in = null;
        BufferedOutputStream ou = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src));
            ou = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buffer = new byte[READ_CACHE_LENGTH];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                ou.write(buffer, 0, read);
            }
        } finally {
            IoUtil.close(in);
            IoUtil.close(ou);
        }
    }

    public static void saveBitmap(String path, Bitmap bitmap) {
        File file = new File(path);
        saveBitmap(file, bitmap);
    }

    public static void saveBitmap(File file, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            ensureFile(file);
            out = new FileOutputStream(file);
            // PNG is a lossless format, the compression factor (100) is ignored
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(out);
        }
    }

    public static String readFile(String path) {
        File file = new File(path);
        return readFile(file);
    }

    public static String readFile(File file) {
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new FileInputStream(file);
            streamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(streamReader);
            IoUtil.close(bufferedReader);
        }
        return null;
    }

    public static void writeFile(String path, String content) {
        File file = new File(path);
        writeFile(file, content);
    }

    public static void writeFile(File file, String content) {
        OutputStream outputStream = null;
        OutputStreamWriter streamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            outputStream = new FileOutputStream(file);
            streamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            bufferedWriter = new BufferedWriter(streamWriter);
            bufferedWriter.write(content);
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            IoUtil.close(outputStream);
            IoUtil.close(streamWriter);
            IoUtil.close(bufferedWriter);
        }
    }

    public static boolean deleteFile(File file) {
        File[] files = file.listFiles();
        if (files != null && files.length >   0) {
            for (File deleteFile : files) {
                if (deleteFile.isDirectory()) {
                    if (!deleteFile(deleteFile)) {
                        return false;
                    }
                } else {
                    if (!deleteFile.delete()) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}
