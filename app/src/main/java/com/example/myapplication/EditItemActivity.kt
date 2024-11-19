package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditItemActivity : AppCompatActivity() {
    private lateinit var itemNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private var dbHelper: DbHelper? = null
    private var itemId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        itemNameEditText = findViewById(R.id.itemNameEditText)
        quantityEditText = findViewById(R.id.quantityEditText)
        dbHelper = DbHelper(this)

        // Получение ID элемента из интента
        itemId = intent.getIntExtra("item_id", -1)
        if (itemId != -1) { // Если элемент редактируется
            loadItemData(itemId)  // Загрузка данных для редактирования
        }

        findViewById<View>(R.id.saveButton).setOnClickListener { saveItem() } // Кнопка сохранения
        findViewById<Button>(R.id.deleteButton).setOnClickListener { deleteItem() }  // Кнопка удаления
    }

    // Загрузка данных для редактирования
    private fun loadItemData(itemId: Int) {
        val cursor = dbHelper?.getItemById(itemId)
        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow(DbHelper.COLUMN_NAME))
                val quantity = it.getInt(it.getColumnIndexOrThrow(DbHelper.COLUMN_QUANTITY))
                itemNameEditText.setText(name)
                quantityEditText.setText(quantity.toString())
            }
        }
    }

    // Сохранение элемента
    private fun saveItem() {
        val name = itemNameEditText.text.toString()
        val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1

        // Проверка на пустое название
        if (name.isBlank()) {
            Toast.makeText(this, "Аты бос болмауы керек", Toast.LENGTH_SHORT).show() // Сообщение об ошибке
            return
        }

        // Сохранение элемента в БД
        if (itemId == -1) { // Если элемент новый
            dbHelper?.addItem(name, quantity) // Добавление элемента
        } else { // Если элемент редактируется
            dbHelper?.updateItem(itemId, name, quantity) // Обновление элемента
        }
        // Возврат результата
        setResult(RESULT_OK)
        finish()
    }

    // Удаление текущего элемента
    private fun deleteItem() {
        if (itemId != -1) { // Проверка на существование элемента
            dbHelper?.deleteItem(itemId) // Удаление элемента
            Toast.makeText(this, "Элемент жойылды", Toast.LENGTH_SHORT).show() // Сообщение об успешном удалении
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Элемент табылмады", Toast.LENGTH_SHORT).show() // Сообщение об ошибке
        }
    }
}
