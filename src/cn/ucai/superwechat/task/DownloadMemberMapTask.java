package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadMemberMapTask {
    private static final String TAG = DownloadMemberMapTask.class.getSimpleName();
    String hxid;
    Context mContext;

    public DownloadMemberMapTask(Context context, String hxid) {
        mContext=context;
        this.hxid = hxid;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        Log.e(TAG,"result="+result);
                        List<MemberUserAvatar> list= (List<MemberUserAvatar>) result.getRetData();
                        if (list!=null&&list.size()>0) {
                            Log.e(TAG,"list.size="+list.size());
                            Map<String, HashMap<String, MemberUserAvatar>> memberMap = SuperWeChatApplication.getInstance().getMemberMap();
                            if (!memberMap.containsKey(hxid)) {
                                memberMap.put(hxid,new HashMap<String, MemberUserAvatar>());
                            }
                            Map<String, MemberUserAvatar> hxidMember = memberMap.get(hxid);
                            for (MemberUserAvatar user : list) {
                                hxidMember.put(user.getMUserName(), user);
                            }
                            mContext.sendStickyBroadcast(new Intent("update_member_list"));

                        }


                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);

                    }
                });

    }
}
