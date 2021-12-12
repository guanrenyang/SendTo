package com.example.transferfile.provider;

import com.example.transferfile.MyApplication;
import com.example.transferfile.MyToastutils.Toastutils;
import com.example.transferfile.ResourceTable;
import com.example.transferfile.bean.AMessage;
import com.example.transferfile.bean.ImageLineItem;
import com.example.transferfile.slice.MainAbilitySlice;
import ohos.app.Context;
import ohos.data.rdb.ValuesBucket;
import utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.List;

public class ImageLineProvider extends BaseItemProvider {
    private static final String TAG = ImageLineProvider.class.getSimpleName();
    private List<ImageLineItem> list;
    private AbilitySlice slice;

    private AbilitySlice AbilitySlice;


    private DataAbilityHelper dataAbilityHelper;

    public void setMainAbilitySlice(AbilitySlice AbilitySlice){
        this.AbilitySlice = AbilitySlice;
    }

    public ImageLineProvider(List<ImageLineItem> list, AbilitySlice slice) {
        LogUtil.info(TAG,"list.size() : "+list.size());
        this.list = list;
        this.slice = slice;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list != null && position >= 0 && position < list.size()){
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Component getItemComponent(int position) {
        return getComponent(position);
    }

    private Component getComponent(int position) {
        LogUtil.info(TAG,"list.size()"+list.size());
        final Component cpt;
        cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_images_line, null, false);
        ImageLineItem imageLineItem = list.get(position);
        Image image1,image2,image3;
        image1 = (Image) cpt.findComponentById(ResourceTable.Id_image1);
        image2 = (Image) cpt.findComponentById(ResourceTable.Id_image2);
        image3 = (Image) cpt.findComponentById(ResourceTable.Id_image3);

        DataAbilityHelper helper=DataAbilityHelper.creator(slice.getContext());
        //定义图片来源对象
        ImageSource imageSource;
        Uri[] uris = imageLineItem.getUris();
        FileDescriptor fd = null;


        dataAbilityHelper=DataAbilityHelper.creator(slice);
        image1.setClickedListener(component1 -> {
        String str = String.valueOf(uris[0]);
        System.out.println("------------------------>" +str);
        File file = new File(String.valueOf(uris[0]));
        Toastutils.show(slice, "选中"+file.getAbsolutePath());
        //数据库更新
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString("nameoffile",file.getAbsolutePath());
        try {
            int j =dataAbilityHelper
                    .insert(Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names"),valuesBucket);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        });

        image2.setClickedListener(component1 -> {
        String.valueOf(uris[1]);
        File file = new File(String.valueOf(uris[1]));

        Toastutils.show(slice, "选中"+file.getAbsolutePath());
        //数据库更新
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString("nameoffile",file.getAbsolutePath());
        try {
            int j =dataAbilityHelper
                    .insert(Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names"),valuesBucket);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        });

        image3.setClickedListener(component1 -> {
        String.valueOf(uris[2]);
        File file = new File(String.valueOf(uris[2]));

        Toastutils.show(slice, "选中"+file.getAbsolutePath());
        //数据库更新
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString("nameoffile",file.getAbsolutePath());
        try {
            int j =dataAbilityHelper
                    .insert(Uri.parse("dataability:///com.example.transferfile.UploadDataAbility/names"),valuesBucket);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        });

        try {
            fd = helper.openFile(uris[0], "r");
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }
        imageSource = ImageSource.create(fd, null);
        //创建位图
        PixelMap pixelMap = imageSource.createPixelmap(null);
        image1.setPixelMap(pixelMap);
        imageSource.release();
        helper.release();

        try {
            fd = helper.openFile(uris[1], "r");
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }
        imageSource = ImageSource.create(fd, null);
        //创建位图
        pixelMap = imageSource.createPixelmap(null);
        image2.setPixelMap(pixelMap);
        imageSource.release();
        helper.release();

        try {
            fd = helper.openFile(uris[2], "r");
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }
        imageSource = ImageSource.create(fd, null);
        //创建位图
        pixelMap = imageSource.createPixelmap(null);
        image3.setPixelMap(pixelMap);
        imageSource.release();
        helper.release();

        return cpt;
    }

    @Override
    public Component getComponent(int position, Component convertComponent, ComponentContainer componentContainer) {
        return getItemComponent(position);
    }
}
