package com.example.transferfile.slice;



import com.example.transferfile.MyToastutils.Toastutils;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.domain.Item;
import com.example.transferfile.domain.Pic;
import com.example.transferfile.provider.Itemprovider;
import com.openharmony.filepicker.config.FilePickerManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.app.Context;
import ohos.bluetooth.BluetoothHost;
import ohos.bundle.IBundleManager;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSetIndexOutOfRangeException;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.security.SystemPermission;
import ohos.utils.net.Uri;


import java.util.ArrayList;
import java.util.Random;


import ohos.data.resultset.ResultSet;
import ohos.eventhandler.EventHandler;

public class MainAbilitySlice extends AbilitySlice {
    ArrayList<Pic> listofPic = new ArrayList<>();

    private static final int EVENT_INSERT_DATA_BASE = 1;
    private DataAbilityHelper dataAbilityHelper;
    private ArrayList<String> btnames= new ArrayList<>();
    private ArrayList<String> address = new ArrayList<>();

    private static final String PERM_LOCATION = "ohos.permission.LOCATION";
    private static final String PERM_WRITE = "ohos.permission.WRITE_MEDIA";
    private static final String PERM_READ = "ohos.permission.READ_MEDIA";

    private Context context;

    ListContainer listContainer1;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        btnames=intent.getStringArrayListParam("btname");
        address=intent.getStringArrayListParam("btaddress");


        requestPermissionsFromUser(new String[]{SystemPermission.READ_USER_STORAGE,SystemPermission.LOCATION,
                SystemPermission.WRITE_USER_STORAGE,SystemPermission.GET_NETWORK_INFO},1123);

