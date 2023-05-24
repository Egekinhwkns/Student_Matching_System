package com.example.student_matching_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder


class FinalSharedList : AppCompatActivity() {

    private var ppView: ImageView? = null
    private var rv: RecyclerView? = null
    private var dataSets = mutableListOf<Shared>()
    val poolData = mutableListOf<studentModel>()
    var str : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_shared_list)

        ppView = findViewById(R.id.ppView3)
        ppView?.clipToOutline= true
        rv = findViewById(R.id.recyclerView3)
        rv?.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        str = readFromFile()

        rv?.layoutManager = LinearLayoutManager(this)
        getPoolItems()

    }

    fun readFromFile(): String? {
        try {
            val fis: FileInputStream
            fis = openFileInput("usernameFile")
            val inputStreamReader = InputStreamReader(fis)
            val bufferedReader = BufferedReader(inputStreamReader)
            var stringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            return stringBuilder.toString()
        } catch (e: Exception) {
            println(e)
            return null
        }
    }

    fun getPoolItems(){
        val db = Firebase.firestore
        db.collection("shared").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val student1 = document["student1"].toString()
                val student2 = document["student2"].toString()
                val sharedItems = Shared(student1,student2)
                dataSets.add(sharedItems)
            }

            for (dataset in dataSets){

                if(dataset.student2 == str || dataset.student1 == str){
                    var usrnm = ""
                    if(dataset.student1 == str) usrnm = dataset.student2!!;
                    else usrnm = dataset.student1!!

                    var pp: ByteArray? = null
                    db.collection("students").document(usrnm).get().addOnSuccessListener { response ->
                        val student: Student? = response.toObject<Student>()
                        val name = student!!.name
                        val surname = student!!.surname
                        val status = student!!.status
                        val username = student!!.username
                        val poolSharedItems = PoolSharedItem(name,surname,status,username)

                        val FIVE_MEGABYTE: Long = 1024 * 1024 * 5
                        val img = FirebaseStorage.getInstance().reference.child("students/${username}pp.jpg")
                        img.getBytes(FIVE_MEGABYTE).addOnSuccessListener { response2 ->
                            pp = response2
                            val fullname = name + " " + surname
                            poolData.add(studentModel(pp,fullname,status!!, username!!))
                            rv!!.adapter = poolAdapter(poolData){it ->
                                val intent = Intent(this, StudentProfile::class.java)
                                intent.putExtra("Username", it.username)
                                startActivity(intent)
                            }
                        }.addOnFailureListener {it ->
                            val fullname = name + " " + surname
                            poolData.add(studentModel(pp,fullname,status!!,username!!))
                            rv!!.adapter = poolAdapter(poolData){it ->
                                val intent = Intent(this, StudentProfile::class.java)
                                intent.putExtra("Username", it.username)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}