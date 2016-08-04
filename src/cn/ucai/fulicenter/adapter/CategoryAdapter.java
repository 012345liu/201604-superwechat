package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/8/4.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;

    public CategoryAdapter(Context mContext, List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mContext = mContext;
        this.mGroupList = new ArrayList<CategoryGroupBean>();
        this.mGroupList.addAll(mGroupList);
        this.mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        this.mChildList.addAll(mChildList);
    }

    @Override
    public int getGroupCount() {
        return mGroupList!=null?mGroupList.size():0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        if(mGroupList!=null) return mGroupList.get(groupPosition);
        return null;
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        if(mGroupList.get(groupPosition)!=null&&mChildList.get(groupPosition).get(childPosition)!=null)
            return mChildList.get(groupPosition).get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder=null;
        if (convertView == null) {
            convertView= View.inflate(mContext, R.layout.item_category_group, null);
            holder = new GroupViewHolder();
            holder.ivGroupThumb = (ImageView) convertView.findViewById(R.id.iv_group_thumb);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            holder.ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        ImageUtils.setGroupCategoryImag(mContext, holder.ivGroupThumb, group.getImageUrl());
        holder.tvGroupName.setText(group.getName());
        if (isExpanded) {
            holder.ivIndicator.setImageResource(R.drawable.expand_off);
        } else {
            holder.ivIndicator.setImageResource(R.drawable.expand_on);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_category_child, null);
            holder = new ChildViewHolder();
            holder.layoutCategoryChild = (RelativeLayout) convertView.findViewById(R.id.layout_category_child);
            holder.tvCategoryChildＮame = (TextView) convertView.findViewById(R.id.tv_category_child_name);
            holder.ivCategoryChildThumb = (ImageView) convertView.findViewById(R.id.iv_category_child_thumb);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        if (child != null) {
            ImageUtils.setChildCategoryImag(mContext, holder.ivCategoryChildThumb, child.getImageUrl());
            holder.tvCategoryChildＮame.setText(child.getName());
            holder.layoutCategoryChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, CategoryChildActivity.class)
                    .putExtra(I.CategoryChild.CAT_ID,child.getId()));
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void addAll(List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mGroupList.clear();
        this.mGroupList.addAll(mGroupList);
        this.mChildList.clear();
        this.mChildList.addAll(mChildList);
        notifyDataSetChanged();
    }

    class GroupViewHolder {
        ImageView ivGroupThumb;
        TextView tvGroupName;
        ImageView ivIndicator;
    }


    class ChildViewHolder {
        RelativeLayout layoutCategoryChild;
        ImageView ivCategoryChildThumb;
        TextView tvCategoryChildＮame;
    }
}
