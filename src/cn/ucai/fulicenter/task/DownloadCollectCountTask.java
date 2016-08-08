package cn.ucai.fulicenter.task;

import android.content.Context;
import android.util.Log;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadCollectCountTask {
    private static final String TAG = DownloadCollectCountTask.class.getSimpleName();
    String userName;
    Context mContext;

    public DownloadCollectCountTask(Context context, String userName) {
        mContext=context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,userName)
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean msg) {
                        Log.e(TAG,"msg="+msg);
                        if (msg!=null) {
                            if (msg.isSuccess()) {
                                FuLiCenterApplication.getInstance().setCollectCount(Integer.valueOf(msg.getMsg()));
                            } else {
                                FuLiCenterApplication.getInstance().setCollectCount(0);
                            }
                        }
                    }
                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }
}
