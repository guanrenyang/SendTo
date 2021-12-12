package com.example.transferfile;

//import com.example.transferfile.MyToastutils.Toastutils;
//import com.example.transferfile.slice.ChoosefileSlice;
//import com.example.transferfile.slice.TransferfileSlice;
//import ohos.aafwk.ability.Ability;
//import ohos.aafwk.ability.DataAbilityHelper;
//import ohos.aafwk.content.Intent;
//import ohos.data.DatabaseHelper;
//import ohos.data.dataability.DataAbilityUtils;
//import ohos.data.rdb.*;
//import ohos.data.resultset.ResultSet;
//import ohos.data.dataability.DataAbilityPredicates;
//import ohos.hiviewdfx.HiLog;
//import ohos.hiviewdfx.HiLogLabel;
//import ohos.utils.net.Uri;
//import ohos.utils.PacMap;
//
//import java.io.FileDescriptor;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.PacMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;

//上传文件数据库
public class UploadDataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    private RdbStore rdbStore;

    private StoreConfig storeConfig =StoreConfig.newDefaultConfig("namestore.db");

    private RdbOpenCallback rdbOpenCallback=new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            //创建数据表
            rdbStore.executeSql("CREATE TABLE IF NOT EXISTS names(nameoffile text )");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {

        }
    };
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "UploadDataAbility onStart");
        //storeconfig对象关联数据文件配置  namestore.db 就是数据存储文件
        //回调函数
        //传回callback创建数据表

        //初始化与数据库的连接
        DatabaseHelper helper = new DatabaseHelper(this);
        //rdbstore对象表示与数据库的连接。
        //通过此连接对象可以完成对数据表中数据的增删改查

        rdbStore = helper.getRdbStore(storeConfig,1,rdbOpenCallback);
    }

    //predicates代表封装查询条件
    //column代表返回的列
    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        //将传来的数据转换为适应表的数据
        RdbPredicates rdbPredicates= DataAbilityUtils.createRdbPredicates(predicates,"names");//转换成适应表的
        ResultSet rs = rdbStore.query(rdbPredicates,columns);
        if (rs == null) {
            HiLog.info(LABEL_LOG, "resultSet is null");
        }

        return rs;
    }

    //uri表示访问路径哪张表
    //value是参数容器
    //重写
    @Override
    public int insert(Uri uri, ValuesBucket value) {
        int i =-1;
        HiLog.info(LABEL_LOG, "UploadDataAbility insert");
        String path = uri.getLastPath();
        if("names".equalsIgnoreCase(path)){
            i = (int)rdbStore.insert("names",value);
        System.out.println("------------->插入up数据成功");
        }
        return i;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "names");
        int i = rdbStore.delete(rdbPredicates);
        return i;
//        return 0;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

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