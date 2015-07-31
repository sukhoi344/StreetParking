package chau.streetparking.backend;

import android.content.Context;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chau.streetparking.datamodels.UserProperties;
import chau.streetparking.datamodels.Users;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 7/26/2015.
 */
public class BackendTest {
    private static final String TAG = "BackendTest";
    private Context context;

    public BackendTest(Context context) {
        this.context = context;
    }

    public void testFind() {
        Logger.d(TAG, "testFind() begins");

        final String whereClause = UserProperties.EMAIL + " = 'DavidBeckham@gmail.com' "
                + " OR " + UserProperties.MOBILE + " = '2023301969'";
        final BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);

        Backendless.Persistence.of(Users.class).find(query, new AsyncCallback<BackendlessCollection<Users>>() {
            @Override
            public void handleResponse(BackendlessCollection<Users> response) {
                if (response == null) {
                    Log.d(TAG, "null response");
                } else {
                    List<Users> data = response.getCurrentPage();

                    if (data == null) {
                        Log.d(TAG, "data null");
                    } else {
                        Log.d(TAG, "data.size = " + data.size());

                        for (Users user : data) {
                            Log.d(TAG, user.toString());
                        }
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, fault.getMessage());
            }
        });
    }

    public void testEvent() {
        Backendless.Events.dispatch(EventManager.RequestToken.NAME, new HashMap(), new AsyncCallback<Map>() {
            @Override
            public void handleResponse(Map response) {
                if (response != null) {
                    String clientToken = (String) response.get(EventManager.RequestToken.KEY_TOKEN);
                    Logger.d(TAG, "clientToken: " + clientToken);
                } else {
                    Logger.d(TAG, "null response");
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.d(TAG, "error: " + fault.getMessage());
            }
        });
    }
}
