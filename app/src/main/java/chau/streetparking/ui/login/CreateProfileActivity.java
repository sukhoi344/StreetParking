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
    public static final int REQUEST_EXIT = 2;

    public static final String EXTRA_MOBILE = "extra_mobile";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_PASSWORD = "extra_pass";

    private static final String TAG = "CreateProfileActivity";
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
                    Intent intent = new Intent(this, LinkPaymentActivity.class);

                    intent.putExtra(LinkPaymentActivity.EXTRA_EMAIL, email);
                    intent.putExtra(LinkPaymentActivity.EXTRA_MOBILE, mobile);
                    intent.putExtra(LinkPaymentActivity.EXTRA_PASS, password);
                    intent.putExtra(LinkPaymentActivity.EXTRA_FIRST, editTextFirst.getText().toString());
                    intent.putExtra(LinkPaymentActivity.EXTRA_LAST, editTextLast.getText().toString());
                    intent.putExtra(LinkPaymentActivity.EXTRA_AVATAR_SELECTED, avatarSelected);

                    startActivityForResult(intent, REQUEST_EXIT);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AVATAR_REQUEST_CODE:
                    if (data != null && data.getData() != null) {
                        new TaskCropImage(this, data.getData()).execute();
                    }
                    break;

                case Crop.REQUEST_CROP:
                    if (data != null) {
                        Uri avatarUri = Crop.getOutput(data);

                        if (avatarUri != null) {
                            ivAvatar.setImageURI(avatarUri);
                            avatarSelected = true;
                        }
                    }
                    break;

                case REQUEST_EXIT:
                    setResult(RESULT_OK);
                    finish();
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
}
