package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/1.
 */
public class CartAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = CartAdapter.class.getSimpleName();
    Context mContext;
    List<CartBean> mCartList;
    CartViewHolder mCartViewHolder;
    boolean isMore;
    boolean isChecked;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context context, List<CartBean> list) {
        mContext = context;
        mCartList = list;
        mCartList.addAll(list);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        ViewHolder holder= new CartViewHolder(inflater.inflate(R.layout.item_cart, null, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CartViewHolder) {
            mCartViewHolder = (CartViewHolder) holder;
            final CartBean cart = mCartList.get(position);
            ImageUtils.setGoodThumb(mContext,mCartViewHolder.ivGoodThumb,cart.getGoods().getGoodsThumb());
            mCartViewHolder.tvGoodName.setText(cart.getGoods().getGoodsName());
            mCartViewHolder.tvGoodCount.setText("("+cart.getCount()+")");
            mCartViewHolder.tvGoodPrice.setText(cart.getGoods().getCurrencyPrice());
            mCartViewHolder.cbChecked.setChecked(isChecked);
            mCartViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                    .putExtra(D.GoodDetails.KEY_GOODS_ID,cart.getGoodsId()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCartList !=null? mCartList.size():0;
    }

    public void initData(ArrayList<CartBean> list) {
        if (mCartList !=null) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends ViewHolder {
        RelativeLayout layout;
        CheckBox cbChecked;
        TextView tvGoodName,tvGoodCount,tvGoodPrice;
        ImageView ivGoodThumb,ivDelete,ivAdd;
        public CartViewHolder(View itemView) {
            super(itemView);
            layout= (RelativeLayout) itemView.findViewById(R.id.layout_cart_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.iv_cart_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_cart_delete);
            ivAdd = (ImageView) itemView.findViewById(R.id.iv_cart_add);
            tvGoodCount = (TextView) itemView.findViewById(R.id.tv_cart_good_count);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_cart_good_prices);
            cbChecked = (CheckBox) itemView.findViewById(R.id.cb_cart_check);
        }
    }
}
