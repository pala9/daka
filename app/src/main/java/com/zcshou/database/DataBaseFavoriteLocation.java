package com.zcshou.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elvishew.xlog.XLog;

public class DataBaseFavoriteLocation extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "FavoriteLocation";
    public static final String DB_COLUMN_ID = "DB_COLUMN_ID";
    public static final String DB_COLUMN_LOCATION = "DB_COLUMN_LOCATION";
    public static final String DB_COLUMN_LONGITUDE_WGS84 = "DB_COLUMN_LONGITUDE_WGS84";
    public static final String DB_COLUMN_LATITUDE_WGS84 = "DB_COLUMN_LATITUDE_WGS84";
    public static final String DB_COLUMN_TIMESTAMP = "DB_COLUMN_TIMESTAMP";
    public static final String DB_COLUMN_LONGITUDE_CUSTOM = "DB_COLUMN_LONGITUDE_CUSTOM";
    public static final String DB_COLUMN_LATITUDE_CUSTOM = "DB_COLUMN_LATITUDE_CUSTOM";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "FavoriteLocation.db";
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME +
            " (DB_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, DB_COLUMN_LOCATION TEXT, " +
            "DB_COLUMN_LONGITUDE_WGS84 TEXT NOT NULL, DB_COLUMN_LATITUDE_WGS84 TEXT NOT NULL, " +
            "DB_COLUMN_TIMESTAMP BIGINT NOT NULL, DB_COLUMN_LONGITUDE_CUSTOM TEXT NOT NULL, DB_COLUMN_LATITUDE_CUSTOM TEXT NOT NULL)";

    public DataBaseFavoriteLocation(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    // 保存收藏位置（按坐标去重，同一位置只保留最新的一条）
    public static void saveFavoriteLocation(SQLiteDatabase sqLiteDatabase, ContentValues contentValues) {
        try {
            String longitudeWgs84 = contentValues.getAsString(DB_COLUMN_LONGITUDE_WGS84);
            String latitudeWgs84 = contentValues.getAsString(DB_COLUMN_LATITUDE_WGS84);
            sqLiteDatabase.delete(TABLE_NAME,
                            DB_COLUMN_LONGITUDE_WGS84 + " = ? AND " +
                            DB_COLUMN_LATITUDE_WGS84 + " = ?",
                    new String[] {longitudeWgs84, latitudeWgs84});
            sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            XLog.e("DATABASE: insert error");
        }
    }

    // 修改收藏名称
    public static void updateFavoriteLocationName(SQLiteDatabase sqLiteDatabase, String locID, String location) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_COLUMN_LOCATION, location);
            sqLiteDatabase.update(TABLE_NAME, contentValues, DB_COLUMN_ID + " = ?", new String[]{locID});
        } catch (Exception e) {
            XLog.e("DATABASE: update error");
        }
    }

    // 删除收藏位置
    public static void deleteFavoriteLocation(SQLiteDatabase sqLiteDatabase, int locID) {
        try {
            if (locID <= -1) {
                sqLiteDatabase.delete(TABLE_NAME, null, null);
            } else {
                sqLiteDatabase.delete(TABLE_NAME, DB_COLUMN_ID + " = ?", new String[]{Integer.toString(locID)});
            }
        } catch (Exception e) {
            XLog.e("DATABASE: delete error");
        }
    }
}
