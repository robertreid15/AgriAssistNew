package com.robertreid.farm.system.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHelper {

    public static boolean isNetworkAvailable(Context context) {
        //We create a connectivity manager that control networks in android apps
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //We check if manager is not null(networks EXISTS and phone has NETWORK function)
        if (connectivityManager != null) {
            //We get information about all networks and check if we connected although to one of them
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            //return true is connected and false either
            return (networkInfo!=null && networkInfo.isConnected());
        } else {
            //return false is Connectivity manager doesn't exist
            return false;
        }
    }
}