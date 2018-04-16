package com.mudassirkhan.androidportfolio.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    //Static method used to get a resized circle Bitmap from the Uri passed in as a parameter
    static Bitmap getResizedCircleBitmap(Context context, String largeBitmapUriString, int imagePlaceHolder) {

        Bitmap resizedCircleBitmap = null;

        if (largeBitmapUriString != null && !largeBitmapUriString.isEmpty()) {

            Uri largeIconUri = Uri.parse(largeBitmapUriString);

            try {
                resizedCircleBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), largeIconUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            resizedCircleBitmap = BitmapFactory.decodeResource(context.getResources(), imagePlaceHolder);
        }

        if (resizedCircleBitmap != null) {
            resizedCircleBitmap = ImageUtils.getCircleBitmap(ImageUtils.scaleCenterCrop(resizedCircleBitmap));
        }

        return resizedCircleBitmap;
    }


    //Static method used to convert to a circular Bitmap the Bitmap passed in as a parameter
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(outputBitmap);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return outputBitmap;
    }


    //Static method used to rescale the Bitmap passed in as a parameter
    private static Bitmap scaleCenterCrop(Bitmap sourceBitmap) {
        int sourceWidth = sourceBitmap.getWidth();
        int sourceHeight = sourceBitmap.getHeight();

        int newWidth = 400;
        int newHeight = 400;

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, sourceBitmap.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(sourceBitmap, null, targetRect, null);

        return dest;
    }


    //Static method used to create the file that will be used for caching an Image
    public static File createTemporaryImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String temporaryImageFileName = "GMAndroidPortfolio_Temp_JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();
        return File.createTempFile(temporaryImageFileName, ".jpg", storageDir);
    }


    //Static method used to delete an image for the given path
    public static boolean deleteFile(Context context, String imageFilePath) {

        boolean hasBeenDeleted = false;

        if (imageFilePath != null) {

            File imageFile = new File(imageFilePath);
            hasBeenDeleted = imageFile.delete();

        } else {

            Toast.makeText(context, R.string.file_fragment_no_image_to_delete, Toast.LENGTH_SHORT).show();

        }

        return hasBeenDeleted;
    }


    //Static method that allows us to add an image to the picture Gallery
    private static void galleryAddPic(Context context, String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imagePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    //Static method that allows us to save the picture from the given path
    public static String savePicture(Context context, String mTempPicturePath) {

        String savedImagePath = null;

        if (mTempPicturePath != null) {

            //We create the new file in the external storage
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "GMAndroidPortfolio_Final_JPEG_" + timeStamp + ".jpg";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/GMAndroidPortfolio");
            boolean success = true;

            //We create the storage directory
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }

            //Now we save the image
            if (success) {
                File imageFile = new File(storageDir, imageFileName);
                savedImagePath = imageFile.getAbsolutePath();

                try {
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    Bitmap image = BitmapFactory.decodeFile(mTempPicturePath);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                galleryAddPic(context, savedImagePath);

                Toast.makeText(context, R.string.file_fragment_image_saved, Toast.LENGTH_SHORT).show();
            }

        }

        return savedImagePath;
    }


    //Static method that allows us to color and return a Bitmap using the resource and the color passed in as parameters
    public static Bitmap getColoredBitmapFromResource(Context context, int resource, int color) {

        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), resource);

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);

        return resultBitmap;
    }

}
