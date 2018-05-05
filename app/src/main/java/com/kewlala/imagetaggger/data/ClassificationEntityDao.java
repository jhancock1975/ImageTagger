package com.kewlala.imagetaggger.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by jhancock2010 on 5/3/18.
 */
@Dao
public interface ClassificationEntityDao {
    @Query("select * from Classifications")
    List<ClassificationEntity> getAll();

    @Query("SELECT * FROM Classifications where classificationId IN (:classificationIds)")
    List<ClassificationEntity> loadAllByIds(int[] classificationIds);

    @Query("SELECT * FROM Classifications WHERE imageID = :imageId LIMIT 1")
    ClassificationEntity findByImageID(int imageId);

    @Insert
    void insertAll(ClassificationEntity... classificationEntities);

    @Delete
    void delete(ClassificationEntity classificationEntities);
}
