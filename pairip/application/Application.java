package com.pairip.application;

import android.content.Context;
import android.content.pm.PackageManager;
import com.facial.prerna.technosys.pvt.psattendance.Datetime.App;
import com.pairip.SignatureCheck;
import com.pairip.VMRunner;

/* loaded from: classes5.dex */
public class Application extends App {
    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) throws PackageManager.NameNotFoundException {
        VMRunner.setContext(context);
        SignatureCheck.verifyIntegrity(context);
        super.attachBaseContext(context);
    }
}
