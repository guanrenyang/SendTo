package com.example.transferfile.provider;

//import com.example.transferfile.ResourceTable;
//import com.example.transferfile.domain.Item;
//import com.example.transferfile.domain.Itemoftransfer;
//import ohos.aafwk.ability.AbilitySlice;
//import ohos.agp.components.*;
//
//import java.io.InputStreamReader;
//import java.util.ArrayList;

import com.example.transferfile.ResourceTable;
import com.example.transferfile.domain.Itemoftransfer;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.ArrayList;

public class Itemoftransferprovider extends BaseItemProvider {
    private ArrayList<Itemoftransfer> list;//itemoftransfer数据集合
    private AbilitySlice as;//容器加载的界面

    //构造list。
    public Itemoftransferprovider(ArrayList<Itemoftransfer> list, AbilitySlice as) {
        this.list = list;
        this.as = as;
    }

    public ArrayList<Itemoftransfer> getList() {
        return list;
    }

    public void setList(ArrayList<Itemoftransfer> list) {
        this.list = list;
    }

    public AbilitySlice getAs() {
        return as;
    }

    public void setAs(AbilitySlice as) {
        this.as = as;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if(list != null && i >= 0 && i < list.size()){
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int position)  {
        return position;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        DirectionalLayout dl;
        if(component != null){
            dl =  (DirectionalLayout)component;
        }else{
            //获取每一个item里面的布局对象
            dl = (DirectionalLayout) LayoutScatter.getInstance(as).parse(ResourceTable.Layout_itemviewoftransfer, null, false);
        }

        //获取每一个item里面的数据
        Itemoftransfer item = list.get(i);

        //把数据加载到布局里面的Text中
        Text text = (Text) dl.findComponentById(ResourceTable.Id_ittext);
        text.setText(item.getText());
// 跑马灯效果
        text.setTruncationMode(Text.TruncationMode.AUTO_SCROLLING);
// 始终处于自动滚动状态
        text.setAutoScrollingCount(Text.AUTO_SCROLLING_FOREVER);
// 启动跑马灯效果
        text.startAutoScrolling();
        Image img = (Image) dl.findComponentById(ResourceTable.Id_filepic);
        img.setImageAndDecodeBounds(item.getImg());
        Text progresstxt=(Text) dl.findComponentById(ResourceTable.Id_Progresstext);
        progresstxt.setText(item.getProgresstxt());
        //当上面的四行代码执行完毕之后，我就获取到了一个有数据的布局对象
        //此时我们只要把布局对象dl返回出去就可以了
        //其实就是因为在Item当中，最外层的就是这个dl布局对象

        return dl;
    }


    public Component deleteComponent(int i, Component component, ComponentContainer componentContainer){
        DirectionalLayout dl;
        if(component != null){
            dl =  (DirectionalLayout)component;
        }else{
            //获取每一个item里面的布局对象
            dl = (DirectionalLayout) LayoutScatter.getInstance(as).parse(ResourceTable.Layout_itemviewoftransfer, null, false);
        }
        //获取每一个item里面的数据
        Itemoftransfer item = list.remove(i);
        //把数据加载到布局里面的Text中
        Text text = (Text) dl.findComponentById(ResourceTable.Id_ittext);
        text.setText(item.getText());
        Image img =(Image) dl.findComponentById(ResourceTable.Id_filepic);
        img.setImageAndDecodeBounds(item.getImg());
        Text progresstxt=(Text) dl.findComponentById(ResourceTable.Id_Progresstext);
        progresstxt.setText(item.getProgresstxt());

        return dl;
    }
}
