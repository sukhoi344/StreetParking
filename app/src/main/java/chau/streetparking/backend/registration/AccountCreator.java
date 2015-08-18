package chau.streetparking.backend.registration;

import android.app.ProgressDialog;
import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.stripe.android.model.Card;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import chau.streetparking.FileManager;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.util.FileUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/10/15.
 */
public class AccountCreator {
    private static final String TAG = AccountCreator.class.getSimpleName();

    private Context context;
    private String email;
    private String mobile;
    private String password;
    private String firstName;
    private String lastName;
    private boolean avatarSelected;
    private boolean isFacebook;

    private ResultCallBack resultCallBack;
    private StripeCustomerCreator stripeCustomerCreator;

    private boolean isWorking = false;
    private ProgressDialog dialog;

    public AccountCreator(
            Context context,
            Card card,
            String email,
            String mobile,
            String password,
            String firstName,
            String lastName,
            boolean avatarSelected,
            boolean isFacebook,
            final ResultCallBack resultCallBack)
    {
        this.context = context;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarSelected = avatarSelected;
        this.resultCallBack = resultCallBack;
        this.isFacebook = isFacebook;

        stripeCustomerCreator = new StripeCustomerCreator(context, card, email, new ResultCallBack() {
            @Override
            public void success(String customerId) {
                saveAccount(customerId);
            }

            @Override
            public void failure(String errorMessage) {
                resultCallBack.failure(errorMessage);
            }
        });
    }

    public void saveAccount() {
        if (!isWorking) {
            isWorking = true;
            showProgressDialog();
            stripeCustomerCreator.createCustomer();
        }
    }

    private void saveAccount(final String customerId) {
        final User user;

        if (isFacebook) {
            user = (User) ParseUser.getCurrentUser();
        } else {
            user = new User();
            user.setPassword(password);
        }

        user.setUsername(email);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobileVerified(Boolean.FALSE);

        if (avatarSelected) {
            byte[] data = FileUtil.getBytesFromPath(context.getFilesDir().getPath() + "/" +
                    FileManager.AVATAR_CROPPED_FILE_NAME);
            final ParseFile file = new ParseFile(FileManager.getFileAvatarName(mobile), data);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        resultCallBack.failure(e.getLocalizedMessage());
                        onFinished();
                    } else {
                        saveAccount(customerId, user, file, true);
                    }
                }
            });
        } else {
            saveAccount(customerId, user, null, false);
        }
    }

    private void saveAccount(String customerId, final User user, final ParseFile file, final boolean uploaded) {
        final ParseObject customer = new ParseObject("Credit");
        customer.put("customerId", customerId);
        customer.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    resultCallBack.failure(e.getLocalizedMessage());
                    onFinished();
                } else {
                    List<ParseObject> list = new ArrayList<>();
                    list.add(customer);
                    user.put("creditCard", list);

                    if (uploaded && file != null) {
                        user.put("avatar", file);
                    }

                    if (isFacebook) {
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                saveCallBack(e, customer, user);
                            }
                        });
                    } else {
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                saveCallBack(e, customer, user);
                            }
                        });
                    }
                }
            }
        });
    }

    private void saveCallBack(ParseException e, ParseObject customer, User user) {
        if (e == null) {
            customer.put("user", user);
            customer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        Logger.d(TAG, "customer pointer saved");
                    else
                        Logger.d(TAG, "customer pointer error: " + e.getLocalizedMessage());
                }
            });

            resultCallBack.success(user.getObjectId());
        } else {
            resultCallBack.failure(e.getLocalizedMessage());
        }

        onFinished();
    }

    private void onFinished() {
        isWorking = false;
        if (dialog != null)
            dialog.dismiss();
    }

    private void showProgressDialog() {
        if (context != null) {
            dialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Creating account...");

            dialog.show();
        }
    }
}
