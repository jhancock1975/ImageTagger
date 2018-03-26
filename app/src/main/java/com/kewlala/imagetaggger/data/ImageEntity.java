package com.kewlala.imagetaggger.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;

import java.sql.Date;

/**
 * Created by jhancock2010 on 3/4/18.
 */
@Entity
public class ImageEntity {

    public ImageEntity(){
    }

    public ImageEntity(String filePath, String sha256, Date created) {
        this.filePath = filePath;
        this.sha256 = sha256;
        this.created = created;
    }

    @PrimaryKey(autoGenerate = true)
    private int imageId;

    @ColumnInfo(name="filePath")
    private String filePath;

    @ColumnInfo(name="sha256")
    private String sha256;

    @ColumnInfo(name="created")
    private Date created;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageId=" + imageId +
                ", filePath='" + filePath + '\'' +
                ", sha256='" + sha256 + '\'' +
                ", created=" + created +
                '}';
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }

    };
}
