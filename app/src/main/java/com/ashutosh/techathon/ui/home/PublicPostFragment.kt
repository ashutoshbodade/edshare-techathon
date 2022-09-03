package com.ashutosh.techathon.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashutosh.techathon.R
import com.ashutosh.techathon.adapter.NotesAdapter
import com.ashutosh.techathon.databinding.FragmentPublicPostBinding
import com.ashutosh.techathon.ui.post.NewPostActivity
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot

class PublicPostFragment : Fragment() {

    var _binding: FragmentPublicPostBinding? = null
    private val binding get() = _binding!!

    lateinit var notesAdapter: NotesAdapter
    val notesList = ArrayList<QueryDocumentSnapshot>()

    lateinit var sm: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicPostBinding.inflate(inflater,container,false)

        sm = SessionManager(requireActivity())

        binding.btnNewPost.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(),NewPostActivity::class.java))
        }



        notesAdapter = NotesAdapter(notesList,requireActivity())

        binding.rvNotes.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvNotes.adapter = notesAdapter

        db().collection("public_notes").whereEqualTo("institute_uid",sm.getUser()!!.instituteuid).addSnapshotListener { docs, error ->
            if(docs != null){
                notesList.clear()
                notesList.addAll(docs)
                notesAdapter.notifyDataSetChanged()
            }

        }



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}