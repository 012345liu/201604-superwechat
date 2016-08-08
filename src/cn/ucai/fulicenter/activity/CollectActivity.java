package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.DisplayUtils;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/8/2.
 */
public class CollectActivity extends BaseActivity {
    static final String TAG = CollectActivity.class.getSimpleName();
    CollectActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<CollectBean> mCollectList;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mAdapter;
    int mPageId=0;
    TextView tvHint;
    int action=I.ACTION_DOWNLOAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mContext = this;
        mCollectList=new ArrayList<>();
        initView();
        initData();
        setListener();
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
                        mPageId += I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mGridLayoutManager.findFirstVisibleItemPosition();
                int l = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "f=" + f+", l=" + l);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();

            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                mPageId=0;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void initData() {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        Log.e(TAG, "userName=" + userName);
        if (userName ==null) {
            finish();
        }
        findCollectList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
            @Override
            public void onSuccess(CollectBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "result=" + result);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterText(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<CollectBean> collectList = Utils.array2List(result);
                    if (action == I.ACTION_PULL_UP) {
                        mAdapter.addData(collectList);
                    }
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(collectList);
                    }
                    if (collectList.size() < I.PAGE_SIZE_DEFAULT) {
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

    private void findCollectList(OkHttpUtils2.OnCompleteListener<CollectBean[]> listener) {
        OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID, String.valueOf(mPageId))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CollectBean[].class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"收藏的宝贝");
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_yellow,
                R.color.google_red, R.color.google_green);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_collect);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(mContext, mCollectList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView)findViewById(R.id.tv_refresh_hint);
    }


}
