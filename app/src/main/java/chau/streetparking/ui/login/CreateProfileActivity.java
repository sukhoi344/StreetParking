package chau.streetparking.ui.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.soundcloud.android.crop.Crop;

import java.io.File;

import chau.streetparking.FileManager;
import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.ImageUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 7/27/15.
 */
public class CreateProfileActivity extends ColoredBarActivity {
    public static final String EXTRA_MOBILE = "extra_mobile";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_PASSWORD = "extra_pass";

    private static final String TAG = "CreateProfileActivity";
    private static final int MAX_IMAGE_DIMENSION = 500;
    private static final int AVATAR_REQUEST_CODE = 1;

    private EditText    editTextFirst,
                        editTextLast;
    private ImageView   ivAvatar;

    private String  mobile,
                    email,
                    password;
    private boolean avatarSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        getWidgets();
    }

    @Override
    protected int getLayout() {
        return R.layout.create_profile_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "CREATE A PROFILE";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next:
                if (checkName()) {

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case AVATAR_REQUEST_CODE:
                    if (data.getData() != null) {
                        new TaskCropImage(this, data.getData()).execute();
                    }

                    break;

                case Crop.REQUEST_CROP:
                    Uri avatarUri = Crop.getOutput(data);

                    if (avatarUri != null) {
                        ivAvatar.setImageURI(avatarUri);
                        avatarSelected = true;
                    }

                    break;
            }
        }
    }

    public void onAvatarClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, AVATAR_REQUEST_CODE);
    }

    private boolean checkName() {
        String first = editTextFirst.getText().toString();
        String last = editTextLast.getText().toString();

        if (first.isEmpty() || last.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getExtras() {
        if (getIntent() != null) {
            mobile = getIntent().getStringExtra(EXTRA_MOBILE);
            email = getIntent().getStringExtra(EXTRA_EMAIL);
            password = getIntent().getStringExtra(EXTRA_PASSWORD);
        }
    }

    private void getWidgets() {
        editTextFirst = (EditText) findViewById(R.id.edit_text_first);
        editTextLast = (EditText) findViewById(R.id.edit_text_last);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
    }

    private class TaskCropImage extends AsyncTask<Void, Void, Uri> {
        private ProgressDialog progressDialog;
        private Uri photoUri;
        private Context context;

        public TaskCropImage(Context context, Uri photoUri) {
            this.photoUri = photoUri;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
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
                int orientation = ImageUtil.getOrientation(context, photoUri);
                if (orientation == 0) {
                    return photoUri;
                }

                Bitmap rotatedBitmap = ImageUtil.getCorrectlyOrientedImage(context, photoUri, MAX_IMAGE_DIMENSION);

                if (rotatedBitmap != null) {
                    boolean saved = ImageUtil.saveBitmap(context, rotatedBitmap,
                            FileManager.AVATAR_UNCROPPED_FILE_NAME, Bitmap.CompressFormat.PNG, 100);

                    if (saved) {
                        String filePath = context.getFilesDir() + "/" + FileManager.AVATAR_UNCROPPED_FILE_NAME;
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
                    Crop.of(uri, Uri.fromFile(new File(getFilesDir() + "/" +
                            FileManager.AVATAR_CROPPED_FILE_NAME))).asSquare().start(CreateProfileActivity.this);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
