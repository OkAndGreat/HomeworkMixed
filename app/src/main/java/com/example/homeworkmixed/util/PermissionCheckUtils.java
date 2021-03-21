package com.example.homeworkmixed.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class PermissionCheckUtils {
    private static OnWantToOpenPermissionListener listener;

    /**
     * 检测Activity所需要的权限
     *  @param activity    当前Activity对象
     * @param permissions 当前Activity对象所需要的权限
     * @param requestCode 申请权限的请求码
     * @param fragment    当前fragment
     */
    public static int checkActivityPermissions(Activity activity, String[] permissions, int requestCode, Fragment fragment) {
        if (permissions == null || activity == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String permiss = permissions[i];
                int resultCode = ContextCompat.checkSelfPermission(activity, permiss);
                if (resultCode != PackageManager.PERMISSION_GRANTED) {
                    permissionsList.add(permiss);
                }
            }
            if (permissionsList.size() > 0) {
                requestActivityPermissions(activity, permissionsList, requestCode, fragment);
            }
            return permissionsList.size();
        } else {
            return 0;
        }
    }

    public static void requestActivityPermissions(Activity activity, List<String> permissions, int requestCode, Fragment fragment) {
        List<String> permissionList = new ArrayList<>();
        String[] strings = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            String str = permissions.get(i);
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
                permissionList.add(str);
            }
            strings[i] = str;
        }
        if (permissionList.size() > 0) {
            if (listener != null) {
                listener.onWantToOpenPermission();
            }
        } else {
            if (fragment == null) {
                ActivityCompat.requestPermissions(activity, strings, requestCode);
            } else {
                fragment.requestPermissions(strings, requestCode);
            }
        }
    }

    public interface OnWantToOpenPermissionListener {
        void onWantToOpenPermission();
    }

}