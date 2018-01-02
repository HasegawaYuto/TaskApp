package jp.techacademy.yuuto.hasegawa.taskapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.AdapterView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.support.v7.widget.SearchView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "jp.techacademy.yuuto.hasegawa.taskapp.TASK";

    private ListView mListView;
    private TaskAdapter mTaskAdapter;
    private SearchView searchView;

    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };
    RealmResults<Task> taskRealmResults;
    String searchWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);
        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                //Log.d("DEBUG_PRINT","submit");
                searchWord = searchQuery;
                reloadListView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String searchQuery) {
                //Log.d("DEBUG_PRINT","change");
                searchWord = searchQuery;
                reloadListView();
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());

                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        addTaskForTest();

        reloadListView();
    }

    private void reloadListView() {
        if (searchWord.length() == 0) {
            taskRealmResults = mRealm.where(Task.class).findAllSorted("date", Sort.DESCENDING);
            //Log.d("DEBUG_PRINT","empty");
        } else {
            taskRealmResults = mRealm.where(Task.class).equalTo("category", searchWord).findAllSorted("date", Sort.DESCENDING);
            //Log.d("DEBUG_PRINT","filter");
        }
        mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void addTaskForTest() {
        Task task = new Task();
        task.setTitle("【カテゴリー】作業");
        task.setContents("プログラムを書いてPUSHする");
        task.setDate(new Date());
        task.setId(0);
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(task);
        mRealm.commitTransaction();
    }
}
