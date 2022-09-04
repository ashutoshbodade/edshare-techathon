package com.ashutosh.techathon.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ashutosh.techathon.adapter.HomePagerAdapter
import com.ashutosh.techathon.databinding.FragmentHomeBinding
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {

    var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var homePagerAdapter: HomePagerAdapter

    lateinit var sm: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)


        sm = SessionManager(requireActivity())

        binding.txtInstitute.text = sm.getUser()!!.institutename

        binding.imgLogOut.setOnClickListener {
            sm.clearAll()
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            val userRef = Constants.db().collection("users").document(mAuth.currentUser!!.uid)
            db().runTransaction { tran ->
                tran.update(userRef,"fcm_token",token)
            }
        })

        FirebaseMessaging.getInstance().subscribeToTopic(sm.getUser()!!.instituteuid).addOnSuccessListener {
            Log.i("TAG", "subscribed to ${sm.getUser()!!.instituteuid}")
        }

        homePagerAdapter = HomePagerAdapter(parentFragmentManager)
        homePagerAdapter.add(PublicPostFragment(), "Institute Wall")
        homePagerAdapter.add(ChatFragment(), "Inbox")

        binding.viewpager.adapter = homePagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}