/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.name.rmedal.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.name.rmedal.api.cookie.SerializableCookie;
import com.veni.tools.VnUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "okgo.db";
    private static final int DB_CACHE_VERSION = 1;
    static final String TABLE_CACHE = "cache";
    static final String TABLE_COOKIE = "cookie";
    static final String TABLE_DOWNLOAD = "download";
    static final String TABLE_UPLOAD = "upload";

    static final Lock lock = new ReentrantLock();

    private TableEntity cookieTableEntity = new TableEntity(TABLE_COOKIE);

    DBHelper() {
        this(VnUtils.getContext());
    }

    DBHelper(Context context) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);

        cookieTableEntity.addColumn(new ColumnEntity(SerializableCookie.HOST, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.DOMAIN, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.COOKIE, "BLOB"))//
                .addColumn(new ColumnEntity(SerializableCookie.HOST, SerializableCookie.NAME, SerializableCookie.DOMAIN));

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(cookieTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, cookieTableEntity)) db.execSQL("DROP TABLE IF EXISTS " + TABLE_COOKIE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
