package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownloadCartListTask {
    private static final String TAG = DownloadCartListTask.class.getSimpleName();
    String userName;
    Context mContext;

    public DownloadCartListTask(Context context, String userName) {
        mContext=context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<CartBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME,userName)
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] s) {
                        Log.e(TAG,"s="+s);
                        if (s!=null) {
                            ArrayList<CartBean> list = Utils.array2List(s);
                            List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                            for (final CartBean cart : cartList) {
                                if (!cartList.contains(cart)) {
                                    OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<>();
                                    utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                                            .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(cart.getGoodsId()))
                                            .targetClass(GoodDetailsBean.class)
                                            .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                                                @Override
                                                public void onSuccess(GoodDetailsBean result) {
                                                    cart.setGoods(result);
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.e(TAG, "error=" + error);
                                                }
                                            });
                                    cartList.add(cart);

                                } else {
                                    cartList.get(cartList.indexOf(cart)).setChecked(cart.isChecked());
                                    cartList.get(cartList.indexOf(cart)).setCount(cart.getCount());
                                }
                                mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                            }

                            Log.e(TAG,"cartList.size="+cartList.size());
                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });

    }
}