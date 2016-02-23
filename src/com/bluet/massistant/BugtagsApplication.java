package com.bluet.massistant;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

import android.app.Application;

public class BugtagsApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();
        //�������ʼ��
        //Bugtags.start("APP_KEY", this, Bugtags.BTGInvocationEventBubble);
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).//�Ƿ��ȡλ��
                trackingCrashLog(true).//�Ƿ��ռ�crash
                trackingConsoleLog(true).//�Ƿ��ռ�console log
                trackingUserSteps(true).//�Ƿ��ռ��û���������
                versionName("1.0.1").//�Զ���汾����
                versionCode(10).//�Զ���汾��
                build();

       Bugtags.start("0f124ef7b6927a2ca17adb679d31ac16", this, Bugtags.BTGInvocationEventBubble, options);
    }
}
