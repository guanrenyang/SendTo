package com.example.transferfile.slice;



import com.example.transferfile.MainAbility;
import com.example.transferfile.MyToastutils.Toastutils;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.bean.AMessage;
import com.example.transferfile.bean.ImageLineItem;
import com.example.transferfile.domain.Itemoffile;
import com.example.transferfile.provider.ImageLineProvider;
import com.example.transferfile.provider.Itemoffileprovider;
import com.openharmony.filepicker.config.FilePickerManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.data.rdb.ValuesBucket;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.utils.net.Uri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ohos.agp.components.*;
import utils.LogUtil;
import utils.PictureManager;

import java.io.File;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class ChoosefileSlice extends AbilitySlice implements Component.ClickedListener {
    private DataAbilityHelper dataAbilityHelper;
//        ArrayList<String> namelist = new ArrayList<String>();
    Button butback;
    Button butconfirm;
    Button butchoosefile;
    Button searchfilebut;
    String filename;
    TextField filenametf;
    ArrayList<Itemoffile> datasList =new ArrayList<>();
    ListContainer listContainer;
    private static final int EVENT_FIND_FILE = 1;
    private static final int EVENT_INSERT_DATA_BASE = 2;

    String paramstring ;

    private ArrayList<AMessage> messageData;
    //图片服务
    CommonDialog dialog;
    private PictureManager pictureManager;
    private ListContainer imagelistContainer;
    private Button closeimagebutton;
    private List<Uri> imagePathElements = null;
    private List<ImageLineItem> imageLineItemList;
    private ImageLineProvider imageLineProvider;

    private static final String TAG = ChoosefileSlice.class.getSimpleName();

    //图片服务
    MyEventHandler handler;
    MyEventHandler handlerB;
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_choosefile);

        //在应用目录下创建txt文件，测试未成功，权限问题？
        File file = new File("/");
        File newfile = new File("/text.txt");
        String[] str= file.list();
        try {
//                如果没有这个文件创建一个同名文件。
//                创建失败，跳错“创建禁用文件”
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

// 1.创建EventRunner，以托管模式为例。
        EventRunner runnerA = EventRunner.create(true); // 内部会新建一个线程
        EventRunner runnerB = EventRunner.create(true);

// 2.创建MyEventHandler子类实例（我们要实现的是线程间通信）
        handler = new MyEventHandler(runnerA);
        handlerB = new MyEventHandler(runnerB);

// 3.获取InnerEvent事件。没有参数
        long param = 0L;
        InnerEvent event = InnerEvent.get(EVENT_FIND_FILE, param, EventRunner.current());
// 4.向线程A发送事件
        handler.sendEvent(event);

        dataAbilityHelper=DataAbilityHelper.creator(this);

        butback = (Button) findComponentById(ResourceTable.Id_getback);
        butconfirm = (Button) findComponentById(ResourceTable.Id_confirm);
        butchoosefile = (Button) findComponentById(ResourceTable.Id_fileconfirm);
        searchfilebut = (Button) findComponentById(ResourceTable.Id_filesearchbut) ;
        filenametf = (TextField) findComponentById(ResourceTable.Id_input);

        listContainer = (ListContainer) findComponentById(ResourceTable.Id_Containeroffilename);
//        Itemoffileprovider itemProvider = new Itemoffileprovider(datasList,this);
//        listContainer.setItemProvider(itemProvider);

        searchfilebut.setClickedListener(this);
        butchoosefile.setClickedListener(this);
        butconfirm.setClickedListener(this);//确定按钮点击后跳转到两个页面：TransferPA和DownloadSA
        filenametf.setClickedListener(this);
        butback.setClickedListener(this);



    }


    //图片服务---------------------

    private void initImageData(){
        pictureManager = new PictureManager(getApplicationContext());
        imagePathElements = pictureManager.getimageElements();
        LogUtil.info(TAG,"The size is " + imagePathElements.size());
    }

    private void initImageListContainer() {
        imagelistContainer.setReboundEffect(true);
        //imagePathElements = pictureManager.getimageElements();

        LogUtil.info(TAG,"imagePathElements.size() : "+imagePathElements.size());

        imageLineItemList = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            ImageLineItem imageLineItem = new ImageLineItem(i);
            Uri[] uris = new Uri[3];
            uris[0] = imagePathElements.get(i*3);
            uris[1] = imagePathElements.get(i*3+1);
            uris[2] = imagePathElements.get(i*3+2);
            imageLineItem.setUris(uris);
            imageLineItemList.add(imageLineItem);
        }
        LogUtil.info(TAG,"imageLineItemList.size() : "+imageLineItemList.size());
        imageLineProvider = new ImageLineProvider(imageLineItemList,this);
        imageLineProvider.setMainAbilitySlice(this);
        imagelistContainer.setItemProvider(imageLineProvider);
    }
    public  CommonDialog getDialog(){
        return this.dialog;
    }

    //图片服务终止-------------------




