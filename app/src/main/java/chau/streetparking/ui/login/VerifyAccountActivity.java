package chau.streetparking.ui.login;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.*;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.ParseUser;

import org.json.JSONObject;

import chau.streetparking.R;
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

    private static final int AVATAR_SIZE = 200;

    private ImageView ivAvatar;
    private EditText etFirst;
    private EditText etLast;
    private EditText etEmail;
    private EditText etMobile;

    private User user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidgets();

        user = (User) ParseUser.getCurrentUser();
        if (user != null && user.isAuthenticated()) {
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAvatarClicked(View v) {

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
                        }
                    }
                });
        request.executeAsync();
    }

    private void setupName(JSONObject jsonObject) {
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

                    lastName = lastName.trim();
                    firstName = firstName.trim();

                    etFirst.setText(firstName);
                    etLast.setText(lastName);
                }
            }
        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
        }
    }

    private void setupEmail(JSONObject jsonObject) {
        try {
            if (jsonObject.has("email")) {
                etEmail.setText(jsonObject.getString("email"));
            }

        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
        }
    }

    private void setupAvatar(JSONObject jsonObject) {
        try {
            long id = jsonObject.getLong("id");
            String urlPath = "https://graph.facebook.com/" + id +
                    "/picture?width="+ AVATAR_SIZE + "&height=" + AVATAR_SIZE;

            ImageLoader.getInstance().loadImage(urlPath, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    if (failReason == null) {
                        Logger.d(TAG, "Facebook avatar loading failed");
                    } else {
                        Logger.d(TAG, "Facebook avatar error: " + failReason.getCause().getLocalizedMessage());
                    }
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (ivAvatar != null && bitmap != null) {
                        ivAvatar.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });

        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
        }
    }

    private void getWidgets() {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        etFirst = (EditText) findViewById(R.id.edit_text_first);
        etLast = (EditText) findViewById(R.id.edit_text_last);
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etMobile = (EditText) findViewById(R.id.edit_text_mobile);
    }

    private class GetInfoRunnable implements Runnable {
        private final JSONObject jsonObject;
        private ProgressDialog dialog;

        public GetInfoRunnable(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            if (jsonObject != null) {
                showDialog();
                setupName();
                dismissDialog();
            }
        }

        private void showDialog() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(VerifyAccountActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage("Loading...");
                    dialog.show();
                }
            });
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
    }
}
