package com.example.admin.yandextranslator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "My DB"
val TABLE_NAME = "Collocations"
val COL_COLLOCATION = "collocation"
val COL_DATE = "date"
val COL_ID = "id"

class DataBaseHundler(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_COLLOCATION + " VARCHAR(256)," +
                COL_DATE+ " VARCHAR(256));"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertData(collocation: Collocation){
        val db = this.getWritableDatabase()
        var cv = ContentValues()

        cv.put(COL_COLLOCATION,collocation.collocation)
        cv.put(COL_DATE,collocation.date)

        var result = db.insert(TABLE_NAME,null,cv)
        if (result == (-1).toLong()){
            Toast.makeText(context, "Field to insert data", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Success insert data", Toast.LENGTH_SHORT).show()
        }
    }


    fun readData(): MutableList<Collocation>{
        var list: MutableList<Collocation> = ArrayList()

        val db = this.readableDatabase
        var query = "Select * from " + TABLE_NAME
        val results = db.rawQuery(query,null)

        if (results.moveToFirst()){
            do {
                var collocation = Collocation()

                collocation.collocation = results.getString(1).toString()
                collocation.date = results.getString(2).toString()

                list.add(collocation)
            }while (results.moveToNext())
        }


        results.close()
        db.close()
        return list
    }

    fun deleteAllData(){
        val db = this.writableDatabase

        db.delete(TABLE_NAME, null, null)

        db.close()
    }

    fun deleteData(collocation: Collocation?){
        val db = this.writableDatabase

        db.delete(TABLE_NAME, COL_COLLOCATION + "=? and " + COL_DATE + "=?", arrayOf(collocation!!.collocation,collocation!!.date))

        db.close()
    }

}