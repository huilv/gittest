package com.daunkredit.program.sulu.app.custom.alipush;

import com.alibaba.sdk.android.push.notification.AdvancedCustomPushNotification;
import com.alibaba.sdk.android.push.notification.CustomNotificationBuilder;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;

/**
 * @作者:My
 * @创建日期: 2017/5/12 13:29
 * @描述:自定义通知的样式
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class CustomPushNotifactionManager {
    public static void init(){
        AdvancedCustomPushNotification notification = new AdvancedCustomPushNotification(R.layout.notifaction_ali_push, R.id.m_icon, R.id.m_title, R.id.m_text);//创建高级自定义样式通知,设置布局文件以及对应的控件ID
        notification.setServerOptionFirst(true);//设置服务端配置优先
        notification.setBuildWhenAppInForeground(false);//设置当推送到达时如果应用处于前台不创建通知
        boolean res = CustomNotificationBuilder.getInstance().setCustomNotification(FieldParams.NotifactionId.ONE, notification);//注册该通知,并设置ID为2
//        PushServiceFactory.getCloudPushService().clearNotifications();
    }
}
