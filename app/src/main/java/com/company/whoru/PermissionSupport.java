package com.company.whoru;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionSupport {

    private Context context;
    private Activity activity;
    private String[] permissions = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };
    private HashMap<String, String> permissionMap;
    private List<String> deniedList;
    private final int PERMISSIONS_REQUEST = 3004;

    public PermissionSupport(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        permissionMap = new HashMap<>();
        permissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "저장소 쓰기");
        permissionMap.put(Manifest.permission.CAMERA, "카메라");
    }

    public boolean checkPermissions() {
        deniedList = new ArrayList<>();

        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedList.add(permission);
            }
        }

        return deniedList.isEmpty();
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(activity, deniedList.toArray(new String[0]) , PERMISSIONS_REQUEST);
    }

    public boolean permissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        boolean allPermissionGrant = true;

        if(requestCode == PERMISSIONS_REQUEST && (grantResults.length > 0)) {
            for(int i = 0; i < grantResults.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allPermissionGrant = false;
                    Toast.makeText(context, permissionMap.get(permissions[i]) + "권한이 필요합니다", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return allPermissionGrant;
    }
}
