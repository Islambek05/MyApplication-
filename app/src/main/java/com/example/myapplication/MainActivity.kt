package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.CursorAdapter

class MainActivity : AppCompatActivity() {
    private var dbHelper: DbHelper? = null
    private var isAscendingOrder = true
    private lateinit var listView: ListView
    private lateinit var adapter: ShoppingListAdapter

    // Регистрация ActivityResultLauncher
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> // Обработка результата
            if (result.resultCode == RESULT_OK) { // Если результат успешный
                loadItems(isAscendingOrder) // Загрузка списка элементов
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(this)

        // Инициализация ListView и адаптера
        listView = findViewById(R.id.listView)
        adapter = ShoppingListAdapter(this, null)
        listView.adapter = adapter

        // Обработка нажатия на кнопки
        findViewById<View>(R.id.deleteAllButton).setOnClickListener { confirmDeleteAll() }
        findViewById<View>(R.id.sortButton).setOnClickListener { toggleSortOrder() }
        findViewById<View>(R.id.addButton).setOnClickListener { openEditItemActivity() }

        // Обработка нажатия на элемент списка
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, id ->
            openEditItemActivity(id.toInt())  // Передаем ID элемента
        }

        // Обработка изменений в поле поиска
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher { // Добавление слушателя изменений
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadSearchResults(s.toString())
            }
        })

        loadItems(isAscendingOrder)
    }

    // Открытие экрана редактирования элемента
    private fun openEditItemActivity(itemId: Int = -1) {
        val intent = Intent(this, EditItemActivity::class.java) // Переходим на EditItemActivity
        intent.putExtra("item_id", itemId)  // Передаем ID элемента
        activityResultLauncher.launch(intent)
    }

    // Загрузка списка элементов с текущей сортировкой
    private fun loadItems(ascendingOrder: Boolean) {
        val cursor: Cursor? = dbHelper?.getAllItemsOrdered(ascendingOrder)
        adapter.changeCursor(cursor)
    }

    // Метод для обработки поиска
    private fun loadSearchResults(query: String) {
        val cursor: Cursor? = dbHelper?.searchItems(query) // Поиск элементов по названию
        adapter.changeCursor(cursor) // Обновление адаптера
    }

    // Подтверждение удаления всех элементов
    private fun confirmDeleteAll() {
        AlertDialog.Builder(this)
            .setTitle("Жоюды растау")
            .setMessage("Барлық элементтерді жою?")
            .setPositiveButton("Да") { _, _ ->
                dbHelper?.deleteAllItems()
                loadItems(isAscendingOrder)
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    // Переключение между порядком сортировки
    private fun toggleSortOrder() {
        isAscendingOrder = !isAscendingOrder
        loadItems(isAscendingOrder)
    }

    // Закрытие БД
    override fun onDestroy() {
        super.onDestroy()
        dbHelper?.close()
    }
}

class ShoppingListAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.item_shopping, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val itemNameTextView = view.findViewById<TextView>(R.id.itemNameTextView)
        val itemQuantityTextView = view.findViewById<TextView>(R.id.itemQuantityTextView)

        // Получаем данные из курсора
        val itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"))
        val itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))

        // Устанавливаем данные в TextView
        itemNameTextView.text = itemName
        itemQuantityTextView.text = "Количество: $itemQuantity"
    }
}
