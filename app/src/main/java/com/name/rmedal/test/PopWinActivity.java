package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.LogTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.model.ActionItem;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.ToastTool;
import com.veni.tools.view.popupwindows.PopupImply;
import com.veni.tools.view.popupwindows.PopupSingleView;
import com.veni.tools.view.popupwindows.tools.PopupView;
import com.veni.tools.view.popupwindows.tools.PopupViewManager;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * popupwindows
 */

public class PopWinActivity extends BaseActivity implements PopupViewManager.TipListener{

    @BindView(R.id.popwin_title_view)
    TitleView popwinTitleView;
    @BindView(R.id.tv_imply)
    TextView mTvImply;
    @BindView(R.id.tv_definition)
    TextView mTvDefinition;
    @BindView(R.id.text_input_edit_text)
    TextInputEditText mTextInputEditText;
    @BindView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;
    @BindView(R.id.text_view_buttons_label)
    TextView mTextViewButtonsLabel;
    @BindView(R.id.button_above)
    Button mButtonAbove;
    @BindView(R.id.button_below)
    Button mButtonBelow;
    @BindView(R.id.button_left_to)
    Button mButtonLeftTo;
    @BindView(R.id.button_right_to)
    Button mButtonRightTo;
    @BindView(R.id.linear_layout_buttons_above_below)
    LinearLayout mLinearLayoutButtonsAboveBelow;
    @BindView(R.id.button_align_left)
    RadioButton mButtonAlignLeft;
    @BindView(R.id.button_align_center)
    RadioButton mButtonAlignCenter;
    @BindView(R.id.button_align_right)
    RadioButton mButtonAlignRight;
    @BindView(R.id.linear_layout_buttons_align)
    RadioGroup mLinearLayoutButtonsAlign;
    @BindView(R.id.text_view)
    TextView mTextView;
    @BindView(R.id.parent_layout)
    RelativeLayout mParentLayout;
    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;

    /**
     * 启动入口
     */
    public static void startAction(Context context, View view) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(PopWinActivity.class)
                .setView(view)
                .setActionString(AppConstant.TRANSITION_ANIMATION)
                .screenTransitAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_popwin;
    }

    @Override
    public void initPresenter() {

    }

    public static final String TIP_TEXT = "Tip";
    PopupViewManager mRxPopupViewManager;
    @PopupView.Align
    int mAlign = PopupView.ALIGN_CENTER;
    private PopupSingleView titlePopup;
    private PopupImply popupImply;//提示  一小时后有惊喜

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置启动动画对应的view
        View view = popwinTitleView.getTvTitle();
        ViewCompat.setTransitionName(view, AppConstant.TRANSITION_ANIMATION);
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, popwinTitleView);
        //设置返回点击事件
        popwinTitleView.setLeftFinish(context);
        //设置显示标题
        popwinTitleView.setTitle("PopWin");
        //设置侧滑退出
        setSwipeBackLayout(0);
        //初始化pop弹窗管理器
        mRxPopupViewManager = new PopupViewManager(this);

        //默认选中居中
        mButtonAlignCenter.setChecked(true);

    }

    @OnClick({R.id.tv_imply, R.id.tv_definition, R.id.button_above, R.id.button_below, R.id.button_left_to, R.id.button_right_to, R.id.button_align_center, R.id.button_align_left, R.id.button_align_right})
    public void onClick(View view) {
        String text = TextUtils.isEmpty(mTextInputEditText.getText()) ? TIP_TEXT : mTextInputEditText.getText().toString();
        PopupView.Builder builder;
        View tipvView;
        switch (view.getId()) {
            case R.id.tv_imply://点我
                if (popupImply == null) {
                    popupImply = new PopupImply(context);
                }
                popupImply.show(mTvImply);
                break;
            case R.id.tv_definition://标清
                initPopupView();
                titlePopup.show(mTvDefinition, 0);
                break;
            case R.id.button_above:
                mRxPopupViewManager.findAndDismiss(mTextView);
                builder = new PopupView.Builder(this, mTextView, mParentLayout, text, PopupView.POSITION_ABOVE);
                builder.setAlign(mAlign);
                tipvView =  mRxPopupViewManager.show(builder.build());
                break;
            case R.id.button_below:
                mRxPopupViewManager.findAndDismiss(mTextView);
                builder = new PopupView.Builder(this, mTextView, mParentLayout, text, PopupView.POSITION_BELOW);
                builder.setAlign(mAlign);
                builder.setBackgroundColor(getResources().getColor(R.color.orange));
                tipvView = mRxPopupViewManager.show(builder.build());
                break;
            case R.id.button_left_to:
                mRxPopupViewManager.findAndDismiss(mTextView);
                builder = new PopupView.Builder(this, mTextView, mParentLayout, text, PopupView.POSITION_LEFT_TO);
                builder.setBackgroundColor(getResources().getColor(R.color.limegreen));
                builder.setTextColor(getResources().getColor(R.color.black));
                builder.setGravity(PopupView.GRAVITY_CENTER);
                builder.setTextSize(12);
                tipvView = mRxPopupViewManager.show(builder.build());
                break;
            case R.id.button_right_to:
                mRxPopupViewManager.findAndDismiss(mTextView);
                builder = new PopupView.Builder(this, mTextView, mParentLayout, text, PopupView.POSITION_RIGHT_TO);
                builder.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
                builder.setTextColor(getResources().getColor(android.R.color.black));
                tipvView =  mRxPopupViewManager.show(builder.build());
                break;
            case R.id.button_align_center:
                mAlign = PopupView.ALIGN_CENTER;
                break;
            case R.id.button_align_left:
                mAlign = PopupView.ALIGN_LEFT;
                break;
            case R.id.button_align_right:
                mAlign = PopupView.ALIGN_RIGHT;
                break;
        }
    }

    /**
     *PopView 标题按钮上的弹窗
     */
    private void initPopupView() {
        titlePopup = new PopupSingleView(context, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.activity_popwin_item);
        titlePopup.addAction(new ActionItem("标清"));
        titlePopup.addAction(new ActionItem("高清"));
        titlePopup.addAction(new ActionItem("超清"));
        titlePopup.setColorItemText(R.color.darkgray);
        titlePopup.setItemOnClickListener(new PopupSingleView.OnItemOnClickListener() {

            @Override
            public void onItemClick(ActionItem item, int position) {
                // TODO Auto-generated method stub
                if (titlePopup.getAction(position).mTitle.equals(mTvDefinition.getText())) {
                    ToastTool.showToast(context, "当前已经为" + mTvDefinition.getText(), 500);
                } else {
                    if (position >= 0 && position < 3) {
                        mTvDefinition.setText(titlePopup.getAction(position).mTitle);
                    }
                }
            }
        });
    }

    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
        LogTools.d(TAG, "tip near anchor view " + anchorViewId + " dismissed");

        if (anchorViewId == R.id.text_view) {
            // Do something when a tip near view with id "R.id.text_view" has been dismissed
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


        PopupView.Builder builder = new PopupView.Builder(this, mTextView, mRootLayout, TIP_TEXT, PopupView.POSITION_ABOVE);
        builder.setAlign(mAlign);
        mRxPopupViewManager.show(builder.build());
    }
}
