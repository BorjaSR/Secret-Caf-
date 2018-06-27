package es.bsalazar.secretcafe.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutionException;

public class GetImageFromStorageReference extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;
    private StorageReference storedReference;
    private long updateDate;
    private OnImageDownloadListener onFinishListener;

    public GetImageFromStorageReference(Context context, StorageReference storedReference, long updateDate, OnImageDownloadListener onFinishListener) {
        this.mContext = context;
        this.storedReference = storedReference;
        this.updateDate = updateDate;
        this.onFinishListener = onFinishListener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        try {
            return Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storedReference)
                    .asBitmap()
                    .signature(new MediaStoreSignature("", updateDate, 0))
                    .into(300, 300) // Width and height
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (onFinishListener != null) onFinishListener.onFinish(bitmap);
    }

    public interface OnImageDownloadListener {
        void onFinish(Bitmap bitmap);
    }
}
