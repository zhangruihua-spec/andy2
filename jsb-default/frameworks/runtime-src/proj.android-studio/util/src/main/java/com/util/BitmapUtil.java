package com.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class BitmapUtil {

    // 生成圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 14;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    @Deprecated
    // 将Bitmap转换成Base64
    public static String getImgStr(Bitmap bit, int quality) {

        if (bit == null) {
            return null;
        }

        byte[] bytes = getImgBytes(bit, quality);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] getImgBytes(Bitmap bit, int quality) {

        if (bit == null) {
            return null;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bit.compress(CompressFormat.JPEG, quality, bos);// 参数100表示不压缩
            return bos.toByteArray();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {

        if (options == null || reqWidth <= 0 || reqHeight <= 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(Context context, Uri uri, int reqWidth, int reqHeight) {

        if (context == null || uri == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(uri);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // It seems that BitmapFactory closed the stream after decode
            BitmapFactory.decodeStream(stream, null, options);
            IoUtil.close(stream);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            stream = context.getContentResolver().openInputStream(uri);
            // It seems that BitmapFactory closed the stream after decode
            return BitmapFactory.decodeStream(stream, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(stream);
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromLocalFile(Context context, File file, int reqWidth, int reqHeight) {

        if (context == null || file == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // It seems that BitmapFactory closed the stream after decode
            BitmapFactory.decodeStream(stream, null, options);
            IoUtil.close(stream);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            stream = new FileInputStream(file);
            // It seems that BitmapFactory closed the stream after decode
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
            }

            // try again
            if(bitmap == null) {
                try {
                    options.inSampleSize = options.inSampleSize * 2;
                    bitmap = BitmapFactory.decodeStream(stream, null, options);
                }catch (Exception e) {
                }
            }
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(stream);
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(res, resId, options);
        }catch (Exception e) {
        }

        if(bitmap == null) {
            try {
                options.inSampleSize *= 2;
                bitmap = BitmapFactory.decodeResource(res, resId, options);
            }catch (Exception e) {
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapInSize(Bitmap bitmap, int reqWidth, int reqHeight) {

        if (bitmap == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        float reqRate = reqHeight / (float) reqWidth;
        float rate = bitmap.getHeight() / (float) bitmap.getWidth();

        if (rate > reqRate) {
            int height = (int)(bitmap.getWidth() * reqRate);
            int offsetY = (int)((bitmap.getHeight() - height) / 2);
            Matrix matrix = new Matrix();
            float scale = reqWidth / (float)bitmap.getWidth();
            matrix.setScale(scale, scale);
            return Bitmap.createBitmap(bitmap, 0, offsetY, bitmap.getWidth(), height, matrix, false);
        }

        int width = (int)(bitmap.getHeight() / reqRate);
        int offsetX = (int)((bitmap.getWidth() - width) / 2);
        Matrix matrix = new Matrix();
        float scale = reqWidth / (float)width;
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(bitmap, offsetX, 0, width, bitmap.getHeight(), matrix, false);
    }

    public static Bitmap decodeBitmapFromStream(Context context, Uri uri, int reqWidth, int reqHeight) {

        if (context == null || uri == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        Bitmap bitmap = decodeSampledBitmapFromStream(context, uri, reqWidth, reqHeight);
        Bitmap targetBitmap = createBitmapInSize(bitmap, reqWidth, reqHeight);
        if (!bitmap.equals(targetBitmap)) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return targetBitmap;
    }

    public static Bitmap decodeBitmapFromFromResource(Context context, int resId,
            int reqWidth, int reqHeight) {
        if(context == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), resId, reqWidth, reqHeight);
        Bitmap targetBitmap = createBitmapInSize(bitmap, reqWidth, reqHeight);
        if (!bitmap.equals(targetBitmap)) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return targetBitmap;
    }

    public static Bitmap decodeBitmapFromFromLocal(Context context, File file, int reqWidth, int reqHeight) {
        if(context == null || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        Bitmap bitmap = decodeSampledBitmapFromLocalFile(context, file, reqWidth, reqHeight);
        if(bitmap == null) {
            return null;
        }
        Bitmap targetBitmap = createBitmapInSize(bitmap, reqWidth, reqHeight);
        if (!bitmap.equals(targetBitmap)) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return targetBitmap;
    }

    public static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<Context> contextReference;
        private int data = 0;
        int width;
        int height;

        public BitmapWorkerTask(ImageView imageView, Context context) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            contextReference = new WeakReference<Context>(context);
            width = imageView.getWidth();
            height = imageView.getHeight();
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            if(contextReference != null && imageViewReference != null){
                final Context context = contextReference.get();
                final ImageView imageView = imageViewReference.get();
                if(context != null && imageView != null) {


                    return decodeSampledBitmapFromResource(context.getResources(), data, width, height);
                }
            }
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    //imageView.setImageBitmap(bitmap);
                    BitmapDrawable drawable = new BitmapDrawable(contextReference.get().getResources(), bitmap);
                    ViewCompatUtil.setBackground(imageView, drawable);
                }
            }
        }
    }



    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
