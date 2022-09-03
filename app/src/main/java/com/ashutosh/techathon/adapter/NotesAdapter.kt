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
import com.ashutosh.techathon.utils.Constants.mAuth
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QueryDocumentSnapshot

class NotesAdapter(val list: ArrayList<QueryDocumentSnapshot>, val context: Context) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    val userID = mAuth.currentUser!!.uid

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

        holder.itemView.setOnClickListener {

        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgUser = itemView.findViewById<ImageView>(R.id.imgUser)


        fun bindItems(dataitem: QueryDocumentSnapshot,context:Context)
        {





        }

    }

}