package com.kewlala.imagetaggger.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.migration.Migration;

import java.sql.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by jhancock2010 on 5/2/18.
 */
@Entity(foreignKeys = @ForeignKey(entity=ImageEntity.class,
        parentColumns = "imageId",
        childColumns = "imageId",
        onDelete = CASCADE,
        onUpdate = CASCADE),
    tableName = "Classifications")

public class ClassificationEntity {
    public ClassificationEntity(){}

    public ClassificationEntity(int imageId, String category, double probability, Date created) {
        this.imageId = imageId;
        this.category = category;
        this.probability = probability;
        this.created = created;
    }

    @PrimaryKey(autoGenerate = true)
    private int classificationId;

    @ColumnInfo(name="imageId")
    private int imageId;

    @ColumnInfo(name="category")
    private String category;

    @ColumnInfo(name="probability")
    private double probability;

    @ColumnInfo(name="created")
    private Date created;

    public int getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ClassificationEntity{" +
                "classificationId=" + classificationId +
                ", imageId=" + imageId +
                ", category='" + category + '\'' +
                ", probability=" + probability +
                ", created=" + created +
                '}';
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }

    };
}
