package chau.streetparking.backend.registration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;


import java.util.List;

import chau.streetparking.datamodels.UserProperties;
import chau.streetparking.datamodels.Users;

/**
 * Check for valid mobile and email
 * Created by Chau Thai on 7/27/15.
 */
public class IdentityVerifier {
    public static final int RESULT_PASS = 0;
    public static final int RESULT_DUPLICATE_EMAIL = 1;
    public static final int RESULT_DUPLICATE_MOBILE = 2;
    public static final int RESULT_DUPLICATE_BOTH = 3;

    private static final String whereClauseTemplate = UserProperties.EMAIL + " = '?'"
            + " OR " + UserProperties.MOBILE + " = '?'";

    private Activity activity;
    private ResultCallback resultCallback;

    public interface ResultCallback {
//        void handleResult(int result);
//        void handleFault(BackendlessFault fault);
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
//        if (email != null && mobile != null) {
//            final Dialog dialog = showProgressDialog();
//            final BackendlessDataQuery query = getQuery(email, mobile);
//
//            Backendless.Persistence.of(Users.class).find(query, new AsyncCallback<BackendlessCollection<Users>>() {
//                @Override
//                public void handleResponse(BackendlessCollection<Users> response) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//
//                    if (resultCallback == null)
//                        return;
//
//                    if (response != null && response.getCurrentPage() != null && response.getCurrentPage().size() > 0) {
//                        boolean hasMobile = false;
//                        boolean hasEmail = false;
//
//                        List<Users> usersList = response.getCurrentPage();
//
//                        for (Users user : usersList) {
//                            if (email.equalsIgnoreCase(user.getEmail()))
//                                hasEmail = true;
//                            if (mobile.equals(user.getMobile()))
//                                hasMobile = true;
//                        }
//
//                        if (hasMobile && hasEmail)
//                            resultCallback.handleResult(RESULT_DUPLICATE_BOTH);
//                        else if (hasMobile)
//                            resultCallback.handleResult(RESULT_DUPLICATE_MOBILE);
//                        else if (hasEmail)
//                            resultCallback.handleResult(RESULT_DUPLICATE_EMAIL);
//                        else
//                            resultCallback.handleResult(RESULT_PASS);
//
//                    } else {
//                        resultCallback.handleResult(RESULT_PASS);
//                    }
//
//                }
//
//                @Override
//                public void handleFault(BackendlessFault fault) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//
//                    if (resultCallback != null)
//                        resultCallback.handleFault(fault);
//                }
//            });

//        }
    }

//    private BackendlessDataQuery getQuery(String email, String mobile) {
//        String whereClause = whereClauseTemplate.replaceFirst("\\?", email.toLowerCase());
//        whereClause = whereClause.replaceFirst("\\?", mobile);
//        return new BackendlessDataQuery(whereClause);
//    }

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
