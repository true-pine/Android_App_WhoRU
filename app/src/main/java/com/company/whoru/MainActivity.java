package com.company.whoru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar = null;
    public static DevicePolicyManager mDPM = null;
    private ComponentName mReceiver = null;
    private ImageView imageView = null;
    private Switch onOffSwitch = null;
    private TextView topTextView = null;
    private TextView bottomTextView = null;

    private KeyguardManager keyguardManager = null;

    private AlertDialog.Builder alertDialogBuilder = null;
    private AlertDialog alertDialog = null;

    private final int CAMERA_PERMISSION_REQUEST = 1001;
    private final int STORAGE_PERMISSION_REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mReceiver = new ComponentName(this, ReceiverAdmin.class);

        this.initializeView();
        this.initDialog();

        checkPermission();

        keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        if(!keyguardManager.isKeyguardSecure()) {
            this.showDialog();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!keyguardManager.isKeyguardSecure()) {
            onOffSwitch.setEnabled(false);
        } else {
            onOffSwitch.setEnabled(true);
            if(mDPM.isAdminActive(mReceiver)) {
                onOffSwitch.setChecked(true);
                imageView.setImageResource(R.drawable.lock);
                topTextView.setText("디바이스가 안전하게 보호되어 있습니다");
                bottomTextView.setText("버튼을 누르면 보호가 해제됩니다");
            } else {
                onOffSwitch.setChecked(false);
                imageView.setImageResource(R.drawable.unlock);
                topTextView.setText("현재 서비스가 비활성화되어 있습니다");
                bottomTextView.setText("버튼을 눌러 디바이스를 보호하세요");
            }
        }
    }

    protected void initializeView() {
        imageView = (ImageView)findViewById(R.id.imageView);
        onOffSwitch = (Switch)findViewById(R.id.switch1);
        topTextView = (TextView)findViewById(R.id.textView2);
        bottomTextView = (TextView)findViewById(R.id.textView3);

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mDPM.isAdminActive(mReceiver)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mReceiver);
                        startActivityForResult(intent, 1111);
                    }
                } else {
                    if (mDPM.isAdminActive(mReceiver)) {
                        mDPM.removeActiveAdmin(mReceiver);
                    }
                    imageView.setImageResource(R.drawable.unlock);
                    topTextView.setText("현재 서비스가 비활성화되어 있습니다");
                    bottomTextView.setText("버튼을 눌러 디바이스를 보호하세요");
                }
            }
        });
    }

    protected void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        }

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "파일 저장 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "카메라 권한 허가되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "카메라 권한 제한되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case STORAGE_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "파일 저장 권한이 허가되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "파일 저장 권한이 제한되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1111 && resultCode == RESULT_OK) {
            imageView.setImageResource(R.drawable.lock);
            topTextView.setText("디바이스가 안전하게 보호되어 있습니다");
            bottomTextView.setText("버튼을 누르면 보호가 해제됩니다");
        }
    }

    protected void initDialog() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("잠금설정");
        alertDialogBuilder
                .setMessage("화면 잠금을 설정하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "서비스를 활성화 할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void showDialog() {
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.action_setting) {

            return true;
        }
        return false;
    }
}
