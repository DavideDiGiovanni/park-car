package eu.dave.parkcar.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import eu.dave.parkcar.entity.Parking

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "parkings.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "parkings"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LATITUDE REAL, $COLUMN_LONGITUDE REAL, $COLUMN_NAME TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertParking(latitude: Double, longitude: Double, name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
            put(COLUMN_NAME, name)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun updateParking(id: Long, latitude: Double, longitude: Double, name: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
            put(COLUMN_NAME, name)
        }
        val updatedRows = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return updatedRows
    }

    fun deleteParking(id: Long): Int {
        val db = writableDatabase
        val deletedRows = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return deletedRows
    }

    fun getAllParkings(): List<Parking> {
        val parkings = mutableListOf<Parking>()
        val db = readableDatabase
        val columns = arrayOf(COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_NAME)
        val cursor: Cursor? = db.query(TABLE_NAME, columns, null, null, null, null, null)

        cursor?.use {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE)
            val longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)

            while (cursor.moveToNext()) {
                val id = if (idIndex != -1) cursor.getLong(idIndex) else 0L
                val latitude = if (latitudeIndex != -1) cursor.getDouble(latitudeIndex) else 0.0
                val longitude = if (longitudeIndex != -1) cursor.getDouble(longitudeIndex) else 0.0
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
                val parking = Parking(id, latitude, longitude, name)
                parkings.add(parking)
            }
        }

        db.close()
        return parkings
    }

    fun getParkingByName(name: String): Parking? {
        val db = readableDatabase
        val cursor: Cursor? =
            db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ?", arrayOf(name))
        var parking: Parking? = null

        cursor?.let {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE)
            val longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE)

            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idIndex)
                val latitude = cursor.getDouble(latitudeIndex)
                val longitude = cursor.getDouble(longitudeIndex)
                parking = Parking(id, latitude, longitude, name)
            }
            cursor.close()
        }

        db.close()
        return parking
    }


}
