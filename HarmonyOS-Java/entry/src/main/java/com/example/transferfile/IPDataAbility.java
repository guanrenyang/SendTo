package com.example.transferfile;

//import ohos.aafwk.ability.Ability;
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
//IP数据库


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

public class IPDataAbility extends Ability {

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    private RdbStore rdbStore;

    private StoreConfig storeConfig =StoreConfig.newDefaultConfig("theip.db");

    private RdbOpenCallback rdbOpenCallback=new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            //创建数据表
            rdbStore.executeSql("CREATE TABLE IF NOT EXISTS ips(theip text)");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {

        }
    };
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "IPDataAbility onStart");
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
            RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "ips");//转换成适应表的
            ResultSet rs = rdbStore.query(rdbPredicates, columns);
            if (rs == null) {
                HiLog.info(LABEL_LOG, "resultSet is null");
        }
            return rs;
//        else{
//            RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "devices");//转换成适应表的
//            ResultSet rs1 = rdbStore.query(rdbPredicates, columns);
//            if (rs1 == null) {
//                HiLog.info(LABEL_LOG, "resultSet is null");
//            }
//            return rs1;
//        }

    }

    //uri表示访问路径哪张表
    //value是参数容器
    //重写插入操作
    @Override
    public int insert(Uri uri, ValuesBucket value) {
            int i = -1;
            HiLog.info(LABEL_LOG, "IPDataAbility insert");
            String path = uri.getLastPath();
            if ("ips".equalsIgnoreCase(path)) {
                i = (int) rdbStore.insert("ips", value);
                System.out.println("--------------------------------------------------------------" +
                        "---------------------------------------------------------->IPdata数据更新");
            }
            return i;
//        else {
//            int i = -1;
//            HiLog.info(LABEL_LOG, "IPDataAbility insert");
//            String path = uri.getLastPath();
//            if ("devices".equalsIgnoreCase(path)) {
//                i = (int) rdbStore.insert("devices", value);
//            }
//            return i;
//        }
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        return 0;
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