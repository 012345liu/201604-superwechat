package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.DisplayUtils;

/**
 * Created by sks on 2016/8/11.
 */
public class BuyActivity extends BaseActivity{
    BuyActivity mContext;
    EditText orderPhone;
    EditText orderUserName;
    EditText orderStreet;
    Spinner orderProvince;
    Button mbtBuy;
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_order);
        initView();
        setListener();
    }

    private void setListener() {
        mbtBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"填写收货地址");
        orderProvince = (Spinner) findViewById(R.id.sp_order_province);
        orderPhone = (EditText) findViewById(R.id.ed_order_phone);
        orderUserName = (EditText) findViewById(R.id.ed_order_name);
        orderStreet = (EditText) findViewById(R.id.ed_order_street);
        mbtBuy = (Button) findViewById(R.id.btn_buy);
    }
}
