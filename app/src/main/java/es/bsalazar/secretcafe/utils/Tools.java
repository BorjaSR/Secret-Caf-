package es.bsalazar.secretcafe.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Tools {


    @TargetApi(Build.VERSION_CODES.DONUT)
    public static byte[] getByteArrayFromImageView(ImageView imageView) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = null;

        try {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
            if (bitmapDrawable != null)
                bitmap = bitmapDrawable.getBitmap();

        } catch (Exception e) {
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            imageView.buildDrawingCache(false);
        }

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        } else
            return null;
    }
}
