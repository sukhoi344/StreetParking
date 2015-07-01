package chau.streetparking.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Chau Thai on 12/19/14.
 */
public class ImageUtil {

    /**
     * Adjust size of the bitmap to fit the screen width
     * @param bitmap to be adjusted
     * @return adjusted bitmap
     */
    public static Bitmap fitBitmapScreenWidth(Bitmap bitmap) {
        try {
            int screenWidth = getScreenWidth();

            double ratio = (double) bitmap.getWidth() / bitmap.getHeight();
            int imageWidth = screenWidth;
            int imageHeight = (int) (imageWidth / ratio);

            Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);

            return scaledImage;

        } catch (OutOfMemoryError e) {
            Log.e("ImageUtil", "out of memory error when resize image");
        }

        return bitmap;
    }

    /**
     * Adjust size of the bitmap to fit the screen height
     * @param bitmap to be adjusted
     * @return adjusted bitmap
     */
    public static Bitmap fitBitmapScreenHeight(Bitmap bitmap) {
        try {
            int screenHeight = getScreenHeight();

            double ratio = (double) bitmap.getWidth() / bitmap.getHeight();
            int imageHeight = screenHeight;
            int imageWidth = (int) (imageHeight * ratio);

            return Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);

        } catch (OutOfMemoryError e) {
            Log.e("Image view helper", "out of memory error when resize image");
        }

        return bitmap;
    }

    /**
     * Resize bitmap to match the new height with correct ratio
     * @param bitmap to be adjusted
     * @param newHeight new height of the bitmap
     * @return adjusted bitmap
     */
    public static Bitmap resizeBitmapHeight(Bitmap bitmap, int newHeight) {
        final float densityMultiplier = Resources.getSystem().getDisplayMetrics().density;

        int h = (int) (newHeight*densityMultiplier);
        int w = (int) (h * bitmap.getWidth()/((double) bitmap.getHeight()));

        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

        return bitmap;
    }

    /**
     * Get bitmap from uri. Return null if failed.
     * @param context
     * @param uriPath
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, String uriPath) {

        InputStream is = null;

        try {
            Uri photoUri = Uri.parse(uriPath);
            is = context.getContentResolver().openInputStream(photoUri);

            return BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {}
        }

        return null;
    }

    /**
     * Get bitmap from a Uri object, which is retrieved from a file chooser. This also
     * ensures that the bitmap is not too large to get MemOverflow exception
     * @param context
     * @param uri
     * @param maxDimension in px
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static Bitmap getBitmapFromUriChooser(Context context, Uri uri, int maxDimension) throws FileNotFoundException {
        AssetFileDescriptor fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");

        BitmapFactory.Options o1 = new BitmapFactory.Options();
        o1.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, o1);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(o1, maxDimension, maxDimension);

        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

        return bitmap;
    }

    /**
     * Reshape the bitmap into a rounded shape
     * @param bitmap orignal bitmap
     * @param radius circle radius in px
     * @return reshaped bitmap, return null if fail
     */
    public static Bitmap getCircleShape(Bitmap bitmap, int radius) {
        if (bitmap == null)
            return null;

        try {
            Bitmap sbmp;

            if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
                float smallest = Math.min(bitmap.getWidth(), bitmap.getHeight());
                float factor = smallest / radius;
                sbmp = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / factor),
                        (int) (bitmap.getHeight() / factor), false);
            } else {
                sbmp = bitmap;
            }

            Bitmap output = Bitmap.createBitmap(radius, radius,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, radius, radius);

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor("#BAB399"));
            canvas.drawCircle(radius / 2 + 0.7f,
                    radius / 2 + 0.7f, radius / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calculate the inSampleSize for the required width and height
     * @param options
     * @param reqWidth maximum width
     * @param reqHeight maximum height
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Resize bitmap to match the new height and width
     * @param bitmap to be adjusted
     * @param newHeight new height of the bitmap
     * @param newWidth new width of the bitmap
     * @return adjusted bitmap
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int newHeight, int newWidth) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /** @return current screen width in pixels */
    public static int getScreenWidth() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /** @return current screen height in pixels */
    public static int getScreenHeight() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /** Convert DP to pixels */
    public static int getPixelFromDP(int dp) {
        Resources r = Resources.getSystem();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    /** Convert pixels to DP */
    public static int getDPFromPixel(int px) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (16.0f * scale + 0.5f);
    }

    /**
     * Get status bar height.
     * @param context current context
     * @return status bar height
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * The actual application screen height is the different between
     * the physical screen height and the status bar height.
     * @param context corrent context
     * @return the actual application screen height (not the physical one)
     */
    public static int getAppScreenHeight(Context context) {
        return getScreenHeight() - getStatusBarHeight(context);
    }

    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.
                    getResources().getDisplayMetrics());
        }

        return 0;
    }

}