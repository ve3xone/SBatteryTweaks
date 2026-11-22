package com.sammy.sbatterytweaks;

import android.os.BatteryManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Run with app_process and the app APK on the classpath; uses no device settings. */
public final class BypassStateTest {
    public static void main(String[] args) throws ReflectiveOperationException {
        Class<?> receiver = Class.forName("com.sammy.sbatterytweaks.BatteryReceiver");
        Class<?> worker = Class.forName("com.sammy.sbatterytweaks.BatteryWorker");
        Method isBypassed = Class.forName("com.sammy.sbatterytweaks.BatteryService")
                .getMethod("isBypassed");

        setInt(receiver, "mPlugged", BatteryManager.BATTERY_PLUGGED_USB);
        setInt(receiver, "mStatus", BatteryManager.BATTERY_STATUS_NOT_CHARGING);
        worker.getField("bypassSupported").setBoolean(null, true);
        worker.getField("pausePdSupported").setBoolean(null, true);
        worker.getField("pausePdEnabled").setBoolean(null, false);
        expect(isBypassed, false, "Samsung protection is not PD bypass");

        worker.getField("pausePdEnabled").setBoolean(null, true);
        setInt(receiver, "mStatus", BatteryManager.BATTERY_STATUS_CHARGING);
        expect(isBypassed, true, "active PD bypass is reported while plugged in");
        setInt(receiver, "mPlugged", 0);
        expect(isBypassed, false, "unplugged PD state is not shown as bypass");

        setInt(receiver, "mPlugged", BatteryManager.BATTERY_PLUGGED_USB);
        setInt(receiver, "mStatus", BatteryManager.BATTERY_STATUS_NOT_CHARGING);
        worker.getField("pausePdSupported").setBoolean(null, false);
        expect(isBypassed, true, "legacy sysfs bypass keeps its status fallback");
    }

    private static void setInt(Class<?> owner, String name, int value) throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(null, value);
    }

    private static void expect(Method isBypassed, boolean expected, String message)
            throws ReflectiveOperationException {
        if (((Boolean) isBypassed.invoke(null)) != expected) throw new AssertionError(message);
    }
}
