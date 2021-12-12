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

package com.example.transferfile;


import static ohos.bluetooth.BluetoothHost.*;

import com.example.transferfile.adapter.BluetoothItemProvider;
import com.example.transferfile.interfaces.BluetoothEventListener;
import com.example.transferfile.slice.MainAbilitySlice;
import model.BluetoothDevice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ListContainer;
import ohos.app.AbilityContext;
import ohos.bluetooth.BluetoothHost;
import ohos.bluetooth.BluetoothRemoteDevice;
import ohos.bundle.IBundleManager;
import ohos.event.commonevent.*;
import ohos.rpc.RemoteException;
import utils.Constants;
import ohos.aafwk.ability.AbilitySlice;
import utils.LogUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BluetoothPlugin
 * All the apis are implemented in this class.
 */
public class BluetoothPlugin {
    private static final String TAG ="=>"+ BluetoothPlugin.class.getSimpleName();

    //BluetoothPlugin 实例，自定义
    private static volatile BluetoothPlugin sInstance = null;

    //蓝牙主机，可以管理蓝牙，SDK提供
    private BluetoothHost btHost = null;

    //已经发现的蓝牙设备数量
    private  int count = 0;

    //普通事件订阅者，用于发布蓝牙事件通知
    private CommonEventSubscriber commonEventSubscriber = null;

    //可用的蓝牙设备列表
    private final Set<BluetoothRemoteDevice> availableDevices = new LinkedHashSet<>();

    //应用上下文
    private AbilityContext BluetoothSliceContext;

    //自定义的蓝牙事件监听
    private BluetoothEventListener bluetoothEventListener;

    private BluetoothPlugin(AbilityContext context) {
        BluetoothSliceContext = context;
    }

