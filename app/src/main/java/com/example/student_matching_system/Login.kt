package com.example.student_matching_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernamePT = findViewById<EditText>(R.id.usernamePT)
        val passwordPT = findViewById<EditText>(R.id.passwordPT)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)


        loginButton.setOnClickListener{
            val db = Firebase.firestore
            db.collection("students").whereEqualTo("username",usernamePT.text.toString())
                .whereEqualTo("password",passwordPT.text.toString())
                .get().addOnSuccessListener {  documents ->
                    if(!documents.isEmpty){
                        writeToFile(usernamePT.text.toString())
                        val intent = Intent(this, Landing::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Kullanıcı Bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        createAccountButton.setOnClickListener{
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }
    }
    fun writeToFile(t: String){
        try {
            val file = File(this.filesDir,"usernameFile")
            val outputStream = FileOutputStream(file, false)
            val writer = outputStream.writer(Charset.defaultCharset())

            writer.use {
                it.write(t)
            }
        }
        catch (e: Exception){
            println(e)
            //Toast.makeText(this,"BASARİSİZ KAYIT",Toast.LENGTH_SHORT).show()
        }
    }
}