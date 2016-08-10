package cn.ucai.fulicenter.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private  static  final  String TAG=CartFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    List<CartBean> mCartList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager gm;
    CartAdapter  mCartAdapter;
    int pageId=1;
    TextView tvRefreshing;
    int action=I.ACTION_DOWNLOAD;

    TextView tvSumPrice;
    TextView tvSavePrice;
    TextView tvBuy;

    UpdateCartReceiver mReceiver;

    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext= (FuliCenterMainActivity) getContext();
        View view = View.inflate(mContext,R.layout.fragment_cart, null);
        mCartList=new ArrayList<CartBean>();
        mCartList=new ArrayList<>();
        initView(view);
        setListener();
        return view;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        setUpdateCartListener();

    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPositon;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a=RecyclerView.SCROLL_STATE_DRAGGING;
                int b=RecyclerView.SCROLL_STATE_IDLE;
                int c=RecyclerView.SCROLL_STATE_SETTLING;
                Log.e(TAG,"newState="+newState);
                if (newState==RecyclerView.SCROLL_STATE_IDLE
                        &&lastItemPositon==mCartAdapter.getItemCount()-1){
                    if (mCartAdapter.isMore()){
                        action=I.ACTION_PULL_UP;
                        pageId+=I.PAGE_ID_DEFAULT;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f=gm.findFirstVisibleItemPosition();
                int l=gm.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+",l="+l);
                lastItemPositon=gm.findLastVisibleItemPosition();
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action=I.ACTION_PULL_DOWN;
                tvRefreshing.setVisibility(View.VISIBLE);
                pageId=1;
                initData();
            }
        });
    }

    private void initData() {
        List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        mCartList.clear();
        mCartList.addAll(cartList);
        tvRefreshing.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mCartAdapter.setMore(true);
        if (mCartList!=null&&mCartList.size()>0){
            Log.e(TAG,"mCartList.size="+mCartList.size());
            if (action==I.ACTION_DOWNLOAD||action==I.ACTION_PULL_DOWN){
                mCartAdapter.initItem(mCartList);
            }else {
                mCartAdapter.addItem(mCartList);
            }
            if (mCartList.size()<I.PAGE_SIZE_DEFAULT){
                mCartAdapter.setMore(false);
            }
            sumPrice();
        }else {
            mCartAdapter.setMore(false);
        }
    }
    private  void findViewGoodList(OkHttpUtils2.OnCompleteListener<String> listener){
        final  OkHttpUtils2<String> utils=new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(String.class)
                .execute(listener);
    }

    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow

        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_cart);
        gm=new GridLayoutManager(mContext,1);
        gm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gm);
        mCartAdapter=new CartAdapter(mContext,mCartList);
        mRecyclerView.setAdapter(mCartAdapter);
        tvRefreshing= (TextView) view.findViewById(R.id.tv_refresh_hint);
        tvBuy = (TextView) view.findViewById(R.id.tv_cart_buy);
        tvSavePrice = (TextView) view.findViewById(R.id.tv_cart_save_price);
        tvSumPrice = (TextView) view.findViewById(R.id.tv_cart_sum_price);

    }
    public void sumPrice(){
        if (mCartList != null && mCartList.size() > 0) {
            int sumPrice=0;
            int rankPrice=0;
            for (CartBean cart : mCartList) {
                GoodDetailsBean goods = cart.getGoods();
                if (goods != null && cart.isChecked()) {
                    sumPrice += convertPrice(goods.getCurrencyPrice()) * cart.getCount();
                    rankPrice += convertPrice(goods.getRankPrice()) * cart.getCount();
                }
            }
            tvSumPrice.setText("合计：￥"+sumPrice);
            tvSavePrice.setText("节省：￥" + (sumPrice - rankPrice));
        } else {
            tvSumPrice.setText("合计：￥00.00");
            tvSavePrice.setText("节省：￥00.00");
        }
    }
    private  int convertPrice(String  price){
        price=price.substring(price.indexOf("￥")+1);
        return Integer.valueOf(price);
    }
    class UpdateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
          initData();
        }
    }

    private void setUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        mContext.registerReceiver(mReceiver, filter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver!=null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }





}
