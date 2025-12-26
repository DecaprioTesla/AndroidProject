package com.example.leeandroidproject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BoundServiceActivity extends Activity {

    private MyBoundService myBoundService;
    private boolean isServiceBound = false;
    private IMyAidlInterface aidlInterface;
    private TextView statusText;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 使用AIDL接口
            aidlInterface = IMyAidlInterface.Stub.asInterface(service);
            isServiceBound = true;
            statusText.setText("服务已绑定");
            Toast.makeText(BoundServiceActivity.this, "服务绑定成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            statusText.setText("服务已断开");
            aidlInterface = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service);

        statusText = findViewById(R.id.status_text);
        Button bindBtn = findViewById(R.id.bind_btn);
        Button unbindBtn = findViewById(R.id.unbind_btn);
        Button getRandomBtn = findViewById(R.id.get_random_btn);

        bindBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyBoundService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        });

        unbindBtn.setOnClickListener(v -> {
            if (isServiceBound) {
                unbindService(serviceConnection);
                isServiceBound = false;
                statusText.setText("服务已解绑");
                myBoundService = null;
            }
        });

        // 修改按钮点击事件
        getRandomBtn.setOnClickListener(v -> {
            if (isServiceBound && aidlInterface != null) {
                try {
                    int randomNum = aidlInterface.getRandomNumber();
                    Toast.makeText(this, "随机数: " + randomNum, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "调用失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请先绑定服务", Toast.LENGTH_SHORT).show();
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
}