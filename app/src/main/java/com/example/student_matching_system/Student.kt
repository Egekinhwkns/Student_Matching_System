package com.example.student_matching_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

data class Student(
    val name: String? = null,
    val surname: String? = null,
    val username: String? = null,
    val eb: String? = null,
    val email: String? = null,
    val password: String? = null,
    val status: String? = null,
    val maxDistance: String? = null,
    val telNo: String? = null,
    val forTime: String? = null,
)