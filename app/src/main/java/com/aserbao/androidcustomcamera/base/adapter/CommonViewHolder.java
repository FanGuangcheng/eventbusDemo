package com.aserbao.androidcustomcamera.base.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fgc.eventbusdemo.R;

import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2020-01-15 17:32
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.base.adapter
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    @BindView(R.id.btn_item_common)
    Button mBtnItemCommon;
    private Activity mActivity;
    private BaseRecyclerBean mClassBean;
    public CommonViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setDataSource(BaseRecyclerBean classBean,Activity activity){
        mActivity = activity;
        mClassBean = classBean;
        mBtnItemCommon.setText(classBean.getName());
        mBtnItemCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, mClassBean.getClazz()));
            }
        });
    }
}
