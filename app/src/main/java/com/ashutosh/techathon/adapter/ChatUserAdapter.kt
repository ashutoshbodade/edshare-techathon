package com.ashutosh.techathon.adapter

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.techathon.R
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QueryDocumentSnapshot


class ChatUserAdapter(val list: ArrayList<QueryDocumentSnapshot>, val context: Context) : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    val userID = mAuth.currentUser!!.uid

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.row_chat_users, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = list[position]
        val usersList = dataItem["users_list"] as ArrayList<String>
        usersList.remove(userID)
        val otherUserID = usersList[0]

        holder.bindItems(list[position],context,otherUserID,userID)
        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {


        }
        db().collection("users").document(otherUserID).get()
            .addOnSuccessListener { doc->
                Glide.with(context).load(doc["profile_image"].toString()).centerCrop().placeholder(R.drawable.profile).into(holder.imgUser)
            }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgUser = itemView.findViewById<ImageView>(R.id.imgUser)

        var txtUserName = itemView.findViewById<TextView>(R.id.txtUserName)
        var txtUnread = itemView.findViewById<TextView>(R.id.txtUnread)

        fun bindItems(dataitem: QueryDocumentSnapshot,context:Context,otherUserID:String,userID:String)
        {


            txtUserName.text = dataitem[otherUserID+"_name"].toString()

            val unreadCount = dataitem[userID+"_unread"] as Long
            if(unreadCount > 0){
                txtUnread.text = unreadCount.toString()
                txtUnread.setTextColor(ContextCompat.getColor(context, R.color.purple_500))
                txtUserName.setTextColor(ContextCompat.getColor(context, R.color.purple_500))
            }



        }

    }

}