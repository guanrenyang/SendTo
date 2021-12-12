package com.example.transferfile.slice;

//import com.example.transferfile.BluetoothPlugin;
//import com.example.transferfile.MyToastutils.Toastutils;
//import com.example.transferfile.ResourceTable;
//import com.example.transferfile.adapter.BluetoothItemProvider;
//import com.example.transferfile.domain.Item;
//import ohos.aafwk.ability.AbilitySlice;
//import ohos.aafwk.ability.DataAbilityHelper;
//import ohos.aafwk.ability.DataAbilityRemoteException;
//import ohos.aafwk.content.Operation;
//import ohos.agp.components.*;
//import ohos.aafwk.ability.OnClickListener;
//import ohos.aafwk.content.Intent;
//import ohos.agp.components.*;
//import ohos.app.AbilityContext;
//import ohos.app.Context;
//import ohos.bluetooth.BluetoothHost;;
//import ohos.data.rdb.ValuesBucket;
//import ohos.eventhandler.EventHandler;
//import ohos.eventhandler.EventRunner;
//import ohos.eventhandler.InnerEvent;
//import ohos.interwork.eventhandler.EventHandlerEx;
//import ohos.utils.net.Uri;

import com.example.transferfile.MyToastutils.Toastutils;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.adapter.BluetoothItemProvider;
import model.BluetoothDevice;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.app.AbilityContext;
import ohos.app.Context;
import ohos.bluetooth.BluetoothDeviceClass;
import ohos.bluetooth.BluetoothHost;
import ohos.bundle.IBundleManager;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class BluetoothSlice extends AbilitySlice
        implements Component.ClickedListener, com.example.transferfile.interfaces.BluetoothEventListener, AbsButton.CheckedStateChangedListener  {

    //可用的蓝牙设备项提供程序
    private BluetoothItemProvider availableDevicesItemProvider;

    //已配对的蓝牙项提供程序
    private BluetoothItemProvider pairedDevicesItemProvider;

    //存放设备的容器
    private DirectionalLayout containerLists;

    //蓝牙开关组件
    private Switch bluetoothSwitch;

    //蓝牙状态组件
    private Text textBluetoothStatus;

    //环形进度条
    private ProgressBar progressBar;

    //通过定时器实现一个简单的进度效果
    private int percent=0;
    private Timer timer;
    private TimerTask timerTask;

    //跳转按钮
    private  Button buttonconfirm;

    private static final int EVENT_FIND_DEVICE = 1;


    private DataAbilityHelper dataAbilityHelper;




    ArrayList<String> datalist = new ArrayList<>();
    ArrayList<String> addresslist = new ArrayList<String>();
    ArrayList<Optional<BluetoothDeviceClass>> classlist =new ArrayList<>();

    Text textip;

    AbilityContext as = (AbilityContext) this;
    MyEventHandler myHandlerA;
    EventRunner runnerA;
    MyEventHandler myHandlerB;
    EventRunner runnerB;MyEventHandler myHandlerC;
    EventRunner runnerC;
    MyEventHandler myHandlerD;
    EventRunner runnerD;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_slice_main);

    }



    @Override
    public void onActive() {
        super.onActive();

        runnerA = EventRunner.create(true);
        myHandlerA = new MyEventHandler(runnerA);
        runnerB = EventRunner.create(true);
        myHandlerB = new MyEventHandler(runnerB);
        runnerC = EventRunner.create(true);
        myHandlerC = new MyEventHandler(runnerC);
        runnerD = EventRunner.create(true);
        myHandlerD = new MyEventHandler(runnerD);
        //数据库初始化操作对象
        dataAbilityHelper=DataAbilityHelper.creator(this);


        textip = (Text) findComponentById(ResourceTable.Id_itemtext12);

        initializeBluetoothHost();
        initComponents();
        subscribeBluetoothEvents();
        jump();

    }

    private class MyEventHandler extends EventHandler {
        private MyEventHandler(EventRunner runner) {
            super(runner);
        }
        // 重写实现processEvent方法
        @Override
        public void processEvent(InnerEvent event) {
            super.processEvent(event);
            if (event == null) {
                return;
            }
            int eventId = event.eventId;
            switch (eventId) {
                case EVENT_FIND_DEVICE: {
                    utils.LogUtil.info("BluetoothSlice", "startBtScan...");
                    com.example.transferfile.BluetoothPlugin.getInstance(as).startBtScan();
                    Object object = event.object;
                    System.out.println("---------------------------------------------------线程A");
                    if (object instanceof EventRunner) {
                        // 将原先线程的EventRunner实例投递给新创建的线程
                        EventRunner runner2 = (EventRunner) object;
                        // 将原先线程的EventRunner实例与新创建的线程的EventHandler绑定
                        EventHandler myHandler2 = new EventHandler(runner2) {
                            @Override
                            public void processEvent(InnerEvent event) {
                                // 需要在原先线程执行的操作
                            }
                        };
                        int eventId2 = 1;
                        long param2 = 0L;
                        Object object2 = null;
                        InnerEvent event2 = InnerEvent.get(eventId2, param2, object2);
                        myHandler2.sendEvent(event2); // 投递事件到原先的线程
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void jump() {
        buttonconfirm=(Button) findComponentById(ResourceTable.Id_BTconfirm);
        buttonconfirm.setClickedListener(BluetoothSlice.this);
    }

    /**
     * 初始化蓝牙插件
     */
    private void initializeBluetoothHost() {
        //
        com.example.transferfile.BluetoothPlugin.getInstance(this).initializeBluetooth(this);
    }

    private void initComponents() {
        //'开始发现' 按钮，用来搜索附件的蓝牙设备
        Button btnStartDiscovery = (Button) findComponentById(ResourceTable.Id_btn_start_discovery);
        //因为implements Component.ClickedListener 所以可以这样写
        btnStartDiscovery.setClickedListener(this);

        initListContainer();
        //蓝牙状态文本组件
//        textBluetoothStatus = (Text) findComponentById(ResourceTable.Id_bluetooth_status);

        //蓝牙开关组件
        bluetoothSwitch = (Switch) findComponentById(ResourceTable.Id_bt_switch);
        //设置状态监听事件
        bluetoothSwitch.setCheckedStateChangedListener(this);

        //根据系统蓝牙状态设置开关
        updateBluetoothStatus(com.example.transferfile.BluetoothPlugin.getInstance(this).getBluetoothStatus());

        //附件的蓝牙设备容器列表
        containerLists = (DirectionalLayout) findComponentById(ResourceTable.Id_container_lists);
        //根据蓝牙状态控制是否显示附件的蓝牙设备容器列表
        containerLists.setVisibility(isBluetoothEnabled() ? Component.VISIBLE : Component.HIDE);

        //环形进度条组件
        progressBar = (ProgressBar) findComponentById(ResourceTable.Id_progressbar);
    }

    /**
     * 订阅蓝牙事件
     */
    private void subscribeBluetoothEvents() {
        //
        com.example.transferfile.BluetoothPlugin.getInstance(this).subscribeBluetoothEvents();
    }

    /**
     * 初始化容器列表
     */
    private void initListContainer() {
        ListContainer availableDevicesContainer =
                (ListContainer) findComponentById(ResourceTable.Id_list_available_devices);

        //1.获取可用的蓝牙设备
        availableDevicesItemProvider = new BluetoothItemProvider(this,
                com.example.transferfile.BluetoothPlugin.getInstance(this).getAvailableDevices());
        //设置提供程序
        availableDevicesContainer.setItemProvider(availableDevicesItemProvider);

        //2.获取已配对的蓝牙设备
        ListContainer pairedDevicesContainer = (ListContainer) findComponentById(ResourceTable.Id_list_paired_devices);
        pairedDevicesItemProvider = new BluetoothItemProvider(this,
                com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices());
        //设置提供程序
        pairedDevicesContainer.setItemProvider(pairedDevicesItemProvider);
    }

    /**
     * 更新蓝牙状态开关和文本
     * @param bluetoothStatus
     */
    private void updateBluetoothStatus(int bluetoothStatus) {
        utils.LogUtil.info("BluetoothSlice", "updateBluetoothStatus:" + bluetoothStatus);
        //开关
        bluetoothSwitch.setChecked(isBluetoothEnabled());
        //状态文本
//        textBluetoothStatus.setText(getBluetoothStatusString(bluetoothStatus));
    }


    /**
     * 显示环形进度条
     * 用定时器实现了一个进度条，遗憾的是有一定的卡顿
     * @param isShow
     */
    private void showProgressBar(boolean isShow) {
        utils.LogUtil.info("BluetoothSlice", "isShow:" + isShow);
        utils.LogUtil.info("BluetoothSlice", "timer=" + timer);
        if(isShow){
            //显示进度条
            progressBar.setVisibility(Component.VISIBLE);
            if(timer==null){
                timer = new Timer();
            }
            if(timerTask==null){
                timerTask= new TimerTask() {
                    @Override
                    public void run() {
                        if(percent==10){
                            percent=1;
                            progressBar.setProgressValue(0);
                        }else{
                            percent++;
                        }
                        utils.LogUtil.info("BluetoothSlice", "percent:" + percent);
                        getContext().getUITaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgressValue(percent*10);
                            }
                        });
                    }
                };
                //
                timer.schedule(timerTask, 0, 1000);
            }
        }else {
            //隐藏进度条
            progressBar.setProgressValue(0);
            progressBar.setVisibility(Component.HIDE);

            if(timer!=null){
                utils.LogUtil.info("BluetoothSlice", "timer set null");
                timer.cancel();
                timerTask.cancel();
                timer=null;
                timerTask=null;
            }
        }

    }

    /**
     * 获取蓝牙状态
     * @return
     */
    private boolean isBluetoothEnabled() {

        int status = com.example.transferfile.BluetoothPlugin.getInstance(this).getBluetoothStatus();
        utils.LogUtil.info("isBluetoothEnabled", "isBluetoothEnabled:"+status);
        return status == BluetoothHost.STATE_ON;
    }


    private String getBluetoothStatusString(int bluetoothStatus) {
        utils.LogUtil.info("bluetoothStatus", "bluetoothStatus:"+bluetoothStatus);
        switch (bluetoothStatus) {

            case BluetoothHost.STATE_OFF:

            case BluetoothHost.STATE_BLE_TURNING_OFF:
                //disabled 不可用
                return utils.Constants.BT_DISABLED;

            case BluetoothHost.STATE_TURNING_ON:
                //turning on 开启
                return utils.Constants.BT_TURNING_ON;

            case BluetoothHost.STATE_ON:
                //enabled 可用的
                return utils.Constants.BT_ENABLED;

            case BluetoothHost.STATE_TURNING_OFF:
                //turning off 关闭
                return utils.Constants.BT_TURNING_OFF;

            default:
                //undefined 未定义
                return utils.Constants.BT_UNDEFINED;
        }
    }

    @Override
    public void onForeground(Intent intent) {
        datalist.clear();
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        //取消订阅蓝牙事件
        com.example.transferfile.BluetoothPlugin.getInstance(this).unSubscribeBluetoothEvents();
        super.onStop();
    }


    @Override
    public void onClick(Component component) {
        if(component==buttonconfirm){
            for(int i =0 ; i<com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices().size();i++) {
                String data = com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices().get(i).getName();
                String name = com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices().get(i).getAddress();
//                Optional<BluetoothDeviceClass> type = com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices().get(i).getType();
                System.out.println("------------------------------------------------------>"+com.example.transferfile.BluetoothPlugin.getInstance(this).getPairedDevices().size());
                datalist.add(data);
                addresslist.add(name);
//                classlist.add(type);
            }
            Intent i = new Intent();
            Operation operation = new Intent.OperationBuilder().withDeviceId("")
                    .withBundleName("com.example.transferfile")
                    .withAbilityName("com.example.transferfile.MainAbility")
                    .build();
            i.setStringArrayListParam("btname",datalist);
            i.setStringArrayListParam("btaddress",addresslist);
            i.setOperation(operation);
            present(new MainAbilitySlice(),i);

        }else {
            utils.LogUtil.info("BluetoothSlice", "startBtScan...");
            //开始发现  扫描蓝牙设备
            if (component.getId() == ResourceTable.Id_btn_start_discovery) {
                utils.LogUtil.info("BluetoothSlice", "startBtScan...");
                com.example.transferfile.BluetoothPlugin.getInstance(as).startBtScan();

            }

        }


    }

    @Override
    public void updateAvailableDevices(List<BluetoothDevice> list) {
        //implements BluetoothEventListener
        //更新容器数据
        availableDevicesItemProvider.updateDeviceList(list);
    }

    @Override
    public void updatePairedDevices(List<model.BluetoothDevice> list) {
        //implements BluetoothEventListener
        //更新容器数据
        pairedDevicesItemProvider.updateDeviceList(list);

    }

    @Override
    public void notifyBluetoothStatusChanged(int bluetoothStatus) {
        utils.LogUtil.info("notifyBluetoothStatusChanged", "bluetoothStatus:"+bluetoothStatus);
        //蓝牙状态改变事件通知
        updateBluetoothStatus(bluetoothStatus);
    }

    @Override
    public void notifyDiscoveryState(boolean isStarted) {
        //蓝牙发现状态的事件通知
        showProgressBar(isStarted);
    }



    @Override
    public void onCheckedChanged(AbsButton absButton, boolean isChecked) {
        //开关状态改变事件触发
        if (absButton.getId() == ResourceTable.Id_bt_switch && containerLists != null) {
            if (isChecked) {
                utils.LogUtil.info("onCheckedChanged", "enableBluetooth");
                //开启蓝牙
                com.example.transferfile.BluetoothPlugin.getInstance(this).enableBluetooth();
                containerLists.setVisibility(Component.VISIBLE);
            } else {
                //关闭蓝牙
                com.example.transferfile.BluetoothPlugin.getInstance(this).disableBluetooth();
                containerLists.setVisibility(Component.HIDE);


            }
        }
    }
}
