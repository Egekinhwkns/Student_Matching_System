package com.example.student_matching_system

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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

class Profile : AppCompatActivity() {
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
    private var profileSaveButton: Button? = null
    private var waitingSharingButton: Button? = null
    private var sharingButton: Button? = null
    private var profilePassword: EditText? = null
    private var cameraButton: Button? = null
    private var galleryButton: Button? = null

    private val PERMISSION_CODE_CAMERA: Int = 1000
    private val PERMISSION_CODE_GALLERY: Int = 1004
    private var imgUri: Uri? = null
    private var IMAGE_CAPTURE_CODE = 1001
    private var PICK_IMAGE_CODE = 1002


    private var str: String? = null
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        iv = findViewById<ImageView>(R.id.imageView)
        iv?.clipToOutline = true
        profileName = findViewById<EditText>(R.id.profileNamePT)
        profileSurname = findViewById<EditText>(R.id.profileSurnamePT)
        profileUsername = findViewById<EditText>(R.id.profileUsernamePT)
        profileEb = findViewById<EditText>(R.id.ebProfilePT)
        profileEmail = findViewById<EditText>(R.id.profileEmailPT)
        profileStatus = findViewById<EditText>(R.id.statusProfilePT)
        profileMaxDistance = findViewById<EditText>(R.id.maxDistanceProfilePT)
        profileFortime = findViewById<EditText>(R.id.timeProfilePT)
        profilePhone = findViewById<EditText>(R.id.profilePhonePT)
        profileSaveButton = findViewById<Button>(R.id.profileSaveButton)
        profilePassword = findViewById<EditText>(R.id.passwordProfile)
        galleryButton = findViewById<Button>(R.id.GalleryButtonProfile)
        cameraButton = findViewById<Button>(R.id.CamerabuttonProfile)
        waitingSharingButton = findViewById<Button>(R.id.waitingButton)
        sharingButton = findViewById<Button>(R.id.SharingButton)

        configureUI()

        waitingSharingButton!!.setOnClickListener{
            val intent = Intent(this, SharedList::class.java)
            startActivity(intent)
        }

        sharingButton!!.setOnClickListener{
            val intent = Intent(this, FinalSharedList::class.java)
            startActivity(intent)
        }

        profileSaveButton?.setOnClickListener {

            str = readFromFile()
            if (checkEmpty()) {
                str?.let {
                    db.collection("students").document(it).update(
                        "name", profileName!!.text.toString(),
                        "surname", profileSurname!!.text.toString(),
                        "eb", profileEb!!.text.toString(),
                        "email", profileEmail!!.text.toString(),
                        "status", profileStatus!!.text.toString(),
                        "maxDistance", profileMaxDistance!!.text.toString(),
                        "forTime", profileFortime!!.text.toString(),
                        "password" , profilePassword!!.text.toString(),
                        "telNo", profilePhone!!.text.toString()
                    ).addOnSuccessListener {
                        Toast.makeText(this,"Kayıt Başarılı.", Toast.LENGTH_SHORT).show()
                    }
                }
                imgUri?.let {uploadImage(it)}
            }
            else{
                Toast.makeText(this,"Zorunlu Alanlar Doldurulmamış.", Toast.LENGTH_SHORT).show()
            }
        }

        galleryButton!!.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    // izin verilmemiş
                    val permission =
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_GALLERY)

                } else {
                    //permission already granted
                    openGallery()
                }
            } else {
                // system is old
                openGallery()
            }
        }

        cameraButton!!.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    // izin verilmemiş
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                    )
                    requestPermissions(permission, PERMISSION_CODE_CAMERA)

                } else {
                    //izin zaten verilmiş
                    openCamera()
                }
            } else {
                // sistem eski
                openCamera()
            }
        }

    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE_CODE)
    }
    fun uploadImage(imageuri : Uri){
        val storageRef = FirebaseStorage.getInstance().reference
        val uploadTask = storageRef.child("students/${profileUsername!!.text.toString()}pp.jpg").putFile(imageuri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Fotoğraf Yükleme Başarılı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            // set image captured on camera
            iv?.setImageURI(imgUri)
        } else if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            imgUri = data?.data
            iv?.setImageURI(imgUri)
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera")
        imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE!!)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE_CAMERA -> (
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //permission from popup granted
                        openCamera()
                    } else {
                        //permission from popup was denied
                    }
                    )
        }
        when (requestCode) {
            PERMISSION_CODE_GALLERY ->
                (
                        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            //permission from popup granted
                            openGallery()
                        } else {
                            //permission from popup was denied
                        }
                        )
        }
    }

    fun configureUI() {
        str = readFromFile()
        val img = FirebaseStorage.getInstance().reference.child("students/${str}pp.jpg")

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
            // Handle any errors
            iv?.setImageResource(R.drawable.empty)
            //Toast.makeText(this, "${str}", Toast.LENGTH_SHORT).show()
        }

        str?.let {
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
                profilePassword?.setText(student?.password)
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

    private fun checkEmpty(): Boolean {
        if(!profileName!!.text.isEmpty() && !profileSurname!!.text.isEmpty() && !profileUsername!!.text.isEmpty() && !profileEb!!.text.isEmpty() && !profileEmail!!.text.isEmpty() && !profilePassword!!.text.isEmpty()
            && !profilePhone!!.text.isEmpty() && !profileFortime!!.text.isEmpty() && !profileStatus!!.text.isEmpty() && !profileMaxDistance!!.text.isEmpty()){
            return true
        }
        return false
    }
}