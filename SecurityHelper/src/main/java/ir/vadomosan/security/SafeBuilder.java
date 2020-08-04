package ir.vadomosan.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.vadomosan.security.emulator.Emulator;
import ir.vadomosan.security.exception.SecurityHelperException;
import ir.vadomosan.security.root.Root;


public class SafeBuilder {

    private Context context;
    private String SHA;
    private boolean CheckRoot = true;
    private boolean CheckEmulator = true;
    private boolean CheckSignature = true;
    private boolean CheckDebuggable = true;
    private boolean CheckHookDetected = true;
    private boolean CheckVPN = true;

    private boolean CheckRoot_;
    private boolean CheckEmulator_;
    private boolean CheckSignature_;
    private boolean CheckDebuggable_;
    private boolean CheckHookDetected_;
    private boolean CheckVPN_;

    public SafeBuilder(Context context) {
        this.context = context;
    }

    public SafeBuilder checkRoot(boolean Do_you_want_to_be_checked) {
        CheckRoot_ = Do_you_want_to_be_checked;
        return this;
    }

    public SafeBuilder checkEmulator(boolean Do_you_want_to_be_checked) {
        CheckEmulator_ = Do_you_want_to_be_checked;
        return this;
    }

    public SafeBuilder checkSignature(String SHA1, boolean Do_you_want_to_be_checked) {
        SHA = SHA1;
        CheckSignature_ = Do_you_want_to_be_checked;
        return this;
    }

    public SafeBuilder checkDebuggable(boolean Do_you_want_to_be_checked) {
        CheckDebuggable_ = Do_you_want_to_be_checked;
        return this;
    }

    public SafeBuilder checkHookDetected(boolean Do_you_want_to_be_checked) {
        CheckHookDetected_ = Do_you_want_to_be_checked;
        return this;
    }

    public SafeBuilder checkVPN(boolean Do_you_want_to_be_checked) {
        CheckVPN_ = Do_you_want_to_be_checked;
        return this;
    }

    public Safe check() throws SecurityHelperException {

        if (CheckRoot_) {
            if (isRooted(context)) {
                CheckRoot = false;
                //throw new SecurityHelperException("The device is root!");
            } else {
                CheckRoot = true;
            }
        }

        if (CheckEmulator_) {
            isEmulator(context, isEmulator -> {
                if (isEmulator) {
                    CheckEmulator = false;
/*                    try {
                        throw new SecurityHelperException("The device is a simulator!");
                    } catch (SecurityHelperException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    CheckEmulator = true;
                }
            });
        }

        if (CheckSignature_) {
            if (isSignatureValid(context, SHA)) {
                CheckSignature = true;
                //throw new SecurityHelperException("The signature is not valid!");
            } else {
                CheckSignature = false;
            }
        }

        if (CheckDebuggable_) {
            if (isDebuggable(context)) {
                CheckDebuggable = false;
                //throw new SecurityHelperException("app is debuggable!");
            } else {
                CheckDebuggable = true;
            }
        }

        if (CheckHookDetected_) {
            if (isHookDetected(context)) {
                CheckHookDetected = false;
                //throw new SecurityHelperException("exposed or similar framework detected in your app!");
            } else {
                CheckHookDetected = true;
            }
        }

        if (CheckVPN_) {
            if (isVPN(context)) {
                CheckVPN = false;
            } else {
                CheckVPN = true;
            }
        }

        return new Safe(CheckRoot, CheckEmulator, CheckSignature, CheckDebuggable, CheckHookDetected, CheckVPN);

    }

    private boolean isRooted(Context context) {
        try {
            Root root = new Root(context);
            return root.isRootedWithBusyBoxCheck();
        } catch (Exception e) {
            printLog(e.toString());
        }

        return false;
    }

    private void isEmulator(Context context, Emulator.OnEmulatorDetectorListener onEmulatorDetectorListener) {
        try {
            Emulator.Companion.with(context).detect(onEmulatorDetectorListener);
        } catch (Exception e) {
            printLog(e.toString());
        }
    }

    private boolean isSignatureValid(Context context, String releasesign) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                String sha1 = getSHA1(signature.toByteArray());

                if (releasesign.equals(sha1))
                    return true;
            }
        } catch (Exception e) {
            printLog(e.toString());
        }
        return false;
    }

    private String getSHA1(byte[] sig) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(sig);
        byte[] hashtext = digest.digest();
        return bytesToHex(hashtext);
    }

    private String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private boolean isDebuggable(Context context) {
        try {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            printLog(e.toString());
        }
        try {
            return Debug.isDebuggerConnected();
        } catch (Exception e) {
            printLog(e.toString());
        }
        return false;
    }

    private boolean isHookDetected(Context context) {
        try {

            try {
                //check for xposed and substrate apps
                PackageManager packageManager = context.getPackageManager();
                List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    if (applicationInfo.packageName.equals("de.robv.android.xposed.installer")) {
                        return true;
                    }
                    if (applicationInfo.packageName.equals("com.saurik.substrate")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                printLog(e.toString());
            }

            try {
                //check for xposed stacktrace
                try {
                    throw new Exception("blah");
                } catch (Exception e) {
                    int zygoteInitCallCount = 0;
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        if (stackTraceElement.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                            zygoteInitCallCount++;
                            if (zygoteInitCallCount == 2) {
                                return true;
                            }
                        }
                        if (stackTraceElement.getClassName().equals("com.saurik.substrate.MS$2") &&
                                stackTraceElement.getMethodName().equals("invoked")) {
                            return true;
                        }
                        if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                                stackTraceElement.getMethodName().equals("main")) {
                            return true;
                        }
                        if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                                stackTraceElement.getMethodName().equals("handleHookedMethod")) {
                            return true;
                        }

                    }
                }
            } catch (Exception e) {
                printLog(e.toString());
            }

            Set<String> libraries = new HashSet<>();
            String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";

            try (BufferedReader reader = new BufferedReader(new FileReader(mapsFilename))) {
                //check /proc/[pid]/maps for shared objects
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith(".so") || line.endsWith(".jar")) {
                        int n = line.lastIndexOf(" ");
                        libraries.add(line.substring(n + 1));
                    }
                }
                reader.close();
                for (String library : libraries) {
                    if (library.contains("com.saurik.substrate")) {
                        return true;
                    }
                    if (library.contains("XposedBridge.jar")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                printLog(e.toString());
            }

        } catch (Exception e) {
            printLog(e.toString());
        }
        return false;
    }

    private void printLog(String s) {
        Log.w("TAG", s);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isVPN(Context context) {

        boolean vpnInUse = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);

            return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }

        Network[] networks = connectivityManager.getAllNetworks();

        for (int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(networks[i]);
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true;
                break;
            }
        }

        return vpnInUse;
    }

}