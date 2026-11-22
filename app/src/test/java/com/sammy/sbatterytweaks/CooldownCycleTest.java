package com.sammy.sbatterytweaks;

public final class CooldownCycleTest {
    public static void main(String[] args) {
        CooldownCycle cycle = new CooldownCycle();
        check(!cycle.isRecoveryWindow(0), "idle cycle must not resume charging");

        cycle.startIfIdle(1_000, 3_000);
        cycle.startIfIdle(2_000, 1_000);
        check(!cycle.isRecoveryWindow(3_999), "repeated hot samples must not restart cooling");
        check(cycle.isRecoveryWindow(4_000), "resume charging at the phase boundary");
        check(cycle.isRecoveryWindow(6_999), "keep recovery active through the second phase");
        check(!cycle.isRecoveryWindow(7_000), "recovery must expire at the cycle boundary");

        cycle.startIfIdle(7_000, 3_000);
        check(cycle.isRecoveryWindow(10_000), "a new cycle must start after expiration");
        cycle.cancel();
        check(!cycle.isRecoveryWindow(10_000), "disabling the timer must end recovery immediately");
        cycle.startIfIdle(10_000, 1_000);
        check(cycle.isRecoveryWindow(11_000), "re-enabling must allow a fresh cycle");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
