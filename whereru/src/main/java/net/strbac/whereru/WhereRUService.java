package net.strbac.whereru;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class WhereRUService extends Service {

    private final IBinder mBinder = new WRUBinder();

    public WhereRUService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class WRUBinder extends Binder {
        WhereRUService getService() {
            return WhereRUService.this;
        }
    }
}
