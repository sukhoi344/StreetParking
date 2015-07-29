package chau.streetparking.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Chau Thai on 12/19/14.
 */
public class DeviceUtil {

    /** Check if has internet connection */
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /** Checks if external storage is available for read and write   */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /** Checks if external storage is available to at least read  */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /** Get unique deviceId  */
    public static String getDeviceId(final Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = telephonyManager.getDeviceId();

        if (id == null || id.isEmpty())
            id = telephonyManager.getSubscriberId();

        if (id == null || id.isEmpty())
            id = telephonyManager.getSimSerialNumber();

        if (id == null || id.isEmpty()) {
            WifiManager m_wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (m_wm != null)
                id = m_wm.getConnectionInfo().getMacAddress();
        }

        if (id == null || id.isEmpty())
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (id == null || id.isEmpty())
            id = telephonyManager.getDeviceId();

        return id;
    }

    /** Get the Wifi Mac address. If wifi is not available, use device id instead */
    public static String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = getDeviceId(context);
        }
        return macAddress;
    }

    /** Get SDK version */
    public static String getPlatformVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /** Get app version. If fail, return "1.0" */
    public static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1.0";
    }

    /** Unlock and wake the screen up  */
    public static void unlockScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /** Vibrate the device  */
    public static void vibrate(Context context, long milisecs) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(milisecs);
    }

    /** Play notification sound */
    public static void playNotification(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(notification != null) {
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }
    }
}