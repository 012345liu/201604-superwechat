package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/7/21.
 */
public class UpdateCartTask {
    private static final String TAG = UpdateCartTask.class.getSimpleName();
    CartBean mCart;
    Context mContext;

    public UpdateCartTask(Context context, CartBean cart) {
        mContext=context;
        this.mCart = cart;
    }

    public void execute() {
        final List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        if (cartList.contains(mCart)) {
            if (mCart.getCount() > 0) {//更新
                updateCart(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null&&result.isSuccess()) {
                            cartList.set(cartList.indexOf(mCart), mCart);
                            mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

            } else {//删除

            }
        } else {//添加

        }
    }
    private void updateCart(OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        final OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_UPDATE_CART)
                .addParam(I.Cart.ID,String.valueOf(mCart.getId()))
                .addParam(I.Cart.COUNT,String.valueOf(mCart.getCount()))
                .addParam(I.Cart.IS_CHECKED,String.valueOf(mCart.isChecked()))
                .targetClass(MessageBean.class)
                .execute(listener);

    }
}
