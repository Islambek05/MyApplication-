package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // Метод для получения элемента по ID
    fun getItemById(id: Int): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
    }


    // Метод для добавления элемента
    fun addItem(name: String?, quantity: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_QUANTITY, quantity)
        return db.insert(TABLE_NAME, null, values)
    }

    // Метод для обновления элемента
    fun updateItem(id: Int, name: String?, quantity: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_QUANTITY, quantity)
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", arrayOf(id.toString()))
    }

    // Метод для получения всех элементов
    val allItems: Cursor
        get() {
            val db = this.readableDatabase
            return db.query(TABLE_NAME, null, null, null, null, null, null)
        }

    // Метод для удаления элемента
    fun deleteItem(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", arrayOf(id.toString()))
    }

    // Метод для удаления всех элементов
    fun deleteAllItems(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }

    // Метод для поиска элементов по названию
    fun searchItems(query: String): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_NAME,
            null,
            COLUMN_NAME + " LIKE ?",
            arrayOf("%$query%"),
            null,
            null,
            null
        )
    }

    // Метод для получения всех элементов в указанном порядке
    fun getAllItemsOrdered(ascendingOrder: Boolean): Cursor {
        val db = this.readableDatabase
        val orderBy = if (ascendingOrder) COLUMN_NAME + " ASC" else COLUMN_NAME + " DESC"
        return db.query(TABLE_NAME, null, null, null, null, null, orderBy) // Используем orderBy для сортировки
    }

    // Методы для получения индексов столбцов
    companion object {
        private const val DATABASE_NAME = "shoppingList.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME: String = "shopping_items"
        const val COLUMN_ID: String = "_id"
        const val COLUMN_NAME: String = "item_name"
        const val COLUMN_QUANTITY: String = "quantity"
    }
}
