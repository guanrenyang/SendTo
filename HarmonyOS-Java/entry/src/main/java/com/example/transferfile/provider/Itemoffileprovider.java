package com.example.transferfile.provider;

//import com.example.transferfile.ResourceTable;
//import com.example.transferfile.domain.Item;
//import com.example.transferfile.domain.Itemoffile;
//import ohos.aafwk.ability.AbilitySlice;
//import ohos.agp.components.*;
//
//import java.util.ArrayList;

import com.example.transferfile.ResourceTable;
import com.example.transferfile.domain.Itemoffile;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.ArrayList;

public class Itemoffileprovider extends BaseItemProvider {
    private ArrayList<Itemoffile> list;//item数据集合
    private AbilitySlice as;//容器加载的界面

    //构造list。
    public Itemoffileprovider(ArrayList<Itemoffile> list, AbilitySlice as) {
        this.list = list;
        this.as = as;
    }
    public ArrayList<Itemoffile> getList() {
        return list;
    }

    public void setList(ArrayList<Itemoffile> list) {
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        DirectionalLayout dl;
        if(component != null){
            dl =  (DirectionalLayout)component;
        }else{
            //获取每一个item里面的布局对象
            dl = (DirectionalLayout) LayoutScatter.getInstance(as).parse(ResourceTable.Layout_itemoffilename, null, false);
        }
        //把数据加载到布局里面的Text中
        //获取每一个item里面的数据
        Itemoffile item = list.get(i);

        Image img=(Image) dl.findComponentById(ResourceTable.Id_imageoffile);
        img.setImageAndDecodeBounds(item.getImg());

        Text text = (Text) dl.findComponentById(ResourceTable.Id_textoffile);
        text.setText(item.getText());

        //当上面的四行代码执行完毕之后，我就获取到了一个有数据的布局对象
        //此时我们只要把布局对象dl返回出去就可以了
        //其实就是因为在Item当中，最外层的就是这个dl布局对象
        return dl;
    }
}

