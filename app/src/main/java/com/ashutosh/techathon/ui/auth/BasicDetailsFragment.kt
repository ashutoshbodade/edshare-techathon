package com.ashutosh.techathon.ui.auth

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.FragmentBasicDetailsBinding
import com.ashutosh.techathon.model.InstitudeModel
import com.ashutosh.techathon.model.StreamModel
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.REQUEST_CODE_PERMISSIONS
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.URIPathHelper
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import kidsparadisepatur.octalgroup.`in`.adapter.SearchInstituteAdapter
import kidsparadisepatur.octalgroup.`in`.adapter.SearchStreamAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class BasicDetailsFragment : Fragment() {

    var _binding: FragmentBasicDetailsBinding? = null
    private val binding get() = _binding!!

    internal var storage: FirebaseStorage?=null
    var compressedImageFile: File? = null
    internal var storageReference: StorageReference?=null


    lateinit var searchInstituteAdapter: SearchInstituteAdapter
    val instituteList = ArrayList<InstitudeModel>()

    lateinit var searchStreamAdapter: SearchStreamAdapter
    val streamList = ArrayList<StreamModel>()

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    val TAG = "BasicDetails"
    var imageuri :String= ""
    var gender = ""
    var instituteuid = ""
    var institutename = ""
    var institutestream= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicDetailsBinding.inflate(inflater,container,false)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        binding.imgCamera.setOnClickListener {
            showFileChooser()
        }

        binding.selectGender.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                gender = checkedRadioButton.text.toString()
            }
        })

        searchStreamAdapter = SearchStreamAdapter(requireActivity(),streamList,::selectStream)

        searchInstituteAdapter = SearchInstituteAdapter(requireActivity(),instituteList,::selectInstitute)


        db().collection("institutes").get()
            .addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.toObject(InstitudeModel::class.java)
                    data.instituteuid = doc.id
                    instituteList.add(data)
                }
                binding.rvSearchInstitute.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.rvSearchInstitute.adapter = searchInstituteAdapter
            }

        db().collection("streams").get()
            .addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.toObject(StreamModel::class.java)
                    data.streamuid = doc.id
                    streamList.add(data)
                }
                binding.rvSearchStream.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.rvSearchStream.adapter = searchStreamAdapter
            }

        binding.txtSearchStream.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadSearchInstitute(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.length > 0){
                    loadSearchStream(newText)
                    binding.rvSearchStream.visibility= View.VISIBLE
                }
                else
                {
                    binding.rvSearchStream.visibility = View.GONE
                }
                return true
            }

        })

        binding.txtSearchInstitute.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadSearchInstitute(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.length > 0){
                    loadSearchInstitute(newText)
                    binding.rvSearchInstitute.visibility= View.VISIBLE
                }
                else
                {
                    binding.rvSearchInstitute.visibility = View.GONE
                }
                return true
            }

        })

        binding.textInputLayout5.setEndIconOnClickListener {

        }

        binding.imgSubmit.setOnClickListener {
            val validateDetails = validateDetails()
            if(validateDetails.first){
                binding.imgSubmit.visibility=View.GONE
                Constants.hideKeyboard(it)
                binding.progress.visibility=View.VISIBLE

                val data = mapOf(
                    "email" to binding.txtEmail.text.toString(),
                    "gender" to gender,
                    "profile_image" to imageuri,
                    "uid" to mAuth.currentUser!!.uid,
                    "name" to binding.txtName.text.toString(),
                    "instituteid" to binding.txtInstID.text.toString(),
                    "instituteuid" to instituteuid,
                    "institutename" to institutename,
                    "institutestream" to institutestream,
                    "fcm_token" to "",
                    "created_at" to FieldValue.serverTimestamp(),
                    "phone" to Constants.mAuth.currentUser!!.phoneNumber.toString()
                )

                db().collection("users").document(mAuth.currentUser!!.uid).set(data).addOnSuccessListener {
                    findNavController().navigate(R.id.action_basicDetailsFragment_to_homeFragment)
                }
            }
            else{
                Toast.makeText(requireActivity(), validateDetails.second, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    fun selectInstitute(data:InstitudeModel){
        instituteuid = data.instituteuid
        institutename = data.institutename
        binding.rvSearchInstitute.visibility=View.GONE
        binding.txtSearchInstitute.visibility=View.GONE
        binding.textInputLayout3.visibility=View.VISIBLE
        binding.txtInstitute.setText(data.institutename)
    }

    fun loadSearchInstitute(queryText :String){
        searchInstituteAdapter.filter.filter(queryText)
        binding.rvSearchInstitute.visibility=View.VISIBLE
    }

    fun selectStream(data:StreamModel){
        institutestream = data.streamname
        binding.rvSearchStream.visibility=View.GONE
        binding.txtSearchStream.visibility=View.GONE
        binding.textInputLayout5.visibility=View.VISIBLE
        binding.txtStream.setText(data.streamname)
    }

    fun loadSearchStream(queryText :String){
        searchStreamAdapter.filter.filter(queryText)
        binding.rvSearchStream.visibility=View.VISIBLE
    }

    private fun showFileChooser() {

        if (allPermissionsGranted()) {
            selectImageFromGallery()
        }
        else
        {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }


    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        try {
            if (uri != null)
            {
                compress(uri)
            }

        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun compress (fileUri : Uri) {
        val uriPathHelper = URIPathHelper()
        val filePath = uriPathHelper.getPath(requireActivity(), fileUri).toString()
        CoroutineScope(Dispatchers.Main).launch {
            val job: Job = lifecycleScope.launch {
                compressedImageFile =  Compressor.compress(requireActivity(), File(filePath))
            }
            job.join()
            uploadImage(compressedImageFile!!)
        }
    }

    private fun uploadImage(compressImgFile: File) {
        binding.imgCamera.visibility=View.GONE
        val imageRef = storageReference!!.child("USER_IMAGES/"+ UUID.randomUUID().toString())
        val uploadTask =  imageRef.putFile(Uri.fromFile(compressImgFile))
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
                binding.imgCamera.visibility=View.VISIBLE
                if (task.isSuccessful) {

                    val downloadUri = task.result
                    imageuri = downloadUri.toString()
                    Glide.with(this).load(imageuri).circleCrop().placeholder(R.drawable.profile).into(binding.imgUser)
                }
                else
                {
                    Toast.makeText(requireActivity(), "Image Upload Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }


    private fun validateDetails():Pair<Boolean,String>{
        var result = Pair(true,"")

        if(TextUtils.isEmpty(binding.txtName.text) )
        {
            result = Pair(false, "Enter valid name")
        }
        else if(!Constants.isValidEmail(binding.txtEmail.text.toString()))
        {
            result = Pair(false, "Enter valid email")
        }
        else if(TextUtils.isEmpty(gender))
        {
            result = Pair(false, "Select gender")
        }
        else if(TextUtils.isEmpty(imageuri))
        {
            result = Pair(false, "Select profile pic")
        }
        else if(TextUtils.isEmpty(instituteuid) || TextUtils.isEmpty(institutename))
        {
            result = Pair(false, "Select institute")
        }
        else if(TextUtils.isEmpty(institutestream))
        {
            result = Pair(false, "Select stream / branch / course / class")
        }
        else if(TextUtils.isEmpty(binding.txtInstID.text))
        {
            result = Pair(false, "Enter institute ID")
        }


        return result
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}