        BluetoothHost bluetoothHost = BluetoothHost.getDefaultHost(this);
        bluetoothHost.enableBt();
        //获取蓝牙开关状态接口。
        int state = bluetoothHost.getBtState();


    }

    private void register(Context ability) {
        context = ability;
        requestPermission();
    }
    private void requestPermission() {
        if (context.verifySelfPermission(PERM_LOCATION) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[] {PERM_LOCATION}, 0);
        }
        if (context.verifySelfPermission(PERM_READ) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[] {PERM_READ}, 0);
        }
        if (context.verifySelfPermission(PERM_WRITE) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[] {PERM_WRITE}, 0);
        }
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
                case EVENT_INSERT_DATA_BASE: {//多线程实现ip数据库更新
                    // 待执行的操作，由开发者定义
                    int i = (int) event.param;
                    Item item =(Item) listContainer1.getItemProvider().getItem(i);
                    String IPtext=item.getText();//长按图片的文本（IP地址）
                    //向IP数据库中传入IP信息
                    ValuesBucket valuesBucketofip = new ValuesBucket();
                    valuesBucketofip.putString("theip",IPtext);
                    //设备名
                    ValuesBucket valuesBucketofname = new ValuesBucket();
                    valuesBucketofname.putString("device",item.getBtname());
                    try {
                        dataAbilityHelper
                                .insert(Uri.parse("dataability:///com.example.transferfile.IPDataAbility/ips"),valuesBucketofip);
                        System.out.println("-------------------------------------------------" +
                                "--------------------------------------->进入IPdata");
                        dataAbilityHelper
                                .insert(Uri.parse("dataability:///com.example.transferfile.NameDataAbility/devices"),valuesBucketofname);
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    present(new ChoosefileSlice(),intent);
                    Object object = event.object;
                    if (object instanceof EventRunner) {
                        // 将原先线程的EventRunner实例投递给新创建的线程
                        EventRunner runner2 = (EventRunner) object;
                        // 将原先线程的EventRunner实例与新创建的线程的EventHandler绑定
                        EventHandler myHandler2 = new EventHandler(runner2) {
                            @Override
                            public void processEvent(InnerEvent event) {
                                // 需要在原先线程执行的操作：更新listcontainer。
                                Toastutils.show(MainAbilitySlice.super.getContext(), "IP数据库更新+"+IPtext);
                                Toastutils.show(MainAbilitySlice.super.getContext(), "IP数据库更新name+"+item.getBtname());
//                                Toastutils.show(MainAbilitySlice.this,IPtext.substring(5));
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

    @Override
    public void onActive() {
        super.onActive();

// 1.创建EventRunner，以托管模式为例。
        EventRunner runnerA = EventRunner.create(true); // 内部会新建一个线程
// 2.创建MyEventHandler子类实例（我们要实现的是线程间通信）
        MainAbilitySlice.MyEventHandler handler = new MainAbilitySlice.MyEventHandler(runnerA);
// 3.获取InnerEvent事件。没有参数

        //找到IP显示图片
        listofPic.add(new Pic(ResourceTable.Media_computer));
        listofPic.add(new Pic(ResourceTable.Media_phone));
        listofPic.add(new Pic(ResourceTable.Media_iPad));


        dataAbilityHelper=DataAbilityHelper.creator(this);

        //找到ListContainer
        listContainer1 = (ListContainer) findComponentById(ResourceTable.Id_Container1);

        //创建集合并给集合添加数据
        ArrayList<Item> datasList1 = getData();


        //创建一个Item的管理员对象（适配器对象）
        //并把要展示的所有数据和要加载的页面传递过去
        Itemprovider itemProvider1 = new Itemprovider(datasList1,this);

        //把适配器交给列表容器组件
        listContainer1.setItemProvider(itemProvider1);



        listContainer1.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                long param = i;
                InnerEvent event = InnerEvent.get(EVENT_INSERT_DATA_BASE, param, EventRunner.current());
                // 4.向线程A发送事件
                handler.sendEvent(event);
                return false;
            }
        });

        FilePickerManager.INSTANCE()
                .from(this)
                .forResult(FilePickerManager.REQUEST_CODE);
        FilePickerManager.INSTANCE().obtainData();
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

    Random r =new Random();
    //IP地址数据从后台传递过来的。
    public ArrayList<Item>  getData(){
        //访问服务器获取要展示的数据。
        ArrayList<Item> list = new ArrayList<>();
        Uri uri=Uri.parse("dataability:///com.example.transferfile.NameDataAbility/devices");
        Uri uriiofip = Uri.parse("dataability:///com.example.transferfile.IPDataAbility/ips");
        String[] columns ={"device"};
        String[] columnsofip ={"theip"};
        DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
        //给集合添加数据


        if(btnames!=null) {
            for (int i = 0; i < btnames.size(); i++) {
                System.out.println(btnames.get(i));
                if(btnames.get(i).equals("DESKTOP-S763IHG")){
                    list.add(new Item(ResourceTable.Media_computer, address.get(i), btnames.get(i)));
                }else if(btnames.get(i).equals("HONOR Band 5-458"))
                {
                    list.add(new Item(ResourceTable.Media_band, address.get(i), btnames.get(i)));
                }else if (btnames.get(i).equals("WH-H910N (h.ear)"))
                {
                    list.add(new Item(ResourceTable.Media_hear, address.get(i), btnames.get(i)));
                }else {
                    int randomindex = r.nextInt(3);
                    Pic pic = listofPic.get(randomindex);
                    list.add(new Item(pic.getPhotoID(), address.get(i), btnames.get(i)));
                }
            }
        }else {
            try {
                ResultSet resultofips = dataAbilityHelper.query(uriiofip,columnsofip,dataAbilityPredicates);
                ResultSet resultofdevicename = dataAbilityHelper.query(uri,columns,dataAbilityPredicates);
                int rowcountofip = resultofips.getRowCount();
                int rowcount = resultofdevicename.getRowCount();
                int numberofbl= rowcount;
//            int i = 0;

                if(rowcount>0){
                    int i =0;
                    resultofdevicename.goToFirstRow();
                    do{
                        int randomindex = r.nextInt(3);
                        Pic pic = listofPic.get(randomindex);
                        System.out.println("----------------------------------------------------->"+rowcount);
                        list.add(new Item(pic.getPhotoID()," ",resultofdevicename.getString(0)));
//                    array.set(i,result.getString(0));
//                    i++;
                    }while(resultofdevicename.goToNextRow());
                }

            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }
        }

        return list;
    }



}
