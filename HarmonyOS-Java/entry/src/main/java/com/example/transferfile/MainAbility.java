package com.example.transferfile;

//import com.example.transferfile.slice.ChoosefileSlice;
//import com.example.transferfile.slice.MainAbilitySlice;
//import com.example.transferfile.slice.TransferfileSlice;
//import com.openharmony.filepicker.config.FilePickerManager;
//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.content.Intent;

import com.example.transferfile.slice.ChoosefileSlice;
import com.example.transferfile.slice.MainAbilitySlice;
import com.example.transferfile.slice.TransferfileSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.security.SystemPermission;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermissionsFromUser(new String[]{SystemPermission.READ_USER_STORAGE,SystemPermission.LOCATION,
                SystemPermission.WRITE_USER_STORAGE, SystemPermission.CAMERA,SystemPermission.MICROPHONE},1123);

        super.addActionRoute("choosefile", ChoosefileSlice.class.getName());
        super.addActionRoute("transferfile", TransferfileSlice.class.getName());



    }
    @Override
    protected void onInactive() {
        super.onInactive();
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onBackground() {
        super.onBackground();
    }

    @Override
    protected void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
