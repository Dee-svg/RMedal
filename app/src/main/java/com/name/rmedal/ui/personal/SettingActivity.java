package com.name.rmedal.ui.personal;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.tools.FingerprintUtil;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.bigimage.BigImageBean;
import com.name.rmedal.ui.bigimage.BigImagePagerActivity;
import com.veni.tools.util.ACache;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.PermissionsUtils;
import com.veni.tools.util.SPUtils;
import com.veni.tools.widget.TitleView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.setting_touxiang)
    ImageView settingTouxiang;
    @BindView(R.id.setting_name)
    TextView settingName;
    @BindView(R.id.setting_username)
    TextView settingUserName;
    @BindView(R.id.setting_userphone)
    TextView settingUserPhone;
    @BindView(R.id.setting_sp_used)
    TextView settingSpUsed;
    @BindView(R.id.setting_pattern_lock_status)
    TextView settingPatternLockStatus;
    @BindView(R.id.setting_fingerprint_lock_status)
    TextView settingFingerprintLockStatus;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    /*启用MVP一定要设置这句*/
    @Override
    public void initPresenter() {
    }

    private UserBean userBean;
    private boolean ispermissionstorage = false;//文件读写权限 true 通过
    private String codeHead;//头像，Base64字符串

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle(R.string.show_setting);

        userBean = AppTools.getUserBean(context);
        settingName.setText(userBean.getUserName());
        Glide.with(context).load(userBean.getCustomerImg()).apply(new RequestOptions()
                .error(R.mipmap.ic_touxiang).circleCrop()).into(settingTouxiang);
        settingUserName.setText(userBean.getRealName());
        settingUserPhone.setText(userBean.getPhone());

        String cacheSize = SPUtils.getFormatSize(SPUtils.getTotalCacheSize(context));
        settingSpUsed.setText(cacheSize);

        //请求文件读写 权限
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .initPermission();
        ispermissionstorage = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String haspatternlock = ACache.get(context).getAsString(AppConstant.PatternlockKey + userBean.getId());
        settingPatternLockStatus.setText(DataUtils.isNullString(haspatternlock) ? R.string.show_open : R.string.show_off);
        String hasfingerprintlock = ACache.get(context).getAsString(AppConstant.FingerprintKey + userBean.getId());
        settingFingerprintLockStatus.setText(DataUtils.isNullString(hasfingerprintlock) ? R.string.show_open : R.string.show_off);
    }

    @OnClick({R.id.setting_touxiang, R.id.setting_username_ll, R.id.setting_userphone_ll, R.id.setting_sp_used_ll,
            R.id.setting_big_images, R.id.setting_pattern_lock_ll, R.id.setting_fingerprint_lock_ll})
    public void onViewClicked(View view) {
        if (antiShake.check(view.getId())) return;
        switch (view.getId()) {
            case R.id.setting_touxiang: // 设置 头像
                if (ispermissionstorage) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, AppConstant.REQUEST_CODEHEAD);
                } else {
                    //请求文件读写 权限
                    PermissionsUtils.with(context)
                            .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .initPermission();
                }
                break;
            case R.id.setting_username_ll:
                break;
            case R.id.setting_userphone_ll:
                break;
            case R.id.setting_sp_used_ll:
                break;
            case R.id.setting_big_images: // 查看大图
                List<BigImageBean> img_list = JsonUtils.parseArray(AppConstant.imgjson, BigImageBean.class);
                startActivity(BigImagePagerActivity.class,
                        BigImagePagerActivity.startJump(img_list, 0));
                break;
            case R.id.setting_pattern_lock_ll:
                String haspatternlock = ACache.get(context).getAsString(AppConstant.PatternlockKey + userBean.getId());
                if (DataUtils.isNullString(haspatternlock)) {//没有手势密码
                    startActivity(PatternlockActivity.class, PatternlockActivity.startJump(AppConstant.CJPatternlock1));
                } else {
                    ACache.get(context).remove(AppConstant.PatternlockKey + userBean.getId());
                    ACache.get(context).remove(AppConstant.PatternlockOK + userBean.getId());
                    settingPatternLockStatus.setText(R.string.show_open);
                }
                break;
            case R.id.setting_fingerprint_lock_ll:
                String hasfingerprintlock = ACache.get(context).getAsString(AppConstant.FingerprintKey + userBean.getId());
                if (DataUtils.isNullString(hasfingerprintlock)) {//没有指纹密码
                    FingerprintUtil.getFingerprintIsok(context);
                    startActivity(FingerprintLockActivity.class, FingerprintLockActivity.startJump(AppConstant.FingerprintKey1));
                } else {
                    ACache.get(context).remove(AppConstant.FingerprintKey + userBean.getId());
                    ACache.get(context).remove(AppConstant.FingerprintOK + userBean.getId());
                    settingFingerprintLockStatus.setText(R.string.show_open);
                }
                break;

        }
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photobitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                switch (requestCode) {
                    case AppConstant.REQUEST_CODEHEAD://头像
                        codeHead = Bitmap2StrByBase64(photobitmap);
                        settingTouxiang.setImageBitmap(photobitmap);
                        break;
                }
            } catch (IOException ignored) {
            }
        }
    }

    /*
     * 注册权限申请回调
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String camera = permissions[i];
                //grantResults[i] == PackageManager.PERMISSION_DENIED 拒绝权限
                //grantResults[i] == PackageManager.PERMISSION_GRANTED 通过权限

                if (camera.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && i < grantResults.length) {
                    //通过文件读写权限
                    ispermissionstorage = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
        }
    }
}
