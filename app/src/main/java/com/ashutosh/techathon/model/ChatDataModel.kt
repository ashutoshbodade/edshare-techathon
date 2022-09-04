package com.ashutosh.techathon.model

import com.google.firebase.Timestamp
import java.io.Serializable


data class ChatDataModel(
    val to:String="",
    val from:String="",
    val message:String="",
    val deliveredTime:Timestamp?=null,
    val readTime:Timestamp?=null,
    val sendTime: Timestamp? = null,
    val type:Int=0,//0 text, 1 post
    val status:Int=0,//0 send, 1 delivered, 2 read,
    val file_type:String = "",
    val note_type:String = "",
    val title:String = "",
    val file_uri:String = "",

):Serializable
