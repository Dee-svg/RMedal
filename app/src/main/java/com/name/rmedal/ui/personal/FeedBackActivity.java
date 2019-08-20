package com.name.rmedal.ui.personal;

import android.os.Bundle;
import android.view.View;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.LogUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.LabelsView;
import com.veni.tools.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.feedback_labels)
    LabelsView feedbackLabels;


    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initPresenter() {

    }

    private String selectStr = "";

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle(R.string.nav_feedback);

        List<String> labellist = new ArrayList<>();
        labellist.add("意见111111111111");
        labellist.add("意见2222");
        labellist.add("意见3333333333333333");
        labellist.add("意见44444444");
        labellist.add("意见55555555555555555555");
        feedbackLabels.setLabels(labellist); //直接设置一个字符串数组就可以了。
        feedbackLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
            }
        });
        feedbackLabels.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                //label是被点击的标签，labelText是标签的文字，isSelect是是否选中，position是标签的位置。
                LogUtils.eTag(TAG, "labelText---" + labelText, "isSelect---" + isSelect);
                selectStr = isSelect ? labelText : "";
                if (isSelect) {
                    ToastTool.success("选中--" + labelText);
                } else {
                    ToastTool.error("取消选中--" + labelText);
                }
            }
        });
    }

    @OnClick({R.id.feedback_btn})
    public void onViewClicked(View view) {
        if (antiShake.check(view.getId())) return;
        switch (view.getId()) {
            case R.id.feedback_btn:
                ToastTool.success("选中--" + selectStr);
                break;
        }
    }
}