    /**
     * 获取BluetoothPlugin类的实例
     * Obtains the instance of teh BluetoothPlugin class
     * @param context AbilityContext
     * @return Instance of the BluetoothPlugin class
     */
    public static BluetoothPlugin getInstance(AbilityContext context) {
        if (sInstance == null) {
            synchronized (BluetoothPlugin.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothPlugin(context);
                }
            }
        }
        return sInstance;
    }


    /**
     * 初始化蓝牙主机对象和监听器
     * Initializes the Bluetooth Host on device.
     * @param eventListener interface to update the Bluwettoth events
     */
    public void initializeBluetooth(BluetoothEventListener eventListener) {
        bluetoothEventListener = eventListener;
        btHost = BluetoothHost.getDefaultHost(BluetoothSliceContext);
        LogUtil.info(TAG, "initializeBluetooth, btHost:"+btHost);
    }


    /**
     * 获取蓝牙状态
     * Obtains the status of the Bluetooth on device.
     * @return status of Bluetooth on device
     */
    public int getBluetoothStatus() {
        LogUtil.info("getBluetoothStatus", "getBluetoothStatus:"+btHost.getBtState());
        //获取蓝牙状态
        return btHost.getBtState();
    }

    /**
     * 开启蓝牙
     * 低功耗蓝牙（BLE ,Bluetooth Low Energy）,LE是2010年才提出的
     * 经典蓝牙（classic Bluetooth）,包括BR，EDR和HS(AMP)三种模式
     * Enables the Bluetooth on device.
     */
    public void enableBluetooth() {
        LogUtil.info("enableBluetooth", "getBtState:"+btHost.getBtState());
        //获取蓝牙主机的状态
        if (btHost.getBtState() == STATE_OFF ||
                btHost.getBtState() == STATE_TURNING_OFF ||
                btHost.getBtState() ==STATE_BLE_ON) {
            LogUtil.info("enableBluetooth", "enableBt:"+btHost.getBtState());
            //开启蓝牙
            btHost.enableBt();
        }
        //事件通知蓝牙状态发生改变
        bluetoothEventListener.notifyBluetoothStatusChanged(btHost.getBtState());
    }


    /**
     * 关闭蓝牙
     * Disables the Bluetooth on device.
     */
    public void disableBluetooth() {
        if (btHost.getBtState() == STATE_ON || btHost.getBtState() == STATE_TURNING_ON) {
            btHost.disableBt();
        }
        bluetoothEventListener.notifyBluetoothStatusChanged(btHost.getBtState());
    }

    /**
     * 启动蓝牙扫描
     * Scans the currently available bluetooth devices
     */
    public void startBtScan() {
        LogUtil.info("startBtScan", "getBtState:"+btHost.getBtState());
        int btStatus = btHost.getBtState();

        if (btStatus == STATE_ON) {
            if (hasPermission()) {
                startBtDiscovery();
            } else {
                requestPermission();
            }
        }
    }


    public void StopBtScan() {
        LogUtil.info("StopBtScan", "getBtState:"+btHost.getBtState());
        int btStatus = btHost.getBtState();

        if (btStatus == STATE_ON) {
            if (hasPermission()) {
                StopBtDiscovery();
            } else {
                requestPermission();
            }
        }
    }

    /**
     * 开始蓝牙发现
     * Scans the currently available bluetooth devices
     */
    public void startBtDiscovery() {
        if (!btHost.isBtDiscovering()) {
            //开始发现设备，大约需要12.8s
            btHost.startBtDiscovery();
        }
    }

    public void StopBtDiscovery() {
        if (btHost.isBtDiscovering()) {
            //开始发现设备，大约需要12.8s
            btHost.cancelBtDiscovery();
        }
    }

    /**
     *判断是否有权限
     */
    private boolean hasPermission() {
        return BluetoothSliceContext.verifySelfPermission(Constants.PERM_LOCATION) == IBundleManager.PERMISSION_GRANTED;
    }

    /**
     * 请求位置权限，扫描蓝牙需要位置权限
     *
     */
    private void requestPermission() {
        if (BluetoothSliceContext.canRequestPermission(Constants.PERM_LOCATION)) {
            BluetoothSliceContext.requestPermissionsFromUser(
                    new String[] {Constants.PERM_LOCATION}, Constants.USER_REQUEST_LOCATION_SCAN);
        }
    }

    /**
     * 启动与给定地址的蓝牙设备配对。
     * initiate pairing with bluetooth device of given address.
     * @param pairAddress address of the bluetooth device
     */
    public void startPair(String pairAddress) {
        Optional<BluetoothRemoteDevice> optBluetoothDevice = getSelectedDevice(pairAddress);
        optBluetoothDevice.ifPresent(BluetoothRemoteDevice::startPair);
        System.out.println("--------------------------------------------------------->开始配对");
    }


    /**
     * 获取要配对的设备
     * @param pairAddress
     * @return
     */
    private Optional<BluetoothRemoteDevice> getSelectedDevice(String pairAddress) {
        if (pairAddress != null && !pairAddress.isEmpty()) {
            for (BluetoothRemoteDevice device : availableDevices) {
                if (device.getDeviceAddr().equals(pairAddress)) {
                    return Optional.ofNullable(device);
                }
            }
        }
        return Optional.empty();
    }




    /**
     * 订阅蓝牙事件
     * Subscribe for Events of Bluetooth using CommonEvents
     */
    public void subscribeBluetoothEvents() {
        MatchingSkills matchingSkills = new MatchingSkills();
        //表示蓝牙状态改变时上报的事件。
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_STATE_UPDATE);
        //指示蓝牙扫描开始时报告的事件。
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_DISCOVERY_STARTED);
        //指示蓝牙扫描完成时报告的事件。
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_DISCOVERY_FINISHED);
        //表示发现远程蓝牙设备时上报的事件。
        matchingSkills.addEvent(BluetoothRemoteDevice.EVENT_DEVICE_DISCOVERED);
        //远程蓝牙设备配对时上报的事件。
        matchingSkills.addEvent(BluetoothRemoteDevice.EVENT_DEVICE_PAIR_STATE);

        //用于创建 CommonEventSubscriber 实例并传递 subscribeInfo 参数的构造函数。
        CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);

        //订阅者
        commonEventSubscriber = new CommonEventSubscriber(subscribeInfo) {
            @Override
            public void onReceiveEvent(CommonEventData commonEventData) {
                Intent intent = commonEventData.getIntent();
                handleIntent(intent);
            }
        };
        try {
            //完成订阅
            CommonEventManager.subscribeCommonEvent(commonEventSubscriber);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "RemoteException while subscribe bluetooth events.");
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        switch (action) {
            //状态更新
            case BluetoothHost.EVENT_HOST_STATE_UPDATE:
                handleHostStateUpdate();
                break;
            //扫描开始
            case BluetoothHost.EVENT_HOST_DISCOVERY_STARTED:
                handleDeviceDiscoveryState(true);
                break;
            //表示发现远程蓝牙设备时上报的事件。
            case BluetoothRemoteDevice.EVENT_DEVICE_DISCOVERED:
                handleBluetoothDeviceDiscovered(intent);
                break;
            // 扫描完成
            case BluetoothHost.EVENT_HOST_DISCOVERY_FINISHED:
                handleDeviceDiscoveryState(false);
                break;
            //表示远程蓝牙设备配对时上报的事件。
            case BluetoothRemoteDevice.EVENT_DEVICE_PAIR_STATE:
                handleDevicePairState(intent);
                break;
            default:
                LogUtil.info(TAG, "Action not handled : " + action);
        }
    }

    private void handleDevicePairState(Intent intent) {
        BluetoothRemoteDevice btRemoteDevice = intent.getSequenceableParam(BluetoothRemoteDevice.REMOTE_DEVICE_PARAM_DEVICE);
        if (btRemoteDevice.getPairState() == BluetoothRemoteDevice.PAIR_STATE_PAIRED) {
            //更新2个设备列表
            updateAvailableDeviceList(btRemoteDevice);
            updatePairedDeviceList();
        }
    }

    private void handleDeviceDiscoveryState(boolean isStarted) {
        //处理扫描状态变化事件通知
        bluetoothEventListener.notifyDiscoveryState(isStarted);
    }

    /**
     * 处理蓝牙状态变化通知
     */
    private void handleHostStateUpdate() {

        int status = getBluetoothStatus();
        bluetoothEventListener.notifyBluetoothStatusChanged(status);
    }

    /**
     * 处理蓝牙发现事件
     * @param intent
     */
    private void handleBluetoothDeviceDiscovered(Intent intent) {
        BluetoothRemoteDevice btRemoteDevice =
                intent.getSequenceableParam(BluetoothRemoteDevice.REMOTE_DEVICE_PARAM_DEVICE);

        //未配对的设备
        if (btRemoteDevice.getPairState() != BluetoothRemoteDevice.PAIR_STATE_PAIRED && btRemoteDevice.getPairState()!=BluetoothRemoteDevice.PAIR_STATE_PAIRING) {
            //发现后添加到可用的蓝牙设备
            availableDevices.add(btRemoteDevice);
            count++;
            System.out.println("--------------------------------------------------->count="+count);
            if(count>10){
                com.example.transferfile.BluetoothPlugin.getInstance(BluetoothSliceContext).StopBtScan();
                count = 0;
            }
        }
        bluetoothEventListener.updateAvailableDevices(getAvailableDevices());
    }




    /**
     * 更新可用设备列表
     * @param remoteDevice
     */
    private void updateAvailableDeviceList(BluetoothRemoteDevice remoteDevice) {
        //移除以配对的蓝牙
        availableDevices.removeIf(device -> device.getDeviceAddr().equals(remoteDevice.getDeviceAddr()));
        bluetoothEventListener.updateAvailableDevices(getAvailableDevices());
    }

    private void updatePairedDeviceList() {
        //刷新已配对的蓝牙列表
        bluetoothEventListener.updatePairedDevices(getPairedDevices());
    }

    public List<BluetoothDevice> getAvailableDevices() {
        return getBluetoothDevices(availableDevices);
    }

    /**
     * 获取已配对的蓝牙设备列表
     * Obtains the paired Bluetooth device list.
     * @return paired Bluetooth devices
     */
    public List<BluetoothDevice> getPairedDevices() {
        //btHost.getPairedDevices()
        Set<BluetoothRemoteDevice> pairedDevices = new HashSet<>(btHost.getPairedDevices());
        return getBluetoothDevices(pairedDevices);
    }

    private List<BluetoothDevice> getBluetoothDevices(Set<BluetoothRemoteDevice> remoteDeviceList) {
        List<BluetoothDevice> btDevicesList = new ArrayList<>();
        if (remoteDeviceList != null) {
            //
            btDevicesList = remoteDeviceList.stream().map(BluetoothDevice::new).collect(Collectors.toList());
        }
        return btDevicesList;
    }


    /**
     * 取消订阅蓝牙事件
     * UnSubscribe for Bluetooth Events
     */
    public void unSubscribeBluetoothEvents() {
        if (commonEventSubscriber != null) {
            try {
                CommonEventManager.unsubscribeCommonEvent(commonEventSubscriber);
            } catch (RemoteException e) {
                LogUtil.error(TAG, "RemoteException while unsubscribing bluetooth events.");
            }
            commonEventSubscriber = null;
        }
    }
}
