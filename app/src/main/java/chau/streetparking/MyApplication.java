package chau.streetparking;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import chau.streetparking.datamodels.parse.User;

/**
 * Created by Chau Thai on 7/4/2015.
 */
public class MyApplication extends Application {
    /** For facebook login */
    public static final int REQUEST_CODE_OFFSET = 300;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        initDrawer();
        initParse();
        initFacebook();
    }

    private void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void initDrawer() {
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(final ImageView imageView, Uri uri, final Drawable drawable) {
                ImageLoader.getInstance().loadImage(uri.toString(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        imageView.setImageDrawable(drawable);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }

            @Override
            public void cancel(ImageView imageView) {

            }

            @Override
            public Drawable placeholder(Context context) {
                return null;
            }
        });
    }

    private void initParse() {
        ParseObject.registerSubclass(User.class);
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(this, REQUEST_CODE_OFFSET);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }


}
