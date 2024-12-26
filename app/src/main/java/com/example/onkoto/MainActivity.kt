package com.example.onkoto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.onkoto.model.UserDto
import com.example.onkoto.service.UserService

/* root class */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editTextView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val button: Button = findViewById(R.id.buttonSubmit)

        button.setOnClickListener {
            buttonAction()
        }
    }

    private fun buttonAction() {
        val loginEditText = findViewById<EditText>(R.id.editTextLogin).text.toString()
        val nameEditText = findViewById<EditText>(R.id.editTextName).text.toString()

        val userService = UserService()
        val userDto = UserDto(id = "", login = loginEditText.toString(), name = nameEditText.toString())
        Log.w("Name:",loginEditText.toString())
        userService.sendUser(userDto)


    }
}