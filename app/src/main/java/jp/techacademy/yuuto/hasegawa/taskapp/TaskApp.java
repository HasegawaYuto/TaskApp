package jp.techacademy.yuuto.hasegawa.taskapp;

/**
 * Created by hasegawayuto on 2018/01/02.
 */
import android.app.Application;

import io.realm.Realm;

public class TaskApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
