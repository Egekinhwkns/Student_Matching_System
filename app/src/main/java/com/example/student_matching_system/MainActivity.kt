package com.example.student_matching_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        val docData = hashMapOf(
            "stringExample" to "Hello world!",
            "booleanExample" to true,
            "numberExample" to 3.14159265,
            "listExample" to arrayListOf(1, 2, 3),
            "nullExample" to null,
        )
        db.collection("graduates").document("deneme").set(docData)
            .addOnSuccessListener {
                Toast.makeText(this, "Kayıt Başarılı.", Toast.LENGTH_SHORT).show()
            }

    }
}