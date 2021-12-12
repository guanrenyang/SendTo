package com.example.transferfile;

//import com.example.transferfile.slice.SkipAbilitySlice;
//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.content.Intent;

import com.example.transferfile.slice.SkipAbilitySlice;
import com.example.transferfile.slice.TransferfileSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class SkipAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(SkipAbilitySlice.class.getName());
        super.addActionRoute("Bluetooth", TransferfileSlice.class.getName());
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
