package com.pairip;

/* loaded from: classes5.dex */
public final class StartupLauncher {
    private static boolean launchCalled = false;
    private static String startupProgramName = "NjyS5TMBCqEP5mX0";

    public static synchronized void launch() {
        if (launchCalled) {
            return;
        }
        launchCalled = true;
        VMRunner.invoke(startupProgramName, null);
    }

    private StartupLauncher() {
    }
}
