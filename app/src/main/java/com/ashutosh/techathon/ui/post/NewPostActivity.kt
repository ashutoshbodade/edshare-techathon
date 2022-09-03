package com.ashutosh.techathon.ui.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.ActivityNewPostBinding
import com.ashutosh.techathon.databinding.FragmentBasicDetailsBinding
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.SessionManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*

class NewPostActivity : AppCompatActivity() {
    var _binding: ActivityNewPostBinding? = null
    private val binding get() = _binding!!

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)


    internal var storage: FirebaseStorage?=null
    internal var storageReference: StorageReference?=null
    
    var fileUri = ""
    var fileType = ""

    lateinit var sm: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        setSupportActionBar(binding.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnSelectFile.setOnClickListener {
            showFileChooser()
        }

        sm = SessionManager(this)
        
        binding.imgSubmit.setOnClickListener { 
            val validateDet = validateDetails()
            if(validateDet.first){
                binding.progress.visibility = View.VISIBLE
                binding.btnSelectFile.isEnabled = false
                val data = mapOf(
                    "uid" to mAuth.currentUser!!.uid,
                    "created_at" to FieldValue.serverTimestamp(),
                    "file_type" to fileType,
                    "file_uri" to fileUri,
                    "title" to binding.txtTitle.text.toString(),
                    "institute_id" to sm.getUser()!!.instituteid,
                    "institute_stream" to sm.getUser()!!.institutestream,
                    "institute_uid" to sm.getUser()!!.instituteuid,
                    "name" to sm.getUser()!!.name,
                )
                db().collection("public_notes").add(data).addOnSuccessListener {
                    Toast.makeText(this, "note added sucessfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else
            {
                Toast.makeText(this, validateDet.second, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun validateDetails():Pair<Boolean,String>{
        var result = Pair(true,"")

        if(TextUtils.isEmpty(binding.txtTitle.text) )
        {
            result = Pair(false, "Enter valid title")
        }
        else if(TextUtils.isEmpty(fileUri))
        {
            result = Pair(false, "Select file")
        }



        return result
    }

    private fun showFileChooser() {

        if (allPermissionsGranted()) {
            selectFile()
        }
        else
        {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS)
        }

    }


    private fun selectFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        selectFileResult.launch(intent) }

    private val selectFileResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                uploadImage(result.data!!.data!!)
            }

        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getFileType(fileUri: Uri):String {
        val docId = DocumentsContract.getDocumentId(fileUri)
        val split = docId.split(":".toRegex()).toTypedArray()
        val type = split[0]
        Log.e("TAG", "addFile: $type", )
        return type
    }

    private fun uploadImage(docURI: Uri) {
        
        binding.btnSelectFile.isEnabled = false
        binding.btnSelectFile.isActivated = false
        binding.btnSelectFile.isClickable = false
        binding.progress.visibility = View.VISIBLE
        
        Toast.makeText(this, "Please wait, uploading the file", Toast.LENGTH_SHORT).show()

        val imageRef = storageReference!!.child("FILES/"+ UUID.randomUUID().toString())
        val uploadTask =  imageRef.putFile(docURI)
        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageRef.downloadUrl
        })
            .addOnSuccessListener {

            }
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    fileUri = task.result.toString()
                    
                    binding.progress.visibility = View.GONE
                    
                    fileType = getFileType(docURI)

                    binding.btnSelectFile.isEnabled = true
                    binding.btnSelectFile.isActivated = true
                    binding.btnSelectFile.isClickable = true

                    Toast.makeText(this, "File Upload Success", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
           this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}