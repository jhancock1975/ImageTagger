package com.kewlala.imagetaggger.data;

/**
 * Created by jhancock2010 on 3/4/18.
 */

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ImageEntityDao {
        @Query("select * from imageentity")
        List<ImageEntity> getAll();

        @Query("SELECT * FROM imageentity WHERE imageId IN (:imageIds)")
        List<ImageEntity> loadAllByIds(int[] imageIds);

        @Query("SELECT * FROM imageentity WHERE filePath LIKE :path LIMIT 1")
        ImageEntity findByPath(String path);

        @Insert
        void insertAll(ImageEntity... imageEntities);

        @Delete
        void delete(ImageEntity imageEntity);

}
