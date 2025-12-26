package com.example.leeandroidproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Random;

public class MyBoundService extends Service {
    private final Random random = new Random();
    private final IBinder binder = new IMyAidlInterface.Stub() {
        @Override
        public int getRandomNumber() throws RemoteException {
            return random.nextInt(100);
        }

        @Override
        public String getServiceInfo() throws RemoteException {
            return "AIDL Service from LeeAndroidProject";
        }

        @Override
        public int addNumbers(int a, int b) throws RemoteException {
            return a + b;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }

    public int getRandomNumber() {
        return random.nextInt(100);
    }
}