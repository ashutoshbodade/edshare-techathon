package com.ashutosh.techathon.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.FragmentMainBinding
import com.ashutosh.techathon.model.UserDataModel
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.SessionManager


class MainFragment : Fragment() {

    var _binding:FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var sm:SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater,container,false)

        sm = SessionManager(requireActivity())

        if(mAuth.currentUser != null){
            Constants.db().collection("users").document(mAuth.currentUser!!.uid).get()
                .addOnSuccessListener { userDoc ->
                    if(userDoc["name"] == null ||
                                userDoc["gender"] == null ||
                                userDoc["email"] == null ||
                                userDoc["profile_image"] == null ||
                                userDoc["uid"] == null ||
                                userDoc["instituteid"] == null ||
                                userDoc["institutename"] == null ||
                                userDoc["institutestream"] == null
                            ){
                        findNavController().navigate(R.id.action_mainFragment_to_basicDetailsFragment)
                    }
                    else
                    {
                        val userData = userDoc.toObject(UserDataModel::class.java)
                        sm.saveUser(userData!!)
                        findNavController().navigate(R.id.action_mainFragment_to_homeFragment)
                        //findNavController().navigate(R.id.action_mainFragment_to_basicDetailsFragment)
                    }
                }
        }
        else
        {
            findNavController().navigate(R.id.action_mainFragment_to_logInFragment)
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}