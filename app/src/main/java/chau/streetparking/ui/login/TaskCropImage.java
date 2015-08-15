package chau.streetparking.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import chau.streetparking.FileManager;
import chau.streetparking.util.ImageUtil;

/**
 * Created by Chau Thai on 8/15/2015.
 */
public class TaskCropImage extends AsyncTask<Void, Void, Uri> {
    private static final int MAX_IMAGE_DIMENSION = 500;
    private ProgressDialog progressDialog;
    private Uri photoUri;
    private Activity activity;

    public TaskCropImage(Activity activity, Uri photoUri) {
        this.photoUri = photoUri;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Uri doInBackground(Void... params) {
        try {
            int orientation = ImageUtil.getOrientation(activity, photoUri);
            if (orientation == 0) {
                return photoUri;
            }

            Bitmap rotatedBitmap = ImageUtil.getCorrectlyOrientedImage(activity, photoUri, MAX_IMAGE_DIMENSION);

            if (rotatedBitmap != null) {
                boolean saved = ImageUtil.saveBitmap(activity, rotatedBitmap,
                        FileManager.AVATAR_UNCROPPED_FILE_NAME, Bitmap.CompressFormat.PNG, 100);

                if (saved) {
                    String filePath = activity.getFilesDir() + "/" + FileManager.AVATAR_UNCROPPED_FILE_NAME;
                    return Uri.fromFile(new File(filePath));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        try {
            progressDialog.dismiss();

            if (uri != null) {
                Crop.of(uri, Uri.fromFile(new File(activity.getFilesDir() + "/" +
                        FileManager.AVATAR_CROPPED_FILE_NAME))).asSquare()
                        .start(activity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
