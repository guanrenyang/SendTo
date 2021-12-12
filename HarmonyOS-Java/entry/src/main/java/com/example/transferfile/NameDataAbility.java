package com.example.transferfile;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.FileDescriptor;

public class NameDataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");


    private RdbStore rdbStore;

    private StoreConfig storeConfig =StoreConfig.newDefaultConfig("name.db");

    private RdbOpenCallback rdbOpenCallback=new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            rdbStore.executeSql("CREATE TABLE IF NOT EXISTS devices(device text)");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {

        }
    };

        @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "NameDataAbility onStart");
        //初始化与数据库的连接
        DatabaseHelper helper = new DatabaseHelper(this);
        //rdbstore对象表示与数据库的连接。
        //通过此连接对象可以完成对数据表中数据的增删改查

        rdbStore = helper.getRdbStore(storeConfig,1,rdbOpenCallback);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
            //将传来的数据转换为适应表的数据
            RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "devices");//转换成适应表的
            ResultSet rs = rdbStore.query(rdbPredicates, columns);
            if (rs == null) {
                HiLog.info(LABEL_LOG, "resultSet is null");
            }
            return rs;

    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "NameDataAbility insert");
            int i = -1;
            HiLog.info(LABEL_LOG, "NameDataAbility insert");
            String path = uri.getLastPath();
            if ("devices".equalsIgnoreCase(path)) {
                i = (int) rdbStore.insert("devices", value);
            }
            System.out.println("----------------------------------------" +
                    "--------------------------------------------->Namedata数据更新完毕");
            return i;
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