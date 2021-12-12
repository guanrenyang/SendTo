package com.example.transferfile.slice;


import ohos.agp.components.*;

import com.example.transferfile.MyToastutils.Toastutils;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.domain.Itemoftransfer;
import com.example.transferfile.provider.Itemoftransferprovider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.app.Context;
import ohos.data.rdb.ValuesBucket;
import utils.ThreadPoolUtils;
import ohos.bundle.ElementName;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.rpc.IRemoteObject;
import ohos.utils.net.Uri;
import ohos.wifi.IpInfo;
import ohos.wifi.WifiDevice;

import java.io.*;
import java.net.*;
import java.util.*;

public class TransferfileSlice extends AbilitySlice implements Component.ClickedListener {
        String Filename=null;
        ArrayList<String> array = new ArrayList<String>();
        DataAbilityHelper dataAbilityHelper;
        private static final int EVENT_FILE_UPLOAD_1 = 1;
        private static final int EVENT_FILE_UPLOAD_2 = 2;
        private static final int EVENT_FILE_UPLOAD_3 = 3;
        private static final int EVENT_FILE_UPLOAD_4 = 4;
        private static final String TAG = TransferfileSlice.class.getSimpleName();
        private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD000F00, TAG);
        Itemoftransferprovider itemProvider3;
        Itemoftransferprovider itemProvider4;

        MyEventHandler handlerA;

        private String ownIP ;
        private final String testIP="10.183.6.194";
        private String transferstring ;
        private static final int PORT = 8088;
        String message;
        //容器初始化数据列表
        ArrayList<Itemoftransfer> datasList3;//外传文件
        ArrayList<Itemoftransfer> datasList4;//接受文件
        //文件容器定义
        ListContainer listContainer3;
        ListContainer listContainer4;


        //计数器（文件传输多线程代用，未完全实现。）
        int number = 0 ;
        int numberofup = 0;
        int numberofdown = 0 ;
        @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_transferfile);

        if(intent.getStringParam("inputstring")!=null) {
            transferstring = intent.getStringParam("inputstring");
        }
        else{
            transferstring = "HelloWorld!";
        }
        ownIP = getlocalIpAddress();
        System.out.println("-------------------------" +
                "-------------------->本机IP为："+ownIP);


