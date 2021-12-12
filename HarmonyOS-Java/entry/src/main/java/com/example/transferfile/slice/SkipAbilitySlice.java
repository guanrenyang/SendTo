package com.example.transferfile.slice;

//import com.example.transferfile.ResourceTable;
//import com.example.transferfile.SkipAbility;
//import ohos.aafwk.ability.AbilitySlice;
//import ohos.aafwk.content.Intent;
//import ohos.aafwk.content.Operation;
import ohos.agp.components.*;

import com.example.transferfile.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;

public class SkipAbilitySlice extends AbilitySlice implements Component.ClickedListener {
    long starttime=0;
    Image img;
    Button buttonskip;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_skip);

        buttonskip = (Button)findComponentById(ResourceTable.Id_SkipButton);
        img = (Image) findComponentById(ResourceTable.Id_imgskip);

        buttonskip.setClickedListener(this);
        img.setClickedListener(this);


    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onBackground() {
        super.onBackground();
    }

    @Override
    protected void onInactive() {
        super.onInactive();

    }

    @Override
    protected  void onStop(){super.onStop();}


    @Override
    public void onClick(Component component) {
        if(component==buttonskip) {
            Intent i = new Intent();
            Operation operation = new Intent.OperationBuilder().withDeviceId("")
                    .withBundleName("com.example.transferfile")
                    .withAbilityName("com.example.transferfile.MainAbility")
                    .build();
            i.setOperation(operation);
            present(new BluetoothSlice(),i);
//            present(new MainAbilitySlice(),i);
        }else if(component==img){
            starttime=System.currentTimeMillis();
            long nowtime=System.currentTimeMillis();

            while(nowtime-starttime<1000) {
                nowtime=System.currentTimeMillis();
            }
            Intent i = new Intent();
            Operation operation = new Intent.OperationBuilder().withDeviceId("")
                    .withBundleName("com.example.transferfile")
                    .withAbilityName("com.example.transferfile.MainAbility")
                    .build();
            i.setOperation(operation);
            present(new BluetoothSlice(),i);
//            present(new MainAbilitySlice(),i);
        }
    }

}
