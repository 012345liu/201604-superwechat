package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.DisplayUtils;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/8/2.
 */
public class CategoryChildActivity extends BaseActivity {
    static final String TAG = CategoryChildActivity.class.getSimpleName();
    CategoryChildActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mGoodList;
    GridLayoutManager mGridLayoutManager;
    GoodAdapter mAdapter;
    int mPageId=0;
    TextView tvHint;
    Button btnSortPrice;
    Button btnSortAddTime;
    boolean mSortPriceArc;
    boolean mSortAddTimeArc;
    int sortBy;
    int mCatId=0;
    int action=I.ACTION_DOWNLOAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mContext = this;
        mGoodList=new ArrayList<>();
        sortBy=I.SORT_BY_ADDTIME_DESC;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStatusChangedListener listener=new SortStatusChangedListener();
        btnSortAddTime.setOnClickListener(listener);
        btnSortPrice.setOnClickListener(listener);
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
        mCatId=getIntent().getIntExtra(I.CategoryChild.CAT_ID,0);
        if (mCatId < 0) {
            finish();
        }
        findBoutiqueChildList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "result=" + result);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterText(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<NewGoodBean> goodBeanArrayList = Utils.array2List(result);
                    if (action == I.ACTION_PULL_UP) {
                        mAdapter.addData(goodBeanArrayList);
                    }
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(goodBeanArrayList);
                    }
                    if (goodBeanArrayList.size() < I.PAGE_SIZE_DEFAULT) {
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

    private void findBoutiqueChildList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(mCatId))
                .addParam(I.PAGE_ID, String.valueOf(mPageId))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView() {
        //String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBack(mContext);
        btnSortAddTime = (Button) findViewById(R.id.btn_add_time_sort);
        btnSortPrice = (Button) findViewById(R.id.btn_price_sort);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_category);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_yellow,
                R.color.google_red, R.color.google_green);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_category);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView)findViewById(R.id.tv_refresh_hint);
    }

    class SortStatusChangedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_price_sort:
                    if (mSortPriceArc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                    }
                    mSortPriceArc = !mSortPriceArc;
                    break;
                case R.id.btn_add_time_sort:
                    if (mSortAddTimeArc) {
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                    } else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                    }
                    mSortAddTimeArc = !mSortAddTimeArc;
                    break;
            }
            mAdapter.setSortBy(sortBy);

        }
    }
}
