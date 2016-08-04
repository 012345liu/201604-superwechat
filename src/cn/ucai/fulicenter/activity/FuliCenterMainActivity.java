package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity{

    public static final String TAG = FuliCenterMainActivity.class.getName();

    RadioButton rbNewGood,rbBoutique,rbCategory,rbCart,rbPersonalCenter;
    TextView tvCartHint;
    RadioButton[] mrbTabs;
    int index=0;
    int currentIndedx;
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    Fragment[] mFragments;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initFragment();
        initView();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment=new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mFragments = new Fragment[5];
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
    }

    private void initView() {
        rbNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        rbBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        rbCart = (RadioButton) findViewById(R.id.layout_cart);
        rbCategory = (RadioButton) findViewById(R.id.layout_category);
        rbPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mrbTabs = new RadioButton[5];
        mrbTabs[0]=rbNewGood;
        mrbTabs[1]=rbBoutique;
        mrbTabs[2]=rbCategory;
        mrbTabs[3]=rbCart;
        mrbTabs[4]=rbPersonalCenter;

        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().
                add(cn.ucai.fulicenter.R.id.fragment_container, mNewGoodFragment)
                .add(cn.ucai.fulicenter.R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment)
                .hide(mBoutiqueFragment)
                .hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index=0;
                break;
            case R.id.layout_boutique:
                index=1;
                break;
            case R.id.layout_category:
                index=2;
                break;
            case R.id.layout_cart:
                index=3;
                break;
            case R.id.layout_personal_center:
                index=4;
                break;
        }

        Log.e(TAG,"INDEX===="+index+"currentIndex====="+currentIndedx);
        if (currentIndedx != index) {
            setCurrentIndexStatus(index);
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentIndedx]);
            if (!mFragments[index].isAdded()) {
                trx.add(cn.ucai.fulicenter.R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            currentIndedx = index;
        }
        /*if (index!=currentIndedx) {
            setCurrentIndexStatus(index);
            currentIndedx = index;
        }*/


    }

    private void setCurrentIndexStatus(int index) {
        for (int i=0;i<mrbTabs.length;i++) {
            if (index ==i) {
                mrbTabs[i].setChecked(true);
            } else {
                mrbTabs[i].setChecked(false);

            }
        }
    }
}
