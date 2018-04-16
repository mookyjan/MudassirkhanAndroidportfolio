package com.mudassirkhan.androidportfolio.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;

public class PermissionsUtils {

    //Static method used to request the permission passed in as a parameter
    public static void requestPermission(Activity activity, String permission, int requestId) {
        //The permission will only be requested if the user's device has an Android version superior or equal to 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{permission}, requestId);
        }
    }


    //Static method used to determine whether a user has the permission passed in as a parameter
    public static boolean checkIfHasPermission(Context context, String permissionType) {
        //The permission check will only occur for devices above API 23, since it was automatically granted before
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permissionType);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


    //Static method used to display a "Permission granted" toast from the context passed in as a parameter
    public static void showPermissionGrantedToast(Context context) {
        Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show();
    }


    //Static method used to display a "Permission denied" toast from the context passed in as a parameter
    public static void showPermissionDeniedToast(Context context) {
        Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show();
    }

}


