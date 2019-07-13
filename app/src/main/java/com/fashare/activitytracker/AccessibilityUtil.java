package com.fashare.activitytracker;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jinliangshan on 16/12/26.
 */
public class AccessibilityUtil {

    /**
     * 判断判断辅助功能是否开启
     */
    public static boolean checkAccessibility(Context context) {
        // 判断辅助功能是否开启
        if (!AccessibilityUtil.isAccessibilitySettingsOn(context)) {
            // 引导至辅助功能设置页面
            try {
                context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                Log.e("AccessibilityUtil","引导至辅助功能设置页面 出错了",e);
            }
            Toast.makeText(context, "请先开启 \"Activity 栈\" 的辅助功能", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }
}
