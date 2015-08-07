package chau.streetparking.backend.registration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.util.Logger;


/**
 * Check for valid mobile and email
 * Created by Chau Thai on 7/27/15.
 */
public class IdentityVerifier {
    private static final String TAG = IdentityVerifier.class.getSimpleName();

    public static final int RESULT_PASS = 0;
    public static final int RESULT_DUPLICATE_EMAIL = 1;
    public static final int RESULT_DUPLICATE_MOBILE = 2;
    public static final int RESULT_DUPLICATE_BOTH = 3;

    private Activity activity;
    private ResultCallback resultCallback;

    public interface ResultCallback {
        void handleResult(int result);
        void handleFault(ParseException e);
    }

    public IdentityVerifier(Activity activity, ResultCallback resultCallback) {
        this.activity = activity;
        this.resultCallback = resultCallback;
    }

    /**
     * Async call to check for valid (not duplicated) email and mobile
     * @param email new email to be registered
     * @param mobile new mobile to be registered
     */
    public void verify(final String email, final String mobile) {
        if (email != null && mobile != null) {
            final Dialog dialog = showProgressDialog();

            getQuery(email, mobile).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if (dialog != null)
                        dialog.dismiss();

                    if (resultCallback != null) {
                        handleResult(list, e, email, mobile);
                    }
                }
            });
        }
    }

    private void handleResult(List<ParseUser> list, ParseException e, String email, String mobile) {
        if (e == null) {
            if (list != null) {
                // found duplicate
                boolean hasMobile = false;
                boolean hasEmail = false;

                for (ParseUser parseUser : list) {
                    if (parseUser.getEmail().equalsIgnoreCase(email))
                        hasEmail = true;
                    if (parseUser.getString("mobile").equals(mobile))
                        hasMobile = true;
                }

                if (hasEmail && hasMobile)
                    resultCallback.handleResult(RESULT_DUPLICATE_BOTH);
                else if (hasEmail)
                    resultCallback.handleResult(RESULT_DUPLICATE_EMAIL);
                else if (hasMobile)
                    resultCallback.handleResult(RESULT_DUPLICATE_MOBILE);
                else
                    resultCallback.handleResult(RESULT_PASS);
            } else {
                // success
                resultCallback.handleResult(RESULT_PASS);
            }

        } else {
            resultCallback.handleFault(e);
        }
    }

    private ParseQuery<ParseUser> getQuery(String email, String mobile) {
        ParseQuery<ParseUser> queryName = ParseUser.getQuery();
        queryName.whereEqualTo("username", email.toLowerCase());

        ParseQuery<ParseUser> queryMobile = ParseUser.getQuery();
        queryMobile.whereEqualTo("mobile", mobile);

        List<ParseQuery<ParseUser>> queryList = new ArrayList<>();
        queryList.add(queryName);
        queryList.add(queryMobile);

        return ParseQuery.or(queryList);
    }

    private Dialog showProgressDialog() {
        if (activity != null) {
            ProgressDialog dialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("VERIFYING");

            dialog.show();

            return dialog;
        }

        return null;
    }
}
