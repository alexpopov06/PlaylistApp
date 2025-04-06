package com.practicum.playlistapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Search : AppCompatActivity() {
    private var searchText: String = ""
    private var inputEditText: EditText? = null
    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val inputEditText = findViewById<EditText>(R.id.inputText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val back = findViewById<Button>(R.id.button_back)
        back.setOnClickListener {
            finish()
        }
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            inputEditText.setText(searchText)
        }
        clearButton.setOnClickListener {
            inputEditText.setText("")
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText?.setText(searchText)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}