package com.name.rmedal.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.name.rmedal.base.BaseActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.veni.tools.LogUtils;

/**
 * 作者：kkan on 2018/10/25 10:26
 * <p>
 * 当前类注释:
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, "你的APPID");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onResp(BaseResp resp) {


//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//		builder.show();
//		}
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_LONG).show();
            } else {
                LogUtils.eTag("java", "onResp: " + resp.errCode);
                Toast.makeText(this, "支付失败", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}
