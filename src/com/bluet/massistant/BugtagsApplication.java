package com.bluet.massistant;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

import android.app.Application;

public class BugtagsApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();
        //在这里初始化
        //Bugtags.start("APP_KEY", this, Bugtags.BTGInvocationEventBubble);
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).//是否获取位置
                trackingCrashLog(true).//是否收集crash
                trackingConsoleLog(true).//是否收集console log
                trackingUserSteps(true).//是否收集用户操作步骤
                versionName("1.0.1").//自定义版本名称
                versionCode(10).//自定义版本号
                build();

       Bugtags.start("0f124ef7b6927a2ca17adb679d31ac16", this, Bugtags.BTGInvocationEventBubble, options);
    }
}
