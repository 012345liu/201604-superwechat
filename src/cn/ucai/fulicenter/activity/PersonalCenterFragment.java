package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.UserUtils;

/**
 * Created by sks on 2016/8/7.
 */
public class PersonalCenterFragment extends Fragment {
    public static final String TAG = PersonalCenterFragment.class.getSimpleName();
    Context mContext;
    private int[] pic_path = {R.drawable.order_list1, R.drawable.order_list2,
            R.drawable.order_list3, R.drawable.order_list3, R.drawable.order_list4, R.drawable.order_list5};
    TextView mtvUserName,mtvCollectCount,mtvSettings;
    ImageView mivMessage,mivUserAvatar;
    LinearLayout mLayoutCenterCollect;
    RelativeLayout mLayoutCenterUserInfo;
    int mCollectCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_personal_center, null);
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        MyClickListener listener = new MyClickListener();
        mLayoutCenterUserInfo.setOnClickListener(listener);
        mtvSettings.setOnClickListener(listener);
        mLayoutCenterCollect.setOnClickListener(listener);

    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (DemoHXSDKHelper.getInstance().isLogined()) {
                switch (v.getId()) {
                    case R.id.tv_center_settings:
                    case R.id.center_user_info:
                        startActivity(new Intent(mContext, SettingsActivity.class));
                        break;
                    case R.id.layout_center_collect:
                        startActivity(new Intent(mContext,CollectActivity.class));
                        break;
                }

            } else {
                Log.e(TAG,"not Logined...");
            }

        }
    }

    private void initData() {
        mCollectCount= FuLiCenterApplication.getInstance().getCollectCount();
        mtvCollectCount.setText(""+mCollectCount);
        if (DemoHXSDKHelper.getInstance().isLogined()){
            UserAvatar user = FuLiCenterApplication.getInstance().getUser();
            Log.e(TAG,"user="+user);
            UserUtils.setCurrentUserAvatar(mContext,mivUserAvatar);
            UserUtils.setAppCurrentUserNick(mtvUserName);
        }

    }

    private void initView(View layout) {
        mivUserAvatar = (ImageView) layout.findViewById(R.id.iv_user_avatar);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tv_collect_count);
        mtvSettings = (TextView) layout.findViewById(R.id.tv_center_settings);
        mtvUserName = (TextView) layout.findViewById(R.id.tv_user_name);
        mLayoutCenterCollect = (LinearLayout) layout.findViewById(R.id.layout_center_collect);
        mivMessage = (ImageView) layout.findViewById(R.id.iv_personal_center_msg);
        mLayoutCenterUserInfo = (RelativeLayout) layout.findViewById(R.id.center_user_info);
        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        GridView gvOrderList = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> order1 = new HashMap<>();
        order1.put("order", R.drawable.order_list1);
        data.add(order1);
        HashMap<String, Object> order2 = new HashMap<>();
        order2.put("order", R.drawable.order_list2);
        data.add(order2);
        HashMap<String, Object> order3 = new HashMap<>();
        order3.put("order", R.drawable.order_list3);
        data.add(order3);
        HashMap<String, Object> order4 = new HashMap<>();
        order4.put("order", R.drawable.order_list4);
        data.add(order4);
        HashMap<String, Object> order5 = new HashMap<>();
        order5.put("order", R.drawable.order_list5);
        data.add(order5);
        SimpleAdapter adapter = new SimpleAdapter(mContext, data, R.layout.simple_adapter,
                new String[]{"order"}, new int[]{R.id.iv_order});
        gvOrderList.setAdapter(adapter);
    }

    class UpdateCollectCount extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = FuLiCenterApplication.getInstance().getCollectCount();
            Log.e(TAG,"count="+count);
            mtvCollectCount.setText(String.valueOf(count));
        }
    }
    UpdateCollectCount mReceiver;
    private void updateCollectCountListener() {
        mReceiver = new UpdateCollectCount();
        IntentFilter filter = new IntentFilter("update_collect");
        mContext.registerReceiver(mReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }
}
