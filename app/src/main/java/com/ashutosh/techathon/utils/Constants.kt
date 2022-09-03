package com.ashutosh.techathon.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.util.*


object Constants{

        const val REQUEST_CODE_PERMISSIONS = 10

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fun db(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun hideKeyboard(view: View){
        try {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }catch (e: Exception){

        }
    }
}