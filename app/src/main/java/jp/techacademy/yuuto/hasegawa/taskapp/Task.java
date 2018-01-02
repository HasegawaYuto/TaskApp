package jp.techacademy.yuuto.hasegawa.taskapp;

/**
 * Created by hasegawayuto on 2018/01/02.
 */
import java.io.Serializable;
import io.realm.RealmObject;
import java.util.Date;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject implements Serializable {
    private String title; // タイトル
    private String category;
    private String contents; // 内容
    private Date date; // 日時

    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
