package com.example.transferfile.MyToastutils;

//import com.example.transferfile.ResourceTable;
//import ohos.agp.components.Component;
//import ohos.agp.components.DirectionalLayout;
//import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
//import ohos.agp.utils.LayoutAlignment;
//import ohos.agp.window.dialog.ToastDialog;
//import ohos.app.Context;

import com.example.transferfile.ResourceTable;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.LayoutScatter;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;

import java.util.concurrent.RecursiveTask;

public class Toastutils {
    public static void show(Context context, String str){
        //把xml文件加载到内存，
        //获取一个布局。
        DirectionalLayout dl =(DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_mytoast, null, false);

        //获取当前布局对象的文本组件。
        Text msg=(Text) dl.findComponentById(ResourceTable.Id_text1);

        //把需要提示的信息设置到文本组件当中。
        msg.setText(str);

        ToastDialog ts = new ToastDialog(context);

        //设置大小，默认包裹内容。
        ts.setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT,DirectionalLayout.LayoutConfig.MATCH_CONTENT);
        //设置出现时间。
        ts.setDuration(1000);

        //设置对齐方式。
        ts.setAlignment(LayoutAlignment.BOTTOM);

        //设置弹框偏移量。
        ts.setOffset(0,200);

        //关联ts和dl。
        ts.setContentCustomComponent(dl);

        ts.show();

    }
}
