package com.example.leeprojectclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.leeandroidproject.IMyAidlInterface;

public class MainActivity extends Activity {

    // View references
    private TextView tvStatus;
    private TextView tvStatusMessage;
    private TextView tvResult;
    private Button btnCheckApp;
    private Button btnStartTargetApp;
    private Button btnBindService;
    private Button btnUnbindService;
    private Button btnGetRandom;
    private Button btnGetInfo;
    private Button btnAddNumbers;

    // Service connection
    private IMyAidlInterface aidlInterface;
    private boolean isServiceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlInterface = IMyAidlInterface.Stub.asInterface(service);
            isServiceBound = true;

            runOnUiThread(() -> {
                updateConnectionStatus(true, "已连接到远程服务");
                showToast("成功连接到远程服务");

                // 启用服务调用按钮
                enableServiceButtons(true);
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlInterface = null;
            isServiceBound = false;

            runOnUiThread(() -> {
                updateConnectionStatus(false, "连接已断开");
                enableServiceButtons(false);
            });
        }

        @Override
        public void onBindingDied(ComponentName name) {
            aidlInterface = null;
            isServiceBound = false;

            runOnUiThread(() -> {
                updateConnectionStatus(false, "绑定已失效");
                enableServiceButtons(false);
            });
        }

        @Override
        public void onNullBinding(ComponentName name) {
            aidlInterface = null;
            isServiceBound = false;

            runOnUiThread(() -> {
                updateConnectionStatus(false, "绑定返回空");
                enableServiceButtons(false);
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initViews();

        // Set up click listeners
        setupClickListeners();

        // Initialize UI state
        updateConnectionStatus(false, "等待连接...");
        enableServiceButtons(false);
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvResult = findViewById(R.id.tvResult);

        btnCheckApp = findViewById(R.id.btnCheckApp);
        btnStartTargetApp = findViewById(R.id.btnStartTargetApp);
        btnBindService = findViewById(R.id.btnBindService);
        btnUnbindService = findViewById(R.id.btnUnbindService);
        btnGetRandom = findViewById(R.id.btnGetRandom);
        btnGetInfo = findViewById(R.id.btnGetInfo);
        btnAddNumbers = findViewById(R.id.btnAddNumbers);
    }

    private void setupClickListeners() {
        // 检查目标应用是否安装
        btnCheckApp.setOnClickListener(v -> checkTargetAppInstalled());

        // 启动目标应用
        btnStartTargetApp.setOnClickListener(v -> startTargetApp());

        // 绑定服务
        btnBindService.setOnClickListener(v -> bindToRemoteService());

        // 断开连接
        btnUnbindService.setOnClickListener(v -> unbindService());

        // 获取随机数
        btnGetRandom.setOnClickListener(v -> getRandomNumber());

        // 获取服务信息
        btnGetInfo.setOnClickListener(v -> getServiceInfo());

        // 计算数字
        btnAddNumbers.setOnClickListener(v -> addNumbers(5, 3));
    }

    private void checkTargetAppInstalled() {
        boolean isInstalled = isTargetAppInstalled();
        if (isInstalled) {
            showToast("目标应用已安装");
            updateStatusMessage("目标应用已安装，可以绑定服务");
        } else {
            showToast("目标应用未安装");
            updateStatusMessage("目标应用未安装，请先安装服务提供者");
        }
    }

    private void startTargetApp() {
        if (isTargetAppInstalled()) {
            try {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.leeandroidproject");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                    showToast("正在启动目标应用...");
                    updateStatusMessage("已启动目标应用，请稍后绑定服务");
                } else {
                    showToast("无法启动目标应用");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("启动失败: " + e.getMessage());
            }
        } else {
            showToast("请先安装目标应用");
        }
    }

    private void bindToRemoteService() {
        if (!isServiceBound) {
            // 先检查目标应用是否安装
            if (!isTargetAppInstalled()) {
                showToast("请先安装目标应用: com.example.leeandroidproject");
                updateStatusMessage("目标应用未安装");
                return;
            }

            try {
                updateStatusMessage("正在绑定到远程服务...");

                // 方法1：使用隐式Intent
                Intent intent = new Intent();
                intent.setAction("com.example.leeandroidproject.MyAIDLService");
                intent.setPackage("com.example.leeandroidproject");

                // 绑定服务
                boolean bindResult = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                if (bindResult) {
                    updateStatusMessage("绑定请求已发送...");
                    showToast("正在尝试绑定服务...");
                } else {
                    updateStatusMessage("绑定失败");
                    showToast("绑定失败，请确保目标应用正在运行");

                    // 方法2：尝试使用显式Intent
                    try {
                        Intent explicitIntent = new Intent();
                        explicitIntent.setComponent(new ComponentName(
                                "com.example.leeandroidproject",
                                "com.example.leeandroidproject.MyBoundService"
                        ));

                        boolean explicitResult = bindService(explicitIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                        if (explicitResult) {
                            updateStatusMessage("使用显式Intent绑定成功");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("显式绑定失败: " + e.getMessage());
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                updateStatusMessage("权限异常: " + e.getMessage());
                showToast("绑定失败，请检查权限");
            } catch (Exception e) {
                e.printStackTrace();
                updateStatusMessage("绑定异常: " + e.getMessage());
                showToast("绑定异常: " + e.getMessage());
            }
        } else {
            showToast("服务已绑定");
        }
    }

    private void unbindService() {
        if (isServiceBound) {
            try {
                unbindService(serviceConnection);
                aidlInterface = null;
                isServiceBound = false;

                updateConnectionStatus(false, "已断开连接");
                enableServiceButtons(false);
                showToast("已断开服务连接");
            } catch (IllegalArgumentException e) {
                // Service not registered, ignore
                updateConnectionStatus(false, "连接已断开");
                enableServiceButtons(false);
            }
        }
    }

    private void getRandomNumber() {
        if (isServiceBound && aidlInterface != null) {
            try {
                int randomNum = aidlInterface.getRandomNumber();
                updateResult("随机数结果: " + randomNum);
                showToast("获取随机数: " + randomNum);
            } catch (RemoteException e) {
                e.printStackTrace();
                updateResult("调用失败: " + e.getMessage());
                showToast("调用失败，服务可能已断开");

                // 重新设置状态
                isServiceBound = false;
                aidlInterface = null;
                updateConnectionStatus(false, "连接异常，请重新绑定");
                enableServiceButtons(false);
            }
        } else {
            showToast("请先绑定服务");
        }
    }

    private void getServiceInfo() {
        if (isServiceBound && aidlInterface != null) {
            try {
                String info = aidlInterface.getServiceInfo();
                updateResult("服务信息: " + info);
                showToast(info);
            } catch (RemoteException e) {
                e.printStackTrace();
                updateResult("调用失败: " + e.getMessage());
            }
        } else {
            showToast("请先绑定服务");
        }
    }

    private void addNumbers(int a, int b) {
        if (isServiceBound && aidlInterface != null) {
            try {
                int result = aidlInterface.addNumbers(a, b);
                updateResult(a + " + " + b + " = " + result);
                showToast("计算结果: " + result);
            } catch (RemoteException e) {
                e.printStackTrace();
                updateResult("调用失败: " + e.getMessage());
            }
        } else {
            showToast("请先绑定服务");
        }
    }

    private boolean isTargetAppInstalled() {
        try {
            getPackageManager().getPackageInfo("com.example.leeandroidproject", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void updateConnectionStatus(boolean connected, String message) {
        if (connected) {
            tvStatus.setText("已连接");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnUnbindService.setEnabled(true);
        } else {
            tvStatus.setText("未连接");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnUnbindService.setEnabled(false);
        }
        tvStatusMessage.setText(message);
    }

    private void updateStatusMessage(String message) {
        tvStatusMessage.setText(message);
    }

    private void updateResult(String result) {
        tvResult.setText(result);
    }

    private void enableServiceButtons(boolean enabled) {
        btnGetRandom.setEnabled(enabled);
        btnGetInfo.setEnabled(enabled);
        btnAddNumbers.setEnabled(enabled);
        btnUnbindService.setEnabled(enabled);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            unbindService();
        }
    }
}