//返回按钮监听
        Button butofgetback=(Button) findComponentById(ResourceTable.Id_getbackintransfer);
        butofgetback.setClickedListener(this);


    }

    //获取自己的IP地址
    private String getlocalIpAddress(){
        WifiDevice wifiDevice = WifiDevice.getInstance(this);
        Optional<IpInfo> ipinfo = wifiDevice.getIpInfo();
        int ip=ipinfo.get().getIpAddress();
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    //线程接受事件（未实现，需与Socket配合实现）
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
            case EVENT_FILE_UPLOAD_1:
                System.out.println("--------------------------" +
                        "--------------------------->进入服务器接受线程");

                try (DatagramSocket socket =new DatagramSocket(PORT)){

                    DatagramPacket packet =new DatagramPacket(new byte[255],255);
//尝试接受字节流数据
//                    File outfile = new File("dataability:///com.example.transferfile.UploadDataAbility");
//                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outfile));
                    while (true){
                        socket.receive(packet);


//                        bos.write(packet.getData(),packet.getOffset(),packet.getLength());

                        message = new String(packet.getData(),packet.getOffset(), packet.getLength());
//                        System.out.println("--------------------------" +
//                                "----------------------->客户端接受文件："+outfile.getAbsolutePath());
                        //向数据库添加下载文件信息
                        ValuesBucket valuesBucket = new ValuesBucket();
                        valuesBucket.putString("nameoffile",message);
                        try {
                            int j =dataAbilityHelper
                                    .insert(Uri.parse("dataability:///com.example.transferfile.DownloadDataAbility/downloadfiles"),valuesBucket);
                        } catch (DataAbilityRemoteException e) {
                            e.printStackTrace();
                        }
                        System.out.println("--------------------------" +
                                "----------------------->客户端接受文件："+message);
                        datasList4 = getDataofin();
                        itemProvider4 = new Itemoftransferprovider(datasList4,TransferfileSlice.this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("-------------------------------------" +
                            "-------------------------------------->进入服务端catch");
                }
                Object object = event.object;
                if (object instanceof EventRunner) {
                    // 将原先线程的EventRunner实例投递给新创建的线程
                    EventRunner runner2 = (EventRunner) object;
                    // 将原先线程的EventRunner实例与新创建的线程的EventHandler绑定
                    EventHandler myHandler2 = new EventHandler(runner2) {
                        @Override
                        public void processEvent(InnerEvent event) {
                            // 返回主线程的操作
                            // 设置文件下载进度
//                            datasList3.set(number-numA,new Itemoftransfer(item.getText(),ResourceTable.Media_file,"100%"));
//                            Itemoftransferprovider itemProvider3 = new Itemoftransferprovider(datasList3,TransferfileSlice.this);
//                            listContainer3.setItemProvider(itemProvider3);
                            listContainer4.setItemProvider(itemProvider4);
                            Toastutils.show(TransferfileSlice.super.getContext(),"server端接受字符串："+transferstring);
                            System.out.println("------------------------------------------------" +
                                    "--------------------------------------->返回UI线程");
                        }
                    };
                    int eventId2 = 1;
                    long param2 = 0L;
                    Object object2 = this;
                    InnerEvent event2 = InnerEvent.get(eventId2, param2, object2);
                    myHandler2.sendEvent(event2); // 投递事件到原先的线程
                }
                break;

            default:
                break;
        }
    }

}