//线程接受事件
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
                case EVENT_INSERT_DATA_BASE: {//多线程实现传输文件数据库更新
                    // 待执行的操作，由开发者定义
                    int i = (int) event.param;
                    Itemoffile item =(Itemoffile) listContainer.getItemProvider().getItem(i);
                    item.setImg(ResourceTable.Media_checked);
                    String name = item.getText();
                    ValuesBucket valuesBucket = new ValuesBucket();
                    valuesBucket.putString("nameoffile",name);
                    try {
                        int j =dataAbilityHelper
                                .insert(Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names"),valuesBucket);
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
                    //将主线程要做的事返回：显示toast弹窗
                    Object object = event.object;
                    if (object instanceof EventRunner) {
                        // 将原先线程的EventRunner实例投递给新创建的线程
                        EventRunner runner2 = (EventRunner) object;
                        // 将原先线程的EventRunner实例与新创建的线程的EventHandler绑定
                        EventHandler myHandler2 = new EventHandler(runner2) {
                            @Override
                            public void processEvent(InnerEvent event) {
                                // 需要在原先线程执行的操作：更新listcontainer。
                                Toastutils.show(ChoosefileSlice.super.getContext(), item.getText()+"选中！");
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
                case EVENT_FIND_FILE:
                    //执行子线程的工作：查询本机文件
                    Intent i = new Intent();
                    onAbilityResult(10401,-1,i);
                    //将主线程要做的事返回。
                    Object object = event.object;
                    if (object instanceof EventRunner) {
                        // 将原先线程的EventRunner实例投递给新创建的线程
                        EventRunner runner2 = (EventRunner) object;
                        // 将原先线程的EventRunner实例与新创建的线程的EventHandler绑定
                        EventHandler myHandler2 = new EventHandler(runner2) {
                            @Override
                            public void processEvent(InnerEvent event) {
                                // 需要在原先线程执行的操作：更新listcontainer。
                                Itemoffileprovider itemProvider = new Itemoffileprovider(datasList,ChoosefileSlice.this);
                                listContainer.setItemProvider(itemProvider);
                                Toastutils.show(ChoosefileSlice.this,"找到文件");
                            }
                        };
                        int eventId2 = 1;
                        long param2 = 0L;
                        Object object2 = null;
                        InnerEvent event2 = InnerEvent.get(eventId2, param2, object2);
                        myHandler2.sendEvent(event2); // 投递事件到原先的线程
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        super.onAbilityResult(requestCode, resultCode, resultData);
        System.out.println("------------------diaoyong1");
        switch (requestCode) {
            case FilePickerManager.REQUEST_CODE:
                if (resultCode == FilePickerManager.RESULT_OK) {
                    List<String> strings = FilePickerManager.INSTANCE().obtainData();
                    //do your work
                    if(strings.isEmpty()){
                        //线程中如何使用toast弹窗？
//                        Toastutils.show(this,"文件夹中无文件");
                        System.out.println("------------------diaoyong2");
                    }
                    int i=0;
                    do{
                        if(i==0) {
                            datasList.add(i,new Itemoffile((ResourceTable.Media_zip), "测试文件"));
                        }else {
                            datasList.set(0,new Itemoffile((ResourceTable.Media_unknown),"以下为您可能想要传输的文件"));
                            datasList.add(i,new Itemoffile((ResourceTable.Media_unknown), strings.get(i)));
                        }
                        i++;
                    }while(i<(strings.size()+1));
                }
                break;
        }
    }
    @Override
    public void onActive() {

        super.onActive();
        FilePickerManager.INSTANCE()
                .from(super.getAbility())
                .forResult(FilePickerManager.REQUEST_CODE);

        //长按选中要传输的文件
        listContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                long param = i;
                //新建线程来访问数据库
                InnerEvent eventB = InnerEvent.get(EVENT_INSERT_DATA_BASE, param, EventRunner.current());
                handlerB.sendEvent(eventB, EventHandler.Priority.HIGH);
                return false;
            }
        });

        //媒体文件选择界面
        searchfilebut.setClickedListener(component1 -> {
            dialog = new CommonDialog(getContext());

            initImageData();
            DirectionalLayout directionalLayout = new DirectionalLayout(getContext());
            Component component2;
            component2 = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_image_main, null, false);
            imagelistContainer= (ListContainer) component2.findComponentById(ResourceTable.Id_list_container);
            closeimagebutton = (Button) component2.findComponentById(ResourceTable.Id_close_image_button);
            closeimagebutton.setClickedListener(component3 -> {
                dialog.destroy();
            });
            directionalLayout.addComponent(component2);
            initImageListContainer();

            dialog.setContentCustomComponent(directionalLayout);
            dialog.setTitleText("图片");
            dialog.setAlignment(0);
            dialog.setSize(MATCH_PARENT,MATCH_PARENT);
            //dialog.setContentText("This is CommonDialog Content area.");
            dialog.setButton(IDialog.BUTTON3, "CONFIRM", (iDialog, i) -> iDialog.destroy());
            dialog.show();
            //addAndUpdateMessage(messageData.size(),"message","image");
        });
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
    public void onClick(Component component) {
        if(component==butback){//返回选人阶段
            Intent i = new Intent();
            present(new MainAbilitySlice(),i);

        }else if(component==butconfirm){
            //转到传输窗口
            //只要sa没有被销毁，就不会再执行Onstart（）
            paramstring = filename;
            Intent i0=new Intent();
            i0.setParam("inputstring",paramstring);
//            i0.setParam("filename",namelist);
            present(new TransferfileSlice(),i0);

        } else if (component==butchoosefile)
        {//确定按钮
            filename=filenametf.getText();
        }else if(component==filenametf){
            filenametf.setText(null);
        }
    }

}