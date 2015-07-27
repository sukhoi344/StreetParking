package chau.streetparking.ui.login;

import android.content.Intent;
import android.net.Uri;
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

/**
 * Created by Chau Thai on 7/27/15.
 */
public class CreateProfileActivity extends ColoredBarActivity {
    public static final String EXTRA_MOBILE = "extra_mobile";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_PASSWORD = "extra_pass";

    private EditText    editTextFirst,
                        editTextLast;
    private ImageView   ivAvatar;

    private String  mobile,
                    email,
                    password;

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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Crop.REQUEST_PICK:
                    Crop.of(data.getData(), Uri.fromFile(new File(getFilesDir() + "/" +
                            FileManager.AVATAR_CROPPED_FILE_NAME))).asSquare().start(this);
                    break;

                case Crop.REQUEST_CROP:
                    Uri avatarUri = Crop.getOutput(data);

                    if (avatarUri != null) {
                        ivAvatar.setImageURI(avatarUri);
                    }
                    break;
            }
        }
    }

    public void onAvatarClicked(View v) {
        Crop.pickImage(this);
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
