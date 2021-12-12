package com.example.transferfile.provider;

//import com.example.transferfile.ResourceTable;
//import com.example.transferfile.domain.Item;
//import ohos.aafwk.ability.AbilitySlice;
//import ohos.agp.components.*;
//
//import java.util.ArrayList;

import com.example.transferfile.ResourceTable;
import com.example.transferfile.domain.Item;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.ArrayList;

public class Itemprovider extends BaseItemProvider {
    private ArrayList<Item> list;//item数据集合
    private AbilitySlice as;//容器加载的界面

    //构造list。
    public Itemprovider(ArrayList<Item> list, AbilitySlice as) {
        this.list = list;
        this.as = as;
    }

    public ArrayList<Item> getList() {
        return list;
    }

    public void setList(ArrayList<Item> list) {
        this.list = list;
    }

    public AbilitySlice getAs() {
        return as;
    }

    public void setAs(AbilitySlice as) {
        this.as = as;
    }

    //重载函数
    //item数量。
    @Override
    public int getCount() {
        return list.size();
    }

    //根据索引返回数据
    @Override
    public Object getItem(int i) {
        if(list != null && i >= 0 && i < list.size()){
            return list.get(i);
        }
        return null;
    }

    //返回某项的id
    @Override
    public long getItemId(int position) {
        return position;
    }


    //返回item中要加载的布局对象
    //i表示哪一行。component表示表示要销毁的item的布局对象。
    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        DirectionalLayout dl;
        if(component != null){
            dl =  (DirectionalLayout)component;
        }else{
            //获取每一个item里面的布局对象
            dl = (DirectionalLayout) LayoutScatter.getInstance(as).parse(ResourceTable.Layout_itemview, null, false);
        }

        //把数据加载到布局里面的Text中
        //获取每一个item里面的数据
        Item item0 = list.get(position);

        Image img11=(Image) dl.findComponentById(ResourceTable.Id_itemimage11);
        img11.setImageAndDecodeBounds(item0.getImg());

        Text text11 = (Text) dl.findComponentById(ResourceTable.Id_itemtext11);
        text11.setText(item0.getText());

        Text btname = (Text) dl.findComponentById(ResourceTable.Id_itemtext12);
        btname.setText(item0.getBtname());

        //当上面的四行代码执行完毕之后，我就获取到了一个有数据的布局对象
        //此时我们只要把布局对象dl返回出去就可以了
        //其实就是因为在Item当中，最外层的就是这个dl布局对象
        return dl;
    }
}
