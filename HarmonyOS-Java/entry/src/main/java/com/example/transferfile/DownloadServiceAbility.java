package com.example.transferfile;

//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.ability.LocalRemoteObject;
//import ohos.aafwk.content.Intent;
//import ohos.event.notification.NotificationRequest;
//import ohos.rpc.IRemoteObject;
//import ohos.hiviewdfx.HiLog;
//import ohos.hiviewdfx.HiLogLabel;
//
//import java.util.ArrayList;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.LocalRemoteObject;
import ohos.aafwk.content.Intent;
import ohos.event.notification.NotificationRequest;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;

import java.util.ArrayList;

public class DownloadServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    //第一次创建serviceability时引用onstart()
    //可以在这初始化想要的功能
    ArrayList<String> Filename=new ArrayList<String>();
    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "DownloadServiceAbility::onStart");
        super.onStart(intent);
        System.out.println("-------download");
        System.out.println("-------Onstart");

    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "DownloadServiceAbility::onBackground");
    }

    //销毁时调用，可以在此释放一些资源.
    //xxx.release
    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "DownloadServiceAbility::onStop");
    }

    //启动当前sa时触发onCommand方法。（在OnStart之后）
    //在这里可以实现启动服务，因为Onstart只能调用一次。
    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
        System.out.println("-------OnCommond");
    }


    //连接后调用这个函数
    //返回一个意图对象
    //当应用连接到此sa时，会执行OnConnect().
    @Override
    public IRemoteObject onConnect(Intent intent) {
        //前台Service。
        System.out.println("---------connect DownloadSA");
        Filename = intent.getStringArrayListParam("filename");
//         创建通知，其中1005为notificationId
        NotificationRequest request = new NotificationRequest(1005);
        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle("FileTransfer").setText("正在传输文件");
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(content);
        request.setContent(notificationContent);

// 绑定通知，1005为创建通知时传入的notificationId
        this.keepBackgroundRunning(1005, request);
        return new LocalRemoteObject(){};


        //停止Service 停止文件传输？？
        //长按item调用这个函数
        //如果对应某个任务终止，而不是整个Service中止
        //terminateAbility()全部任务终止。
    }

    //对应OnConnect，当应用断开与当前sa时会调用执行onDisconnect（）。
    @Override
    public void onDisconnect(Intent intent) {
        System.out.println("----------Disconnected");
    }
}