package com.ashutosh.techathon.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashutosh.techathon.R
import com.ashutosh.techathon.adapter.ChatAdapter
import com.ashutosh.techathon.databinding.ActivityChatBinding
import com.ashutosh.techathon.model.ChatDataModel
import com.ashutosh.techathon.ui.post.NewPostActivity
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.db
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query


class ChatActivity : AppCompatActivity() {
    var isUpdate = false
    var _binding: ActivityChatBinding?=null
    private val binding get() = _binding!!

    lateinit var chatAdapter: ChatAdapter
    val chatList = ArrayList<ChatDataModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val receiverUID =intent.getStringExtra("receiverUID").toString()
        val docID = intent.getStringExtra("docID").toString()

        isUpdate=true
        val chatRef = Constants.db().collection("chats").document(docID)

        val docRef = chatRef.collection("messages")

        chatAdapter= ChatAdapter(chatList,this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = false
        chatAdapter.setHasStableIds(true)

        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.adapter = chatAdapter

        binding.imgNewPost.setOnClickListener {
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("sendType","private")
            intent.putExtra("chatID",docID)
            intent.putExtra("receiverUID",receiverUID)
            this.startActivity(intent)
        }

        db().collection("users").document(receiverUID).get()
            .addOnSuccessListener { userDoc ->
                Glide.with(this).load(userDoc["profile_image"].toString()).circleCrop().placeholder(R.drawable.profile).into(binding.imgUser)
                binding.txtName.text = userDoc["name"].toString()
            }


        docRef.orderBy("sendTime", Query.Direction.DESCENDING)
            .addSnapshotListener { docs, error ->
                if (docs != null) {

                    chatList.clear()

                    val unreadRefs = ArrayList<DocumentReference>()
                    val deliveredRefs = ArrayList<DocumentReference>()

                    for (doc in docs) {
                        val msg = doc.toObject(ChatDataModel::class.java)
                        chatList.add(msg)
                        val chatDocRef = docRef.document(doc.id)
                        if (doc["status"] as Long == 0L && doc["from"].toString() != Constants.mAuth.currentUser!!.uid) {
                            unreadRefs.add(chatDocRef)
                        }
                        if (doc["status"] as Long == 1L && doc["from"].toString() != Constants.mAuth.currentUser!!.uid) {
                            deliveredRefs.add(chatDocRef)
                        }
                    }

                    if(isUpdate){
                    Constants.db().runTransaction { tran ->
                        for (unreadRef in unreadRefs){
                            tran.update(unreadRef,"status",2L)
                            tran.update(unreadRef,"deliveredTime", FieldValue.serverTimestamp())
                            tran.update(unreadRef,"readTime", FieldValue.serverTimestamp())
                        }
                        for (deliveredRef in deliveredRefs){
                            tran.update(deliveredRef,"status",2L)
                            tran.update(deliveredRef,"readTime", FieldValue.serverTimestamp())
                        }
                    }

                    Constants.db().runTransaction { tran ->
                        tran.update(chatRef, Constants.mAuth.currentUser!!.uid + "_unread", 0L)
                    }
                    }

                    //chatAdapter.notifyItemChanged(chatList.size - 1)
                    chatAdapter.notifyDataSetChanged()

                }
            }

        binding.imgSend.setOnClickListener {

            val message = binding.txtMessage.text.toString()

            if(!TextUtils.isEmpty(message))
            {
                val data  = mapOf(
                    "to" to receiverUID,
                    "from" to Constants.mAuth.currentUser!!.uid,
                    "message" to message,
                    "deliveredTime" to null,
                    "readTime" to null,
                    "sendTime" to FieldValue.serverTimestamp(),
                    "type" to 0,
                    "status" to 0,)

                binding.txtMessage.text.clear()

                docRef.add(data)
                    .addOnSuccessListener {
                        Constants.db().runTransaction { tran ->
                            val snapshot = tran.get(chatRef)
                            val newUnred = snapshot.getLong(receiverUID+"_unread")!!+1L
                            tran.update(chatRef,receiverUID+"_unread",newUnred)
                            tran.update(chatRef,"recent_message_time",FieldValue.serverTimestamp())
                        }
                    }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        isUpdate = false
        finish()
    }

    override fun onStop() {
        super.onStop()
        isUpdate = false
    }

    override fun onStart() {
        super.onStart()
        isUpdate = true
    }

    override fun onRestart() {
        super.onRestart()
        isUpdate= true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}