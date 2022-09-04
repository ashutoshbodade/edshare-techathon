package com.ashutosh.techathon.model

import java.io.Serializable

data class UserDataModel(
    val email: String = "",
    val gender: String = "",
    val profile_image: String = "",
    var uid: String = "",
    val name: String = "",
    val instituteid: String = "",
    var instituteuid: String = "",
    val institutename: String = "",
    val institutestream: String = "",
):Serializable