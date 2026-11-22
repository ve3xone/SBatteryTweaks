package com.sammy.sbatterytweaks;

final class CooldownCycle {
    private long recoveryStartsAt;
    private long endsAt;

    synchronized void startIfIdle(long now, long phaseMillis) {
        if (now < endsAt) return;
        recoveryStartsAt = now + phaseMillis;
        endsAt = recoveryStartsAt + phaseMillis;
    }

    synchronized boolean isRecoveryWindow(long now) {
        return now >= recoveryStartsAt && now < endsAt;
    }

    synchronized void cancel() {
        endsAt = 0;
    }
}
