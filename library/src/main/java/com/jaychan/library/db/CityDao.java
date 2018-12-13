package com.jaychan.library.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaychan.library.app.MyApplication;
import com.jaychan.library.bean.City;

import java.util.ArrayList;
import java.util.List;

public class CityDao {
    // 数据库必须放在此目录下，否则无法访问，故在BaseApplication中就把asset目录下的数据库拷贝到该目录下
    private static final String PATH = MyApplication.dbFilePath;
    private static List<City> cityList;

    public static List<City> getCityList() {
        if (cityList == null) {
            cityList = new ArrayList<City>();
            SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
                    SQLiteDatabase.OPEN_READONLY);// 打开一个只读的数据库

            Cursor cursor = db.rawQuery("select * from city order by pinyin", null);//按照拼音排序
            while (cursor.moveToNext()) {
                //查询到城市
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
                City city = new City(name, pinyin);
                cityList.add(city);
            }

            cursor.close();
            db.close();// 关闭数据库
        }
        return cityList;
    }

    /**
     * 通过城市名或者拼音查找城市
     *
     * @param key 关键字
     * @return
     */
    public static List<City> searchCity(String key) {
        List<City> cities = new ArrayList<City>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);// 打开一个只读的数据库

        String sql = "select * from city where name like '%" + key + "%' or pinyin like '%" + key + "%'";

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            //查询到城市
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
            City city = new City(name, pinyin);
            cities.add(city);
        }

        cursor.close();
        db.close();// 关闭数据库
        return cities;
    }
}