//列表容器数据初始化
    private ArrayList<Itemoftransfer> getDataofin() {//接受的文件
        //从文件下载数据库DownloadDataAbility获取下载文件传输到Container中
        dataAbilityHelper=DataAbilityHelper.creator(this);
        Uri uri=Uri.parse("dataability:///com.example.transferfile.DownloadDataAbility/downloadfiles");
        String[] columns ={"nameoffile"};
        DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
        ArrayList<Itemoftransfer> list = new ArrayList<Itemoftransfer>();
        //在Transfer中读取数据库元素。
        try {
            ResultSet result = dataAbilityHelper.query(uri,columns,dataAbilityPredicates);
            int rowcount = result.getRowCount();
            numberofdown= rowcount;
            System.out.println("-----------------------------------------------" +
                    "----------------------------------------------->rowcount:"+rowcount);
//            int i = 0;

            if(rowcount>0){
                result.goToFirstRow();
                do{
                    list.add(new Itemoftransfer(result.getString(0),ResourceTable.Media_unknown,"100%"));

//                    array.set(i,result.getString(0));
//                    i++;
                }while(result.goToNextRow());
            }

        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }


        return list;
    }

    private ArrayList<Itemoftransfer> getDataofout() {//发出的文件
        dataAbilityHelper=DataAbilityHelper.creator(this);
        Uri uri=Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names");
        String[] columns ={"nameoffile"};
        DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
        ArrayList<Itemoftransfer> list = new ArrayList<Itemoftransfer>();
        //在Transfer中读取数据库元素。
        try {
            ResultSet result = dataAbilityHelper.query(uri,columns,dataAbilityPredicates);
            int rowcount = result.getRowCount();
            numberofup= rowcount;
            number=rowcount;
//            int i = 0;

            if(rowcount>0){
                result.goToFirstRow();
                do{
                    list.add(new Itemoftransfer(result.getString(0),ResourceTable.Media_pic,"100%"));

//                    array.set(i,result.getString(0));
//                    i++;
                }while(result.goToNextRow());
            }

        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void sendMessage(){
            new Thread(()->{
                System.out.println("--------------------------" +
                        "--------------------------->进入发送线程");
                String ip = null;//服务器IP
                NetManager netManager = NetManager.getInstance(this);

                if(!netManager.hasDefaultNet()){
                    return;
                }
                NetHandle netHandle = netManager.getDefaultNet();
                if(netHandle ==null){
                    System.out.println("---------------------->netHandle为空");
                }
                //通过socket进行数据传输
                DatagramSocket socket = null;
                File file =null;
                try {
                    InetAddress address = InetAddress.getByName(testIP);
                    InetAddress address1 = InetAddress.getByName(ownIP);
                    System.out.println("--------------------------------" +
                            "-------------------------->传输文件的服务器地址为"+address);
                    System.out.println("--------------------------------" +
                            "-------------------------->传输文件的服务器地址为"+address1);
                    socket = new DatagramSocket();
                    System.out.println("--------------------------------" +
                            "-------------------------->1");
                    netHandle.bindSocket(socket);
                    System.out.println("--------------------------------" +
                            "-------------------------->2");

                    dataAbilityHelper=DataAbilityHelper.creator(this);
                    Uri uri=Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names");
                    String[] columns ={"nameoffile"};
                    DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
                    //在Transfer中读取数据库元素。
                    try {
                        System.out.println("--------------------------------" +
                                "-------------------------->try");
                        ResultSet result = dataAbilityHelper.query(uri,columns,dataAbilityPredicates);
                        int rowcount = result.getRowCount();
                        numberofup= rowcount;
                        number=rowcount;
                        if(rowcount>0){
                            result.goToFirstRow();
                            do{
                                file = new File("dataability:/media/external/images/medias/390");
                                System.out.println("-----------" +
                                        "------------------->"+file.getPath());
                            }while(result.goToNextRow());
                        }

                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();

                    }
//尝试传输本机文件
//                    FileInputStream fis =new FileInputStream(file);
//                    byte[] filebytes = new byte[(int)file.length()];
//                    fis.read(filebytes);
//                    DatagramPacket request=new DatagramPacket(filebytes,filebytes.length,address,PORT);
                    //传输字节流
                    System.out.println("------------------------------" +
                            "---------------------------->"+transferstring);
//                    byte[] buffer =transferstring.getBytes();
                    byte[] buffer =transferstring.getBytes();
                    DatagramPacket request=new DatagramPacket(buffer,buffer.length,address1,PORT);
                    socket.send(request);
                    System.out.println("--------------------------------->传输");
                    System.out.println("--------------------------------->传输成功");
                }catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("--------------------------------" +
                            "-------------------------->catch");
                }finally {
                    if(socket!=null){
                        socket.close();
                    }
                }

            }).start();
    }
    private void startServer() {
//            new Thread(()->{
//                System.out.println("--------------------------" +
//                        "--------------------------->进入服务器接受线程");
//                try (DatagramSocket socket =new DatagramSocket(PORT)){
//                    DatagramPacket packet =new DatagramPacket(new byte[255],255);
////尝试接受字节流数据
////                    File outfile = new File("dataability:///com.example.transferfile.UploadDataAbility");
////                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outfile));
//                    while (true){
//                        socket.receive(packet);
//
//                        //向数据库添加下载文件信息
//                        ValuesBucket valuesBucket = new ValuesBucket();
//                        valuesBucket.putString("nameoffile","项目传输完成");
//                        try {
//                            int j =dataAbilityHelper
//                                    .insert(Uri.parse("dataability:///com.example.transferfile.DownloadDataAbility/downloadfiles"),valuesBucket);
//                        } catch (DataAbilityRemoteException e) {
//                            e.printStackTrace();
//                        }
//
//
////                        bos.write(packet.getData(),packet.getOffset(),packet.getLength());
//
//                        message = new String(packet.getData(),packet.getOffset(), packet.getLength());
////                        System.out.println("--------------------------" +
////                                "----------------------->客户端接受文件："+outfile.getAbsolutePath());
//                        System.out.println("--------------------------" +
//                                "----------------------->客户端接受文件："+message);
//                        datasList4 = getDataofin();
//                        itemProvider4 = new Itemoftransferprovider(datasList4,TransferfileSlice.this);
//                        listContainer4.setItemProvider(itemProvider4);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
                long paramA = 0L;
                InnerEvent eventA = InnerEvent.get(EVENT_FILE_UPLOAD_1, paramA, EventRunner.current());
                handlerA.sendEvent(eventA, EventHandler.Priority.HIGH);
    }


    @Override
    public void onActive() {
        super.onActive();

//容器操作



        listContainer3 = (ListContainer) findComponentById(ResourceTable.Id_Container3);
        listContainer4 = (ListContainer) findComponentById(ResourceTable.Id_Container4);

        datasList3 = getDataofout();
        itemProvider3 = new Itemoftransferprovider(datasList3,this);
        listContainer3.setItemProvider(itemProvider3);
//容器操作结束
// 1.创建EventRunner，以托管模式为例。
        EventRunner runnerA = EventRunner.create(true);
// 2.创建MyEventHandler子类实例（我们要实现的是线程间通信）

        handlerA = new MyEventHandler(runnerA);

        startServer();
        if(number>0){
            sendMessage();
            Toastutils.show(this,message);
        }

        datasList4 = getDataofin();
        itemProvider4 = new Itemoftransferprovider(datasList4,this);
        listContainer4.setItemProvider(itemProvider4);

        //列表容器监听事件，用于实现删除任务。
        listContainer3.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                Itemoftransfer item =(Itemoftransfer) listContainer.getItemProvider().getItem(i);
                Toastutils.show(TransferfileSlice.super.getContext(), item.getText()+"停止传输");
                itemProvider3.deleteComponent(i,component,listContainer3);
                listContainer3.setItemProvider(itemProvider3);
                //需要执行上传文件数据库的删除操作。
                Uri uri=Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names");
                String[] columns ={"nameoffile"};
                DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
                dataAbilityPredicates.equalTo("nameoffile",item.getText());
                try {
                    int num = dataAbilityHelper.delete(uri,dataAbilityPredicates);
                } catch (DataAbilityRemoteException e) {
                    e.printStackTrace();
                }
//                array.remove(Filename);
                return false;
            }
        });
        listContainer4.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                Itemoftransfer item =(Itemoftransfer) listContainer.getItemProvider().getItem(i);
                Toastutils.show(TransferfileSlice.super.getContext(), item.getText()+"停止接收");
                itemProvider4.deleteComponent(i,component,listContainer4);
                listContainer4.setItemProvider(itemProvider4);
                return false;
            }
        });


        //建立与DownloadSA的连接
//Service Ability部分 仅用于实现后台Service
//完全实现后应为，跳转到SA后，在SA分配线程下载（难）
        IAbilityConnection connection = new IAbilityConnection() {
            @Override
            //连接成功的回调
            public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int i) {
                //此方法为连接SA之后的回调方法
                //RemoteObject对象就是Onconnect（）返回的DownloadSA对象。

            }

            @Override
            //连接失败的回调
            public void onAbilityDisconnectDone(ElementName elementName, int i) {

            }
        };
        if(numberofup>0) {
            //当有文件传输的时候，才和DownloadSA建立连接
            Intent intent2 = new Intent();
            intent2.setParam("filename",array);
            Operation operation = new Intent.OperationBuilder()
                    .withBundleName("com.example.transferfile")
                    .withAbilityName("com.example.transferfile.DownloadServiceAbility")
                    .build();
            intent2.setOperation(operation);
            connectAbility(intent2, connection);

        }
        else {
            disconnectAbility(connection);
        }

    }



    @Override
    protected void onInactive() {
        super.onInactive();
    }

    @Override
    protected void onBackground() {
        super.onBackground();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(Component component) {//返回选择文件界面。
        Intent i =new Intent();
        present(new ChoosefileSlice(),i);
    }
}
