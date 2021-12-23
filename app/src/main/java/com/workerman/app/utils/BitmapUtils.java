package com.workerman.app.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Bitmap관련 처리를 위한 Utility Class
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    public static final int THUMB_MINI = Images.Thumbnails.MINI_KIND;
    public static final int THUMB_MICRO = Images.Thumbnails.MICRO_KIND;
    public static final int THUMB_FULL = Images.Thumbnails.FULL_SCREEN_KIND;

    /**
     * 입력받은 bitmap에 대하여 sidePx 기준으로 scale한 bitmap 이미지를 반환한다.
     * @param bm
     * @param sidePx
     * @param max
     * @return
     */
    public static Bitmap getScaled(Bitmap bm, int sidePx, boolean max) {
        float w = bm.getWidth();
        float h = bm.getHeight();
        float wRatio = sidePx / w;
        float hRatio = sidePx / h;
        float ratio = max ? Math.min(wRatio, hRatio) : Math.max(wRatio, hRatio);
        w = ratio * w;
        h = ratio * h;
        return Bitmap.createScaledBitmap(bm, (int) w, (int) h, true);
    }

    /**
     * Exif에서 이미지의 방향을 확인, 360degree 기준으로 값을 반환한다.
     * @param path
     * @return 이미지의 방향
     */
    public static float checkRotate(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            return rotation;

        }
        catch (IOException e) {
            Debug.e(TAG, e.getMessage());
            return 0;
        }
    }

    /**
     * 이미지를 down scaling 처리한다.
     * @param filePath
     * @param maxSize
     * @return scaled bitmap
     */
    public static Bitmap getDownScale(String filePath, int maxSize) {

        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(filePath, options);

            int size = calculateInSampleSize(options, maxSize, maxSize);

            options.inSampleSize = size;
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeFile(filePath, options);
        }
        catch (Exception e) {
            Debug.e(TAG, e.getMessage());
            return null;
        }
        catch (OutOfMemoryError e) {
            Debug.e(TAG, "OutOfMemoryError:"+e.getMessage());
            return null;
        }

        if(bitmap == null){
            Debug.d(TAG, "bitmap == null");
            return null;
        }
        int degree = (int) checkRotate(filePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (degree != 0) {
            try {
                Matrix matrix = new Matrix();
                matrix.preRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            }
            catch (Exception e) {
                Debug.e(TAG, e.getMessage());
            }
            catch (OutOfMemoryError e) {
                Debug.e(TAG, "OutOfMemoryError2:"+e.getMessage());
            }
        }

        return bitmap;
    }

    /**
     * 입력받은 가로,세로 길이 및 BitmapFactory.Options에 맞는 이미지의 BitmapFactory.Options의 sample사이즈를 계산한다.
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            if (width > height) {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            else {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
        }

        Debug.d(TAG, "org width : " + width);
        Debug.d(TAG, "org height : " + height);
        Debug.d(TAG, "req width : " + reqWidth);
        Debug.d(TAG, "req height : " + reqHeight);
        Debug.d(TAG, "inSampleSize : " + inSampleSize);

        return inSampleSize;
    }

    /**
     * bitmap을 입력받은 path에 이미지 파일로 변환하여 파일을 반환한다.
     * @param bitmap
     * @param path
     * @return file
     */
    public static File bitmapToFile(Bitmap bitmap, String path) {

        File file = new File(path);
        OutputStream out = null;

        try {
            file.createNewFile();
            out = new FileOutputStream(file);

            bitmap.compress(CompressFormat.JPEG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                file = null;
            }
        }
        return file;
    }

    /**
     * 입력받은 경로에 위치한 파일에 대하여, 해당 파일이 bitmap일 경우 입력받은 가로,세로에 맞는 scale된 bitmap을 반환한다.
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return bitmap
     */
    public static Bitmap getDownSampling(String filePath, int reqWidth, int reqHeight){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            final int height = options.outHeight;
            final int width = options.outWidth;


            int inSampleSize = 1;


            if (height > reqHeight || width > reqWidth) {

                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
//            inSampleSize++;

            options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
//            options.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFile(filePath, options);
            
        }
        catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * upload를 위하여 입력받은 경로에 위치한 이미지를 down sampling처리하여 새 bitmap을 반환한다.
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return Bitmap
     */
    public static Bitmap getDownSamplingForUpload(String filePath, int reqWidth, int reqHeight){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            final int height = options.outHeight;
            final int width = options.outWidth;


            int inSampleSize = 1;


            if (height > reqHeight || width > reqWidth) {

                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

//                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                inSampleSize = widthRatio;
            }
//            inSampleSize++;

            options = new BitmapFactory.Options();
//            options.inSampleSize = inSampleSize*2;
            options.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(filePath, options);

        }
        catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 입력받은 bitmap을 Center Crop 처리한 bitmap을 생성, 반환한다.
     * @param source
     * @param newHeight
     * @param newWidth
     * @return Bitmap
     */
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    /**
     * exif내 저장된 orientation값이 의미하는게 몇degree의 회전인지 값으로 반환.
     * @param exifOrientation
     * @return degree
     */
    public static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * uri에 해당하는 파일의 방향을 반환
     * @param context
     * @param uri
     * @return degree
     */
    public static float checkRotate(Context context, Uri uri) {
        try {
            String[] projection = { Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * path 해당하는 파일의 방향을 반환
     * @param context
     * @param path
     * @return degree
     */
    public static float checkRotate(Context context, String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            return rotation;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 입력받은 degree로 bitmap을 회전
     * @param bm
     * @param degree
     * @return Bitmap
     */
    public static Bitmap rotate(Bitmap bm, float degree){

        Bitmap bitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.preRotate(degree);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            
            if(bitmap != bm) {
                bm.recycle();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * bitmap에 대해 thumnail 이미지 생성
     * @param ctx
     * @param uri
     * @param kind
     * @return bitmap
     */
    public static Bitmap getThumbnail(Context ctx, Uri uri, int kind){
        Bitmap bm = null;
        try {
            bm = Images.Thumbnails.getThumbnail(ctx.getContentResolver(), Integer.parseInt(uri.getLastPathSegment()), kind, (BitmapFactory.Options) null);
        }
        catch (Exception e) {
            e.printStackTrace();
            bm = null;
        }
        return bm;
    }

    /**
     * 가장 최근에 생성된 이미지의 경로를 반환
     * @param ctx
     * @return 이미지의 Path
     */
    public static String getLastImagePath(Context ctx){
        String ret = null;

        try {
            String[] projection = new String[]{Images.ImageColumns._ID,
                    Images.ImageColumns.DATA,
                    Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    Images.ImageColumns.DATE_TAKEN,
                    Images.ImageColumns.MIME_TYPE
            };

            CursorLoader loader = new CursorLoader(ctx, Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, projection[3] + " DESC");
            Cursor cursor = loader.loadInBackground();

            int columnIndex = cursor.getColumnIndexOrThrow(Images.Media.DATA);

            if (cursor.moveToFirst()) {
                ret = cursor.getString(columnIndex);
            }
        }
        catch (Exception e) {
            ret = null;
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 가장 최근에 생성된 이미지의 Uri를 반환
     * @param ctx
     * @return Uri
     */
    public static Uri getLastImageUri(Context ctx){
        Uri uri = null;

        try {
            String[] projection = new String[]{Images.ImageColumns._ID,
                    Images.ImageColumns.DATA,
                    Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    Images.ImageColumns.DATE_TAKEN,
                    Images.ImageColumns.MIME_TYPE
            };

            CursorLoader loader = new CursorLoader(ctx, Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, projection[3] + " DESC");
            Cursor cursor = loader.loadInBackground();

            if (cursor.moveToFirst()) {
                Integer id = cursor.getInt(0);
                uri = Uri.withAppendedPath(Images.Media.EXTERNAL_CONTENT_URI, id.toString());
            }
        }
        catch (Exception e) {
            uri = null;
            e.printStackTrace();
        }
        return uri;
    }
}
