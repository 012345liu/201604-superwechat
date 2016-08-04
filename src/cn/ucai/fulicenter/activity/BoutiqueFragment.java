package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/8/3.
 */
public class BoutiqueFragment extends Fragment{
    static final String TAG = BoutiqueFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    List<BoutiqueBean> mBoutiqueList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    BoutiqueAdapter mAdapter;
    TextView tvHint;
    int action= I.ACTION_DOWNLOAD;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext, R.layout.fragment_boutique, null);
        mBoutiqueList = new ArrayList<BoutiqueBean>();
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a=RecyclerView.SCROLL_STATE_DRAGGING;
                int b = RecyclerView.SCROLL_STATE_IDLE;
                int c = RecyclerView.SCROLL_STATE_SETTLING;
                Log.e(TAG, "newState=" + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastItemPosition==mAdapter.getItemCount()-1) {
                    if (mAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mLinearLayoutManager.findFirstVisibleItemPosition();
                int l = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "f=" + f+", l=" + l);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void initData() {
        findGoodList(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "result=" + result);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterText(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<BoutiqueBean> boutiqueList = Utils.array2List(result);
                    if (action == I.ACTION_PULL_UP) {
                        mAdapter.addData(boutiqueList);
                    }
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(boutiqueList);
                    }
                    if (boutiqueList.size() < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));

                    }
                } else {
                    mAdapter.setMore(false);
                    mAdapter.setFooterText(getResources().getString(R.string.no_more));
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"error="+error);
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                Toast.makeText(mContext,error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findGoodList(OkHttpUtils2.OnCompleteListener<BoutiqueBean[]> listener) {
        OkHttpUtils2<BoutiqueBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(listener);
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_boutique);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_yellow,
                R.color.google_red, R.color.google_green);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BoutiqueAdapter(mContext, mBoutiqueList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
    }

}
