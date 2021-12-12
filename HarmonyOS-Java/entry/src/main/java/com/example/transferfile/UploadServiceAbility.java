package com.example.transferfile;

//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.content.Intent;
//import ohos.rpc.IRemoteObject;
//import ohos.hiviewdfx.HiLog;
//import ohos.hiviewdfx.HiLogLabel;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;

public class UploadServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "UploadServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "UploadServiceAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "UploadServiceAbility::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return null;
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}