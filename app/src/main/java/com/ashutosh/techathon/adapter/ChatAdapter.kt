package com.ashutosh.techathon.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.techathon.R
import com.ashutosh.techathon.model.ChatDataModel
import com.ashutosh.techathon.utils.Constants.mAuth
import com.ashutosh.techathon.utils.TimestampUtil
import com.bumptech.glide.Glide

class ChatAdapter(val itemListData: ArrayList<ChatDataModel>,val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflatedView: View

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            inflatedView = parent.inflate(R.layout.row_msg_send, false)
            return SendViewHolder(inflatedView)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            inflatedView = parent.inflate(R.layout.row_msg_receive, false)
            return ReceiveViewHolder(inflatedView)
        }
        inflatedView = parent.inflate(R.layout.row_msg_send, false)

        return ReceiveViewHolder(inflatedView)

    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return itemListData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemListData[position].from == mAuth.currentUser!!.uid)
            VIEW_TYPE_MESSAGE_SENT
        else
            VIEW_TYPE_MESSAGE_RECEIVED
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.itemView.tag=position
        holder.setIsRecyclable(false)

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SendViewHolder).bindItems(itemListData[position], context)
                holder.imgDownload.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(itemListData[position].file_uri.toString())
                    context.startActivity(i)
                }
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceiveViewHolder).bindItems(itemListData[position],context)
                holder.imgDownload.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(itemListData[position].file_uri.toString())
                    context.startActivity(i)
                }
            }
        }


    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName = itemView.findViewById<TextView>(R.id.txtUserName)
        val txtMsg = itemView.findViewById<TextView>(R.id.txtMsg)
        val txtTimestamp = itemView.findViewById<TextView>(R.id.txtTimestamp)

        val noteView = itemView.findViewById<ConstraintLayout>(R.id.noteView)
        var txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        var imgFile = itemView.findViewById<ImageView>(R.id.imgFile)
        var imgDownload = itemView.findViewById<ImageView>(R.id.imgDownload)

        fun bindItems(dataitem: ChatDataModel,context:Context) {
            txtMsg.text= dataitem.message
            if(dataitem.sendTime!=null){
                when(dataitem.status){
                    0 ->{
                        txtTimestamp.text = TimestampUtil.getDateTimeFirebaseTimestamp(dataitem.sendTime) + " Send"
                    }
                    1 ->{
                        txtTimestamp.text =TimestampUtil.getDateTimeFirebaseTimestamp(dataitem.sendTime) + " Del"
                    }
                    2 ->{
                        txtTimestamp.text =TimestampUtil.getDateTimeFirebaseTimestamp(dataitem.sendTime) + " Read"
                    }
                }

            }

            if(dataitem.type == 1){
                noteView.visibility=View.VISIBLE
                txtMsg.visibility=View.GONE

                txtTitle.text = dataitem.title.toString()+" ("+""+dataitem.note_type+")"

                when(dataitem.file_type){
                    "image" ->{
                        Glide.with(context).load(dataitem.file_uri).centerCrop().placeholder(R.drawable.ic_placeholder).into(imgFile)
                    }
                    "document" ->{
                        imgFile.setImageResource(R.drawable.ic_doc)
                    }
                    "video" ->{
                        imgFile.setImageResource(R.drawable.ic_video)
                    }
                    else ->{
                        imgFile.setImageResource(R.drawable.ic_doc)
                    }
                }
            }
        }

    }


    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName = itemView.findViewById<TextView>(R.id.txtUserName)
        val txtMsg = itemView.findViewById<TextView>(R.id.txtMsg)
        val txtTimestamp = itemView.findViewById<TextView>(R.id.txtTimestamp)

        val noteView = itemView.findViewById<ConstraintLayout>(R.id.noteView)
        var txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        var imgFile = itemView.findViewById<ImageView>(R.id.imgFile)
        var imgDownload = itemView.findViewById<ImageView>(R.id.imgDownload)

        fun bindItems(dataitem: ChatDataModel,context: Context) {
            txtMsg.text= dataitem.message

            if(dataitem.sendTime!=null){
                txtTimestamp.text =TimestampUtil.getDateTimeFirebaseTimestamp(dataitem.sendTime)
            }

            if(dataitem.type == 1){
                noteView.visibility=View.VISIBLE
                txtMsg.visibility=View.GONE

                txtTitle.text = dataitem.title.toString()+" ("+""+dataitem.note_type+")"

                when(dataitem.file_type){
                    "image" ->{
                        Glide.with(context).load(dataitem.file_uri).centerCrop().placeholder(R.drawable.ic_placeholder).into(imgFile)
                    }
                    "document" ->{
                        imgFile.setImageResource(R.drawable.ic_doc)
                    }
                    "video" ->{
                        imgFile.setImageResource(R.drawable.ic_video)
                    }
                    else ->{
                        imgFile.setImageResource(R.drawable.ic_doc)
                    }
                }
            }

        }
    }


}