package siesgst.edu.in.tml16.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vishal on 30/12/15.
 */
public class ConnectionUtils {

    Context context;

    public ConnectionUtils(Context context) {
        this.context = context;
    }

    public boolean checkConnection() {
        final ConnectivityManager ComMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo nwInfo = ComMgr.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnected())
            return true;
        else
            return false;
    }
}
