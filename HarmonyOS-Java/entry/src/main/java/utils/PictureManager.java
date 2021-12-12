package utils;

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.resultset.ResultSet;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PictureManager {
    private static final String TAG = PictureManager.class.getSimpleName();

    private List<Uri> imagePathElements = new ArrayList<>();

    private Context context;

    /**
     * The construction method of this class
     *
     * @param context Context
     */
    public PictureManager(Context context) {
        this.context = context;
        loadFromMediaLibrary(context);
    }

    private void loadFromMediaLibrary(Context context) {
        Uri remoteUri = AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI;
        //Video.Media.EXTERNAL_DATA_ABILITY_URI;
        DataAbilityHelper helper = DataAbilityHelper.creator(context, remoteUri, false);
        try {
            ResultSet resultSet = helper.query(remoteUri, null, null);
            utils.LogUtil.info(TAG, "The result size: " + resultSet.getRowCount());
            processResult(resultSet);
            resultSet.close();
        } catch (DataAbilityRemoteException e) {
            utils.LogUtil.error(TAG, "Query system media failed.");
        } finally {
            helper.release();
        }
    }

    private void processResult(ResultSet resultSet) {
        while (resultSet.goToNextRow()) {
            String path = resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.DATA));
            String title = resultSet.getString(resultSet.getColumnIndexForName(AVStorage.AVBaseColumns.TITLE));
            String id = resultSet.getString(resultSet.getColumnIndexForName(AVStorage.Images.Media.ID));
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(id));

            utils.LogUtil.info(TAG, "The title is: " + title);
            utils.LogUtil.info(TAG, "The path is: " + path);
            utils.LogUtil.info(TAG, "The id is: " + id);
            utils.LogUtil.info(TAG, "The uri is: " + uri);
            imagePathElements.add(uri);
        }
    }

    public List<Uri> getimageElements() {
        utils.LogUtil.info(TAG, "The size is: " + imagePathElements.size());
        return imagePathElements;
    }
}
