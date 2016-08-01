package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadContactListTask {
    private static final String TAG = DownloadContactListTask.class.getSimpleName();
    String userName;
    Context mContext;

    public DownloadContactListTask(Context context,String userName) {
        mContext=context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        Log.e(TAG,"result="+result);
                        List<UserAvatar> list= (List<UserAvatar>) result.getRetData();
                        if (list!=null&&list.size()>0) {
                            Log.e(TAG,"list.size="+list.size());
                            FuLiCenterApplication.getInstance().setList(list);
                            mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                            Map<String, UserAvatar> userMap = FuLiCenterApplication.getInstance().getUserMap();
                            for (UserAvatar user : list) {
                                userMap.put(user.getMUserName(), user);
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
