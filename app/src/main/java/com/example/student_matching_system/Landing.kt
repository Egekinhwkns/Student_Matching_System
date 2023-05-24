package com.example.student_matching_system

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

class Landing : AppCompatActivity() {
    private var ppView: ImageView? = null
    private var rv: RecyclerView? = null
    private var dataSets = mutableListOf<PoolSharedItem>()
    val poolData = mutableListOf<studentModel>()
    var str = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        ppView = findViewById(R.id.ppView)
        ppView?.clipToOutline= true
        rv = findViewById(R.id.recyclerView)
        rv?.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL))

        str = readFromFile()!!
        val img = FirebaseStorage.getInstance().reference.child("students/${str}pp.jpg")
        val FIVE_MEGABYTE: Long = 1024 * 1024 * 5
        img.getBytes(FIVE_MEGABYTE).addOnSuccessListener { response ->
            ppView?.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    response,
                    0,
                    response.size
                )
            )
        }.addOnFailureListener {
            // Handle any errors
            ppView?.setImageResource(R.drawable.empty)
            //Toast.makeText(this, "${str}", Toast.LENGTH_SHORT).show()
        }

        ppView?.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

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
        db.collection("students").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document["name"].toString()
                val surname = document["surname"].toString()
                val status = document["status"].toString()
                val username = document["username"].toString()
                val sharedItems = PoolSharedItem(name,surname,status,username)
                dataSets.add(sharedItems)
            }

            for (dataset in dataSets){

                var pp: ByteArray? = null
                var name = dataset.name
                var surname = dataset.surname
                var username = dataset.username
                var status = dataset.status

                if(username != str){
                    //paylasılan resim
                    //val img = FirebaseStorage.getInstance().reference.child("imagePool/${dataset.username}/${dataset.poolid}.jpg")
                    val FIVE_MEGABYTE: Long = 1024 * 1024 * 5
                    //img.getBytes(FIVE_MEGABYTE).addOnSuccessListener { response ->
                    //    imgPool = response
                    //pp

                    val img = FirebaseStorage.getInstance().reference.child("students/${dataset.username}pp.jpg")
                    img.getBytes(FIVE_MEGABYTE).addOnSuccessListener { response ->
                        pp = response
                        val fullname = name + " " + surname
                        poolData.add(studentModel(pp,fullname,status!!, username!!))
                        //Toast.makeText(this,"asdf" + poolData.size, Toast.LENGTH_SHORT).show()
                        rv?.adapter = poolAdapter(poolData){it ->
                            // burada karşı profile gidilecek.
                            val intent = Intent(this, StudentProfile::class.java)
                            intent.putExtra("Username", it.username)
                            startActivity(intent)
                            //Toast.makeText(this,it.poolName, Toast.LENGTH_SHORT).show()
                        }
                        //Toast.makeText(this,"${poolData}", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {it ->
                        val fullname = name + " " + surname
                        poolData.add(studentModel(pp,fullname,status!!,username!!))
                        rv?.adapter = poolAdapter(poolData){it ->
                            // burada karşı profile gidilecek.
                            val intent = Intent(this, StudentProfile::class.java)
                            intent.putExtra("Username", it.username)
                            startActivity(intent)
                            //Toast.makeText(this,it.poolName, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}