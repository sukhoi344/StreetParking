package chau.streetparking.backend;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

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


    public void testParse() {
        HashMap<String, Object> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("hello2", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                Log.d(TAG, "Parse response: " + o.toString());
            }
        });


//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

    }

}
