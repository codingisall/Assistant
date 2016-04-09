package com.bluet.massistant;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

import android.app.Application;

public class BugtagsApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).
                trackingCrashLog(true).
                trackingConsoleLog(true).
                trackingUserSteps(true).
                versionName("1.0.1").
                versionCode(10).
                build();

       Bugtags.start("0f124ef7b6927a2ca17adb679d31ac16", this, Bugtags.BTGInvocationEventBubble, options);
    }
}
