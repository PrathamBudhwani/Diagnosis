package com.prathambudhwani.diagnosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RootChecker {
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su" };

        for (String path : paths) {
            if (new File(path).exists()) return true;
        }

        return false;
    }

    private static boolean checkRootMethod3() {
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return br.readLine() != null;
            }
        } catch (IOException e) {
            // Ignore exceptions
        }
        return false;
    }
}
