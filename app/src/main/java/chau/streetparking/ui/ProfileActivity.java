package chau.streetparking.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.soundcloud.android.crop.Crop;

import chau.streetparking.FileManager;
import chau.streetparking.R;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.login.TaskCropImage;
import chau.streetparking.util.FileUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 6/17/2015.
 */
public class ProfileActivity extends ColoredBarActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final String EXTRA_PROFILE = "extra_profile";
    public static final int PROFILE_NO_CHANGE = 1;
    public static final int PROFILE_LOG_OUT = 2;
    public static final int PROFILE_UPDATED = 3;

    private static final int REQUEST_CODE_AVATAR = 1;

    private EditText    etFirstName,
                        etLastName,
                        etMobile,
                        etEmail;
    private Button      btnSave;
    private ImageView   ivAvatar;

    private User        user;
    private ProgressDialog dialog;
    private boolean updated = false;

    @Override
    protected int getLayout() {
        return R.layout.profile_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "PROFILE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();
        setupUser();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PROFILE, updated? PROFILE_UPDATED : PROFILE_NO_CHANGE);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                setEditEnable(true);
                return true;
            case R.id.menu_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_AVATAR:
                    if (data != null && data.getData() != null) {
                        new TaskCropImage(this, data.getData()).execute();
                    }
                    break;

                case Crop.REQUEST_CROP:
                    if (data != null) {
                        Uri avatarUri = Crop.getOutput(data);

                        saveAvatar(avatarUri);
                    }
                    break;
            }
        }
    }

    private void saveAvatar(Uri avatarUri) {
        if (avatarUri != null) {
            ivAvatar.setImageURI(avatarUri);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            showProgressDialog("Saving...");
            final String mobile = user.getMobile();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        byte[] data = FileUtil.getBytesFromPath(getFilesDir() + "/" + FileManager.AVATAR_CROPPED_FILE_NAME);
                        final ParseFile file = new ParseFile(FileManager.getFileAvatarName(mobile), data);

                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(final ParseException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (e != null) {
                                            Logger.d(TAG, e.getLocalizedMessage());
                                        } else {
                                            user.setAvatar(file);
                                            user.saveInBackground();
                                            updated = true;
                                            Toast.makeText(ProfileActivity.this, "Avatar saved", Toast.LENGTH_SHORT).show();
                                        }

                                        if (dialog != null)
                                            dialog.dismiss();
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        if (Logger.DEBUG)
                            e.printStackTrace();
                        if (dialog != null)
                            dialog.dismiss();
                    }
                }
            }).start();
        }
    }

    /**
     * Save button clicked event callback
     */
    public void onSaveClicked(View v) {
        String email = etEmail.getText().toString();
        String first = etFirstName.getText().toString();
        String last = etLastName.getText().toString();
        String mobile = etMobile.getText().toString();

        if (checkInput(email, first, last, mobile)) {
            showProgressDialog("Saving");

            user.setEmail(email);
            user.setFirstName(first);
            user.setLastName(last);
            user.setMobile(mobile);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ProfileActivity.this, "Your profile has been updated", Toast.LENGTH_SHORT).show();
                        setEditEnable(false);
                        updated = true;
                    } else {
                        Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (dialog != null)
                        dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAvatarClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_AVATAR);
    }

    private void setEditEnable(boolean enable) {
        etEmail.setEnabled(enable);
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        etMobile.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        ivAvatar.setClickable(enable);
    }

    private void setupUser() {
        user = (User) ParseUser.getCurrentUser();


        if (user != null) {
            etEmail.setText(user.getEmail());
            etFirstName.setText(user.getFirstName());
            etLastName.setText(user.getLastName());
            etMobile.setText(user.getMobile());

            if (user.getAvatar() != null) {
                ImageLoader.getInstance().displayImage(user.getAvatar().getUrl(), ivAvatar,
                        new DisplayImageOptions.Builder().cacheInMemory(true).build());
            }
        }
    }

    private void signOut() {
        showProgressDialog("Logging out...");

        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (dialog != null)
                    dialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra(EXTRA_PROFILE, PROFILE_LOG_OUT);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean checkInput(String email, String first, String last, String mobile) {
        if (email == null || first == null || last == null || mobile == null)
            return false;
        if (email.isEmpty() || first.isEmpty() || last.isEmpty() || mobile.isEmpty())
            return false;

        return true;
    }

    private void getWidgets() {
        etEmail = (EditText) findViewById(R.id.email);
        etFirstName = (EditText) findViewById(R.id.first_name);
        etLastName = (EditText) findViewById(R.id.last_name);
        etMobile = (EditText) findViewById(R.id.mobile);
        btnSave = (Button) findViewById(R.id.save);
        ivAvatar = (ImageView) findViewById(R.id.avatar);
    }

    private void showProgressDialog(String message) {
        dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);

        dialog.show();
    }
}
