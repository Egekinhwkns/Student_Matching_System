package com.example.student_matching_system

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

class StudentProfile : AppCompatActivity() {

    private var iv: ImageView? = null
    private var profileName: EditText? = null
    private var profileSurname: EditText? = null
    private var profileUsername: EditText? = null
    private var profileEb: EditText? = null
    private var profileEmail: EditText? = null
    private var profileStatus: EditText? = null
    private var profileMaxDistance: EditText? = null
    private var profileFortime: EditText? = null
    private var profilePhone: EditText? = null
    private var profileSendRequestButton: Button? = null
    private var waitingDataSets = mutableListOf<Shared>()
    private var dataSets = mutableListOf<Shared>()
    private var str: String? = null
    private var sum = 0

    private var db = Firebase.firestore

    private var forSearch: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        iv = findViewById<ImageView>(R.id.imageView2)
        iv?.clipToOutline = true
        profileName = findViewById<EditText>(R.id.profileNamePT2)
        profileSurname = findViewById<EditText>(R.id.profileSurnamePT2)
        profileUsername = findViewById<EditText>(R.id.profileUsernamePT2)
        profileEb = findViewById<EditText>(R.id.ebProfilePT2)
        profileEmail = findViewById<EditText>(R.id.profileEmailPT2)
        profileStatus = findViewById<EditText>(R.id.statusProfilePT2)
        profileMaxDistance = findViewById<EditText>(R.id.maxDistanceProfilePT2)
        profileFortime = findViewById<EditText>(R.id.timeProfilePT2)
        profilePhone = findViewById<EditText>(R.id.profilePhonePT2)
        profileSendRequestButton = findViewById<Button>(R.id.profileSaveButton2)


        profileSendRequestButton!!.isClickable = false
        profileSendRequestButton!!.isEnabled = false
        forSearch = intent.getStringExtra("Username")
        str = readFromFile()
        configureUI()

        db.collection("waitingShared").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val student1 = document["student1"].toString()
                val student2 = document["student2"].toString()
                val sharedItems = Shared(student1,student2)
                waitingDataSets.add(sharedItems)
            }
            val kontrolWaiting = checkWaitingSharing()
            if(kontrolWaiting){
                // istek zaten yollanmış
                profileSendRequestButton!!.setText("Paylaşım İsteği Gönderildi.")
            }
            else{
                sum += 1
                if(sum >= 2){
                    // eklenebilir
                    profileSendRequestButton!!.isClickable = true
                    profileSendRequestButton!!.isEnabled = true
                }
            }
        }

        db.collection("shared").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val student1 = document["student1"].toString()
                val student2 = document["student2"].toString()
                val sharedItems = Shared(student1,student2)
                dataSets.add(sharedItems)
            }
            val kontrolNormal = checkSharing()
            if(kontrolNormal){
                //zaten paylaşımdasınız
                profileSendRequestButton!!.setText("Paylaşımdasınız")
            }
            else{
                sum += 1
                if(sum >= 2){
                    // eklenebilir
                    profileSendRequestButton!!.isClickable = true
                    profileSendRequestButton!!.isEnabled = true
                }
            }
        }

        val shared = hashMapOf(
            "student1" to str,
            "student2" to forSearch
        )

        profileSendRequestButton!!.setOnClickListener{
            db.collection("waitingShared").document(str + " " + forSearch).set(shared)
                .addOnSuccessListener {
                    Toast.makeText(this, "Paylaşım İsteği Gönderildi.", Toast.LENGTH_SHORT).show()
                    finish();
                    startActivity(getIntent());
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Paylaşım İsteği Gönderilemedi.", Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun configureUI(){
        val img = FirebaseStorage.getInstance().reference.child("students/${forSearch}pp.jpg")

        val FIVE_MEGABYTE: Long = 1024 * 1024 * 5
        img.getBytes(FIVE_MEGABYTE).addOnSuccessListener { response ->
            if (response == null) {
                iv?.setImageResource(R.drawable.empty)
            } else {
                iv?.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        response,
                        0,
                        response.size
                    )
                )
            }
        }.addOnFailureListener {
            iv?.setImageResource(R.drawable.empty)
            //Toast.makeText(this, "${str}", Toast.LENGTH_SHORT).show()
        }
        forSearch?.let {
            db.collection("students").document(it).get().addOnSuccessListener { response ->
                val student: Student? = response.toObject<Student>()
                profileName?.setText(student?.name)
                profileSurname?.setText(student?.surname)
                profileUsername?.setText(student?.username)
                profileEb?.setText(student?.eb)
                profileEmail?.setText(student?.email)
                profileMaxDistance?.setText(student?.maxDistance)
                profileFortime?.setText(student?.forTime)
                profilePhone?.setText(student?.telNo)
                profileStatus?.setText(student?.status)
            }
        }
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

    fun checkSharing() : Boolean {

        var kontrol = false
        for (data in dataSets) {
            if((data.student1 == str && data.student2 == forSearch) || (data.student1 == forSearch && data.student2 == str)){
                kontrol = true
            }
        }
        return kontrol
    }

    fun checkWaitingSharing() : Boolean {

        var kontrol = false
        for (data in waitingDataSets) {
            if((data.student1 == str && data.student2 == forSearch) || (data.student1 == forSearch && data.student2 == str)){
                kontrol = true
            }
        }
        return kontrol
    }

}