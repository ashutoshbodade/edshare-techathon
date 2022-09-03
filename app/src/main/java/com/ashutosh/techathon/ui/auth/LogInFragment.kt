package com.ashutosh.techathon.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.FragmentLogInBinding
import com.ashutosh.techathon.utils.Constants.mAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LogInFragment : Fragment() {

    var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    lateinit var verificationId:String
    var phoneNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater,container,false)

        binding.txtMobile.setEndIconOnClickListener{
            if(validate())
            {
                binding.progress.visibility=View.VISIBLE
                binding.txtMobile.visibility=View.GONE
                verify ()
            }
        }

        binding.txtOTP.setEndIconOnClickListener{
            authenticate ()
            binding.progress.visibility=View.VISIBLE
            binding.txtOTP.visibility=View.GONE
        }


        return binding.root
    }

    private fun validate(): Boolean {
        if(binding.edtMobile.text.isNullOrBlank() || binding.edtMobile.text.isNullOrEmpty() || binding.edtMobile.text.toString().length < 10){
            Toast.makeText(requireActivity(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            return false
        }
        else
        {
            phoneNumber = "+91"+ binding.edtMobile.text.toString()
            return true
        }
    }

    private fun verify() {

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            binding.progress.visibility = View.GONE
            signIn(credential)
        }


        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(requireActivity(),"$p0 Error", Toast.LENGTH_LONG).show()
            Log.e("TAG", "onVerificationFailed: $$p0", )
            binding.progress.visibility = View.GONE
            binding.txtMobile.visibility=View.VISIBLE
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0.toString()
            binding.progress.visibility = View.GONE
            binding.textView2.text="OTP send to your phone: "+phoneNumber

            binding.txtOTP.visibility= View.VISIBLE

        }
    }

    private fun signIn (credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener {
                task: Task<AuthResult> ->
            if (task.isSuccessful) {
               findNavController().navigate(R.id.action_logInFragment_to_basicDetailsFragment)
            }
            else{
                binding.progress.visibility=View.GONE
                binding.txtOTP.visibility=View.VISIBLE
                Toast.makeText(requireActivity(), task.exception.toString(), Toast.LENGTH_SHORT).show()
                Log.e("TAG", "signIn: ",task.exception)
            }

        }
    }

    private fun authenticate () {
        val verifiNo = binding.edtOTP.text.toString()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)
        signIn(credential)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}