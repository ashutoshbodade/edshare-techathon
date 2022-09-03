package com.ashutosh.techathon.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.techathon.R
import com.ashutosh.techathon.utils.Constants
import com.ashutosh.techathon.utils.Constants.db
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.SessionManager
import com.ashutosh.techathon.utils.TimestampUtil.getDateTimeFirebaseTimestamp
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot


class NotesAdapter(val list: ArrayList<QueryDocumentSnapshot>, val context: Context) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    lateinit var sm: SessionManager

    init {
        sm = SessionManager(context)
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.row_notes, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = list[position]

        holder.bindItems(dataItem,context)
        holder.setIsRecyclable(false)

        holder.txtDownload.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(dataItem["file_uri"].toString())
            context.startActivity(i)
        }

        holder.txtStudentName.setOnClickListener {
          Constants.db().collection("chats")
              .whereEqualTo("users."+mAuth.currentUser!!.uid,true)
              .whereEqualTo("users."+dataItem["uid"].toString(),true)
              .get()
              .addOnSuccessListener { docs ->
                    if(docs.size() > 0){
                        Log.e("TAG", "onBindViewHolder: documentfound ", )
                    }
                  else
                    {

                        val usersArray = ArrayList<String>()
                        usersArray.add(mAuth.currentUser!!.uid)
                        usersArray.add(dataItem["uid"].toString())

                        val data = mapOf(
                            "created_at" to FieldValue.serverTimestamp(),
                            "created_by" to mAuth.currentUser!!.uid,
                            "is_searching" to true,
                            "is_friends" to false,
                            "is_user_found" to false,
                            "users" to mapOf(
                                mAuth.currentUser!!.uid to true,
                                dataItem["uid"].toString() to true
                            ),
                            "users_list" to usersArray,
                            dataItem["uid"].toString()+"_name" to dataItem["name"].toString(),
                            dataItem["uid"].toString()+"_unread" to 0L,
                            mAuth.currentUser!!.uid+"_name" to sm.getUser()!!.name,
                            mAuth.currentUser!!.uid+"_unread" to 0L,
                        )

                       db().collection("chats").add(data)
                           .addOnSuccessListener {
                               Log.e("TAG", "onBindViewHolder: user_added ", )
                           }
                    }
              }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        var imgFile = itemView.findViewById<ImageView>(R.id.imgFile)
        var txtDownload = itemView.findViewById<TextView>(R.id.txtDownload)
        var txtStream = itemView.findViewById<TextView>(R.id.txtStream)
        var txtStudentName = itemView.findViewById<TextView>(R.id.txtStudentName)
        var txtTimeStamp = itemView.findViewById<TextView>(R.id.txtTimeStamp)
        var viewVideo = itemView.findViewById<VideoView>(R.id.viewVideo)

        fun bindItems(dataitem: QueryDocumentSnapshot,context:Context)
        {
            txtTitle.text = dataitem["title"].toString()+" ("+""+dataitem["note_type"].toString()+")"
            txtStream.text = dataitem["institute_stream"].toString()
            txtStudentName.text = "Uploaded by : "+dataitem["name"].toString()
            if(dataitem["created_at"]  != null){
                txtTimeStamp.text = getDateTimeFirebaseTimestamp(dataitem["created_at"] as Timestamp)
            }

            val fileType = dataitem["file_type"].toString()
            val fileUri = dataitem["file_uri"].toString()
            when(fileType){
                "image" ->{
                    Glide.with(context).load(fileUri).centerCrop().placeholder(R.drawable.profile).into(imgFile)
                }
                "document" ->{
                    imgFile.setImageResource(R.drawable.ic_doc)
                }
                "video" ->{
                    imgFile.setImageResource(R.drawable.ic_doc)
//                    viewVideo.visibility=View.VISIBLE
//                    imgFile.visibility=View.INVISIBLE
//                    viewVideo.setVideoPath(fileUri)
//                    viewVideo.start()
                }
                else ->{
                    imgFile.setImageResource(R.drawable.ic_doc)
                }
            }
        }

    }

}