package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by sks on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity{
    static final String TAG = GoodDetailsActivity.class.getSimpleName();
    GoodDetailsActivity mContext;
    TextView tvGoodEnglishName,tvGoodName,tvGoodPriceShop,tvGoodPriceCurrent,tvCartCount;
    ImageView ivShare,ivCollect,ivCart;
    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;
    int goodId;
    GoodDetailsBean mGoodDetails;
    boolean isCollect;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        MyOnClickListener listener = new MyOnClickListener();
        ivCollect.setOnClickListener(listener);
    }

    private void initData() {
        goodId=getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "goodId=" + goodId);
        if (goodId>0) {
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
                    Log.e(TAG,"result="+result);
                    if (result != null) {
                        mGoodDetails=result;
                        showGoodDetails();
                    }
                }
                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                    Toast.makeText(mContext, "获取商品详情失败", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            finish();
            Toast.makeText(mContext, "获取商品详情失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetails.getGoodsName());
        tvGoodPriceShop.setText(mGoodDetails.getShopPrice());
        tvGoodPriceCurrent.setText(mGoodDetails.getCurrencyPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,getAlbumImageUrl(),getAlbumImageSize());
        wvGoodBrief.loadDataWithBaseURL(null,mGoodDetails.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if (mGoodDetails.getProperties()!=null&&mGoodDetails.getProperties().length>0) {
            AlbumsBean[] albums = mGoodDetails.getProperties()[0].getAlbums();
            albumImageUrl = new String[albums.length];
            for (int i=0;i<albumImageUrl.length;i++) {
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize() {
        if (mGoodDetails.getPromotePrice()!=null&&mGoodDetails.getPromotePrice().length()>0) {
            return mGoodDetails.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(goodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBack(mContext);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        tvCartCount = (TextView) findViewById(R.id.tv_good_count);
        tvGoodEnglishName = (TextView) findViewById(R.id.tv_good_name_english);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        tvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
    }

    private void initCollectStatus() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
            utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME,userName)
                    .addParam(I.Collect.GOODS_ID,String.valueOf(goodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG, "initCollectStatus,result=" + result);
                            if (result != null && result.isSuccess()) {
                                isCollect = true;
                            } else {
                                isCollect = false;
                            }
                            updateCollectStatus();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "error=" + error);
                        }
                    });
        }
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_good_collect:
                    goodCollect();
                    break;

            }
        }
    }


    private void goodCollect() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (isCollect) {//取消收藏
                OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
                utils2.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID, String.valueOf(goodId))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                Log.e(TAG, "goodCollect,result=" + result);
                                if (result != null && result.isSuccess()) {
                                    isCollect = false;
                                    new DownloadCollectCountTask(mContext, FuLiCenterApplication.getInstance().getUserName()).execute();
                                    sendStickyBroadcast(new Intent("update_collect_list"));
                                    
                                } else {
                                    Log.e(TAG, "delete fail");
                                }
                                updateCollectStatus();
                                Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "error=" + error);

                            }
                        });
            } else {//添加收藏
                OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
                utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodDetails.getGoodsId()))
                        .addParam(I.Collect.ADD_TIME,String.valueOf(mGoodDetails.getAddTime()))
                        .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetails.getGoodsEnglishName())
                        .addParam(I.Collect.GOODS_IMG,mGoodDetails.getGoodsImg())
                        .addParam(I.Collect.GOODS_THUMB,mGoodDetails.getGoodsThumb())
                        .addParam(I.Collect.GOODS_NAME,mGoodDetails.getGoodsName())
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                Log.e(TAG, "result=" + result);
                                if (result != null && result.isSuccess()) {
                                    isCollect = true;
                                    new DownloadCollectCountTask(mContext, FuLiCenterApplication.getInstance().getUserName()).execute();
                                    sendStickyBroadcast(new Intent("update_collect_list"));
                                }
                                updateCollectStatus();
                                Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "error=" + error);
                            }
                        });
            }
        } else {
            startActivity(new Intent(mContext,LoginActivity.class));
        }
    }

    private void updateCollectStatus() {
        if (isCollect) {
            ivCollect.setImageResource(R.drawable.bg_collect_out);
        } else {
            ivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }
}
