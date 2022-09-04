package com.ashutosh.techathon.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashutosh.techathon.R
import com.ashutosh.techathon.adapter.ChatUserAdapter
import com.ashutosh.techathon.databinding.FragmentChatBinding
import com.ashutosh.techathon.databinding.FragmentHomeBinding
import com.ashutosh.techathon.model.ChatDataModel
import com.ashutosh.techathon.model.InstitudeModel
import com.ashutosh.techathon.model.UserDataModel
import com.ashutosh.techathon.ui.chat.ChatActivity
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.SessionManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kidsparadisepatur.octalgroup.`in`.adapter.SearchUsersAdapter
import kotlin.math.log

class ChatFragment : Fragment() {

    var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatUserAdapter: ChatUserAdapter
    private var userList = ArrayList<QueryDocumentSnapshot>()

    lateinit var searchUsersAdapter: SearchUsersAdapter
    val userListSearch = ArrayList<UserDataModel>()

    lateinit var sm: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater,container,false)

        sm = SessionManager(requireActivity())

        searchUsersAdapter = SearchUsersAdapter(requireActivity(),userListSearch,::selectSearchUser)

        db().collection("users").whereEqualTo("instituteuid",sm.getUser()!!.instituteuid).get()
            .addOnSuccessListener { docs ->
                for(doc in docs){
                    val data = doc.toObject(UserDataModel::class.java)
                    data.uid = doc.id
                    userListSearch.add(data)
                }
                binding.rvSearchUsers.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.rvSearchUsers.adapter = searchUsersAdapter
            }

        binding.txtSearchStudent.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadSearchUser(query!!)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.length > 0){
                    loadSearchUser(newText)
                }
                else
                {
                    binding.rvSearchUsers.visibility = View.GONE
                    binding.rvUsers.visibility = View.VISIBLE
                }
                return true
            }

        })

        binding.rvUsers.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        chatUserAdapter = ChatUserAdapter(userList,requireActivity())
        binding.rvUsers.adapter = chatUserAdapter

        db().collection("chats")
            .whereArrayContains("users_list", mAuth.currentUser!!.uid)
            .orderBy("recent_message_time", Query.Direction.DESCENDING)
            .addSnapshotListener { docs, error ->
                if(docs!=null){
                    userList.clear()
                    Log.e("TAG", "chatuserssize: ${docs.size()}")
                    for (doc in docs){
                        userList.add(doc)
                        val messageRef = db().collection("chats").document(doc.id).collection("messages")
                        messageRef.whereEqualTo("status",0)
                            .whereNotEqualTo("from", mAuth.currentUser!!.uid)
                            .get()
                            .addOnSuccessListener { dcs ->
                                for(d in dcs){
                                    val ref = messageRef.document(d.id)
                                    db().runTransaction { tran ->
                                        tran.update(ref,"status",1)
                                        tran.update(ref,"deliveredTime", FieldValue.serverTimestamp())
                                    }
                                }
                            }
                    }
                    chatUserAdapter.notifyDataSetChanged()
                }
            }

        return binding.root
    }

    fun selectSearchUser(dataModel: UserDataModel){
        if(mAuth.currentUser!!.uid != dataModel.uid)
        {
            db().collection("chats")
                .whereEqualTo("users."+mAuth.currentUser!!.uid,true)
                .whereEqualTo("users."+dataModel.uid.toString(),true)
                .get()
                .addOnSuccessListener { docs ->
                    if(docs.size() > 0){
                        for (doc in docs){
                            Log.e("TAG", "onBindViewHolder: documentfound ", )
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("receiverUID",dataModel.uid.toString())
                            intent.putExtra("docID",doc.id)
                            requireActivity().startActivity(intent)
                        }
                    }
                    else
                    {
                        val usersArray = ArrayList<String>()
                        usersArray.add(mAuth.currentUser!!.uid)
                        usersArray.add( dataModel.uid.toString())

                        val data = mapOf(
                            "created_at" to FieldValue.serverTimestamp(),
                            "created_by" to mAuth.currentUser!!.uid,
                            "is_searching" to true,
                            "is_friends" to false,
                            "is_user_found" to false,
                            "users" to mapOf(
                                mAuth.currentUser!!.uid to true,
                                dataModel.uid.toString() to true
                            ),
                            "users_list" to usersArray,
                            dataModel.uid.toString()+"_name" to dataModel.name.toString(),
                            dataModel.uid.toString()+"_unread" to 0L,
                            mAuth.currentUser!!.uid+"_name" to sm.getUser()!!.name,
                            mAuth.currentUser!!.uid+"_unread" to 0L,
                            "recent_message_time" to FieldValue.serverTimestamp(),
                        )

                        db().collection("chats").add(data)
                            .addOnSuccessListener {
                                val intent = Intent(context, ChatActivity::class.java)
                                intent.putExtra("receiverUID", dataModel.uid.toString())
                                intent.putExtra("docID",it.id)
                                requireActivity().startActivity(intent)
                            }
                    }
                }
        }
    }

    fun loadSearchUser(queryText :String){
        searchUsersAdapter.filter.filter(queryText)
        binding.rvSearchUsers.visibility=View.VISIBLE
        binding.rvUsers.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}