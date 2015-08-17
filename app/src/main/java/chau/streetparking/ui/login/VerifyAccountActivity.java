package chau.streetparking.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chau.streetparking.FileManager;
import chau.streetparking.R;
import chau.streetparking.backend.registration.IdentityVerifier;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.ui.ColoredBarActivity;
import chau.streetparking.util.FileUtil;
import chau.streetparking.util.ImageUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/13/15.
 */
public class VerifyAccountActivity extends ColoredBarActivity {
    private static final String TAG  = VerifyAccountActivity.class.getSimpleName();
    private static final int AVATAR_REQUEST_CODE = 1;
    private static final int REQUEST_EXIT = 2;
    private static final int AVATAR_SIZE = 200;

    private ImageView ivAvatar;
    private EditText etFirst;
    private EditText etLast;
    private EditText etEmail;
    private EditText etMobile;

    private User user;
    private boolean avatarSelected = false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();

        user = (User) ParseUser.getCurrentUser();
        if (user != null && user.isAuthenticated()) {
            showDialog();
            makeRequest();
        } else {
            Logger.d(TAG, "Cannot make request");
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.verify_account_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "VERIFY ACCOUNT";
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
                onNextClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void onNextClicked() {
        final String first = etFirst.getText().toString();
        final String last = etLast.getText().toString();
        final String email = etEmail.getText().toString();
        final String mobile = etMobile.getText().toString();

        if (checkInput(first, last, email, mobile)) {
            IdentityVerifier identityVerifier = new IdentityVerifier(this,
                    new IdentityVerifier.ResultCallback() {
                @Override
                public void handleResult(int result) {
                    switch (result) {
                        case IdentityVerifier.RESULT_DUPLICATE_BOTH:
                            Toast.makeText(VerifyAccountActivity.this, "Duplicate email and phone",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case IdentityVerifier.RESULT_DUPLICATE_EMAIL:
                            Toast.makeText(VerifyAccountActivity.this, "Duplicate email",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case IdentityVerifier.RESULT_DUPLICATE_MOBILE:
                            Toast.makeText(VerifyAccountActivity.this, "Duplicate mobile",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case IdentityVerifier.RESULT_PASS:
                            goToLinkPayment(first, last, email, mobile);
                            break;
                    }
                }

                @Override
                public void handleFault(ParseException e) {
                    Toast.makeText(VerifyAccountActivity.this, "Cannot verify your account",
                            Toast.LENGTH_SHORT).show();
                }
            });
            identityVerifier.verify(email, mobile);
        }
    }

    private void goToLinkPayment(String first, String last, String email, String mobile) {
        Intent intent = new Intent(this, LinkPaymentActivity.class);
        intent.putExtra(LinkPaymentActivity.EXTRA_AVATAR_SELECTED, avatarSelected);
        intent.putExtra(LinkPaymentActivity.EXTRA_EMAIL, email);
        intent.putExtra(LinkPaymentActivity.EXTRA_FIRST, first);
        intent.putExtra(LinkPaymentActivity.EXTRA_LAST, last);
        intent.putExtra(LinkPaymentActivity.EXTRA_MOBILE, mobile);
        intent.putExtra(LinkPaymentActivity.EXTRA_IS_FACEBOOK, true);

        startActivityForResult(intent, REQUEST_EXIT);
    }

    public void onAvatarClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, AVATAR_REQUEST_CODE);
    }

    private void makeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            Logger.d(TAG, jsonObject.toString());
                            new Thread(new GetInfoRunnable(jsonObject)).start();
                        } else {
                            if (graphResponse != null && graphResponse.getError() != null) {
                                Logger.d(TAG, "GraphRequest error: " + graphResponse.getError());
                            } else {
                                Logger.d(TAG, "null jsonObject");
                            }

                            if (dialog != null)
                                dialog.dismiss();
                        }
                    }
                });
        request.executeAsync();
    }

    private boolean checkInput(String first, String last, String email, String mobile) {
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getWidgets() {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        etFirst = (EditText) findViewById(R.id.edit_text_first);
        etLast = (EditText) findViewById(R.id.edit_text_last);
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etMobile = (EditText) findViewById(R.id.edit_text_mobile);
    }

    private void showDialog() {
        dialog = new ProgressDialog(VerifyAccountActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    private class GetInfoRunnable implements Runnable {
        private final JSONObject jsonObject;

        public GetInfoRunnable(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            if (jsonObject != null) {
                setupName();
                setupAvatar();
                dismissDialog();
            }
        }

        private void dismissDialog() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });
        }

        private void setupName() {
            try {
                if (jsonObject.has("name")) {
                    String name = jsonObject.getString("name");
                    String split[] = name.split(" ");

                    if (split.length == 1) {
                        etLast.setText(split[0]);
                    } else {
                        String firstName = "";
                        String lastName = split[split.length - 1];

                        for (int i = 0; i < split.length - 1; i++)
                            firstName += split[i] + " ";

                        final String lastNameFinal = lastName.trim();
                        final String firstNameFinal = firstName.trim();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (etFirst != null && etLast != null) {
                                    etFirst.setText(firstNameFinal);
                                    etLast.setText(lastNameFinal);
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                if (Logger.DEBUG)
                    e.printStackTrace();
            }
        }

        private void setupAvatar() {
            OutputStream os = null;

            try {
                long id = jsonObject.getLong("id");
                String urlPath = "https://graph.facebook.com/" + id +
                        "/picture?width="+ AVATAR_SIZE + "&height=" + AVATAR_SIZE;
                final Bitmap bitmap = getBitmapFromURL(urlPath);

                if (bitmap != null) {
                    File file = new File(getFilesDir() + "/" + FileManager.AVATAR_CROPPED_FILE_NAME);
                    os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivAvatar.setImageBitmap(bitmap);
                            avatarSelected = true;
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
