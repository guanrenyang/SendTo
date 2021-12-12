package com.example.transferfile;

//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.ability.DataAbilityHelper;
//import ohos.aafwk.ability.DataAbilityRemoteException;
//import ohos.aafwk.content.Intent;
import ohos.data.resultset.ResultSet;
//import ohos.data.rdb.ValuesBucket;
//import ohos.data.dataability.DataAbilityPredicates;
//import ohos.hiviewdfx.HiLog;
//import ohos.hiviewdfx.HiLogLabel;
//import ohos.rpc.MessageParcel;
//import ohos.utils.net.Uri;
//import ohos.utils.PacMap;
//
//import java.io.*;
//import java.util.ArrayList;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.MessageParcel;
import ohos.utils.PacMap;
import ohos.utils.net.Uri;

import java.io.*;
import java.util.ArrayList;

public class SearchFile extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    ArrayList<String> Filename = new ArrayList<String>();
    String filename ;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "SearchFile onStart");

//        filename= intent.getStringParam("filename");

        //创建help实例对象
        DataAbilityHelper helper = DataAbilityHelper.creator(this);

//        try {
//
//            //通过文件描述符 读取指定uri的文件 ，“r”(读), “w”(写), “rw”(读写)，“wt”(覆盖写)，“wa”(追加写)，“rwt”(覆盖写且可读)
//
//            FileDescriptor fileDescriptor = helper.openFile(Uri.parse("dataability://com.example.transferfile.SearchFile"),"r");
//
//            //获取文件输入流
//
//            FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
//
//        } catch (DataAbilityRemoteException e) {
//
//            e.printStackTrace();
//
//        } catch (FileNotFoundException e) {
//
//            e.printStackTrace();
//
//        }
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        return null;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "SearchFile insert");
        return 999;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        return 0;
    }

    //返回要操作的文件

    @Override
    public FileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        MessageParcel messageParcel = MessageParcel.obtain();
        File file = new File(uri.getDecodedPathList().get(0)); //get(0)是获取URI完整字段中查询参数字段。
        if (mode == null || !"rw".equals(mode)) {
            file.setReadOnly();
        }
        FileInputStream fileIs = new FileInputStream(file);
        FileDescriptor fd = null;
        try {
            fd = fileIs.getFD();
        } catch (IOException e) {
            HiLog.info(LABEL_LOG, "failed to getFD");
        }

        // 绑定文件描述符
        return MessageParcel.dupFileDescriptor(fd);
    }

//    private void openUriFile(Uri uti){
//        Uri uri = Uri.parse("dataability://com.example.transferfile.SearchFile");
//        File file = new File(uri.getDecodedPath());
//        file.setReadable(true);
//        file.setWritable(true);
//        try{
//            //文件输入流
//
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            //得到文件描述符
//
//            FileDescriptor fileDescriptor = fileInputStream.getFD();
//
//            //绑定文件描述符
//
//            MessageParcel.dupFileDescriptor(fileDescriptor);
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}