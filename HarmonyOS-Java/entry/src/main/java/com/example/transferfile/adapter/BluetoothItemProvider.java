/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.transferfile.adapter;
//



//

import com.example.transferfile.BluetoothPlugin;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.slice.MainAbilitySlice;
import model.BluetoothDevice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.utils.Color;
import ohos.app.AbilityContext;
import ohos.bluetooth.BluetoothRemoteDevice;
import ohos.data.rdb.ValuesBucket;
import ohos.utils.net.Uri;
import utils.LogUtil;

import java.io.OutputStream;
import java.util.List;
import ohos.agp.components.Text;

import java.lang.Object;

/**
 * BluetoothItemProvider extends BaseItemProvider
 */
public class BluetoothItemProvider extends BaseItemProvider {
    //数据库操作
    private DataAbilityHelper dataAbilityHelper;

    public final AbilityContext context;

    private List<BluetoothDevice> bluetoothDeviceList;

    public BluetoothItemProvider(AbilityContext context, List<BluetoothDevice> itemList) {
        this.context = context;
        bluetoothDeviceList = itemList;
    }

    @Override
    public int getCount() {
        return bluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        return getRootComponent(position);
    }

    private Component getRootComponent(int position) {

        //List item 布局组件
        Component rootComponent = LayoutScatter.getInstance(context)
                .parse(ResourceTable.Layout_list_item, null, false);

        Text deviceName = (Text) rootComponent.findComponentById(ResourceTable.Id_bluetooth_device_name);

        //蓝牙设备名称
        BluetoothDevice bluetoothDevice = bluetoothDeviceList.get(position);
        deviceName.setText(bluetoothDevice.getName());

        //设置点击监听事件，开始配对
        rootComponent.setClickedListener(component -> {
            LogUtil.info("BluetoothItemProvider", "startPair:" + bluetoothDevice.getAddress());

            System.out.println("----------------------------------------------------------->蓝牙配对");
            if (bluetoothDevice.getPairState() == BluetoothRemoteDevice.PAIR_STATE_NONE) {//表示对端设备未配对。
                //启动与给定地址的蓝牙设备配对。
                BluetoothPlugin.getInstance(context).startPair(bluetoothDevice.getAddress());
                //作为客户端接受对方的IP

//                Socket socket = new Socket(bluetoothDevice.getUUID().);
//                DataListenSocket dataListenSocket =(DataListenSocket) Socket;
//                SppClientSocket sppClientSocket =dataListenSocket.buildRfcommDataSocket() ;

                ValuesBucket valuesBucket = new ValuesBucket();
                valuesBucket.putString("ip",bluetoothDevice.getAddress());
                dataAbilityHelper=DataAbilityHelper.creator(context);
                try {
                    int j =dataAbilityHelper
                            .insert(Uri.parse("dataability:///com.example.transferfile.IPDataAbility/ips"),valuesBucket);
                } catch (DataAbilityRemoteException e) {
                    e.printStackTrace();
                }


                System.out.println("----------------------------------------------------------->蓝牙配对成功");
            }else if(bluetoothDevice.getPairState()== BluetoothRemoteDevice.PAIR_STATE_PAIRED){//表示点击了已经配对完成的设备
                //进行接收
                //存入数据库
                //作为服务器发送自己的IP
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream os =null;
                        try{

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        return rootComponent;
    }

    /**
     * 更新蓝牙设备列表
     * updates available Bluetooth devices in UI
     *
     * @param devices list of Bluetooth devices
     */
    public void updateDeviceList(List<BluetoothDevice> devices) {
        bluetoothDeviceList = devices;
        notifyDataChanged();
    }
}
