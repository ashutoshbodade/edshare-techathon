package com.ashutosh.techathon.model

import java.io.Serializable

data class InstitudeModel(
    var instituteuid: String = "",
    val institutename: String = "",
    val instituteaddress: String = "",
): Serializable