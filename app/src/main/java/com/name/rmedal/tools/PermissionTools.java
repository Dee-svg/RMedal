package com.name.rmedal.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.veni.tools.util.PermissionsUtils;

public class PermissionTools {
    private PermissionsUtils.Builder builder;
    private Context mContext;

    private String permission_internet = Manifest.permission.INTERNET;//网络
    private String permission_readPhoneState = Manifest.permission.READ_PHONE_STATE;//读取手机状态
    private String permission_camear = Manifest.permission.CAMERA;//相机权限
    private String permission_write = Manifest.permission.WRITE_EXTERNAL_STORAGE;//文件写入权限
    private String permission_read = Manifest.permission.READ_EXTERNAL_STORAGE;//文件读取权限
    private String permission_filesystems = Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS;// 在SDCard中创建与删除文件权限
    private String permission_location_coarse = Manifest.permission.ACCESS_COARSE_LOCATION;//定位权限
    private String permission_location_fine = Manifest.permission.ACCESS_FINE_LOCATION;//定位权限

    public PermissionTools(Activity activity) {
        builder = PermissionsUtils.with(activity);
        mContext = activity;
    }

    public PermissionTools(Fragment fragment) {
        builder = PermissionsUtils.with(fragment);
        mContext = fragment.getContext();
    }

    public void initPermission() {
        builder.initPermission();
    }

    public PermissionTools chickCamear() {
        builder.addPermission(permission_camear);
        return this;
    }

    public PermissionTools chickRead() {
        builder.addPermission(permission_read);
        return this;
    }

    public PermissionTools chickWrite() {
        builder.addPermission(permission_write);
        return this;
    }

    public PermissionTools chickFileSystem() {
        builder.addPermission(permission_filesystems);
        return this;
    }

    public PermissionTools chickInternet() {
        builder.addPermission(permission_internet);
        return this;
    }

    public PermissionTools chickReadPhoneState() {
        builder.addPermission(permission_readPhoneState);
        return this;
    }

    public PermissionTools chickLocation() {
        builder.addPermission(permission_location_coarse)
                .addPermission(permission_location_fine);
        return this;
    }

    //grantResults[i] == PackageManager.PERMISSION_DENIED 拒绝权限
    //grantResults[i] == PackageManager.PERMISSION_GRANTED 通过权限
    public boolean isEnabledinternet() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_internet) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledreadPhoneState() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_readPhoneState) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledcamear() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_camear) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledwrite() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_write) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledfilesystems() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_filesystems) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledlocation() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_location_fine) == PackageManager.PERMISSION_GRANTED)||
                (ActivityCompat.checkSelfPermission(mContext, permission_location_coarse) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isEnabledread() {
        return (ActivityCompat.checkSelfPermission(mContext, permission_read) == PackageManager.PERMISSION_GRANTED);
    }
//     /*
//     * 注册权限申请回调
//     *
//     * @param requestCode  申请码
//     * @param permissions  申请的权限
//     * @param grantResults 结果
//     * /
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PermissionsUtils.PermissionsRequestCode) {
//            for (int i = 0; i < permissions.length; i++) {
//                String permission = permissions[i];
//                //grantResults[i] == PackageManager.PERMISSION_DENIED 拒绝权限
//                //grantResults[i] == PackageManager.PERMISSION_GRANTED 通过权限
//
//                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    //通过文件读写权限
//                    enabledwrite = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    if(enabledwrite){
//                        setUpDirs();
//                    }
//                }else if (permission.equals(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
//                    if (!enabledwrite) {
//                        enabledwrite = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    }
//                    if(enabledwrite){
//                        setUpDirs();
//                    }
//                } else if (permission.equals(Manifest.permission.CAMERA)) {
//                    //通过相机权限
//                    enabledcamear = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                } else if (permission.equals(Manifest.permission.INTERNET)) {
//                    if (!enabledinternet) {
//                        enabledinternet = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    }
//                } else if (permission.equals(Manifest.permission.READ_PHONE_STATE)) {
//                    if (!enabledreadPhoneState) {
//                        enabledreadPhoneState = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    }
//                }else if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    if (!enabledlocation) {
//                        enabledlocation = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    }
//                } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    if (!enabledlocation) {
//                        enabledlocation = grantResults[i] == PackageManager.PERMISSION_GRANTED;
//                    }
//                }
//            }
//        }
//    }

}
