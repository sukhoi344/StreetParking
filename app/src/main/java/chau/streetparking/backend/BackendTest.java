package chau.streetparking.backend;

import android.content.Context;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

import chau.streetparking.datamodels.UserProperties;
import chau.streetparking.datamodels.Users;

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
        Log.d(TAG, "Test begins");

        final String whereClause = UserProperties.EMAIL + " = 'DavidBeckham@gmail.com' "
                + " OR " + UserProperties.MOBILE + " = 2023301969";
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

}
