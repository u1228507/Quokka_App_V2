package com.example.quokka_app.ui.newpatientprofile

data class NewPatienProfileDataClass(
    val firstname: String,
    val middlename: String,
    val lastname: String,
    val dateofbirth: String,
    val mothersvillage: String,
    val lastmenstcycle: String?,
    val lastmenstcycledate: String,
    val mothersphonenumber: String,
    val fathersfirstname: String,
    val fathersmiddlename: String,
    val fatherslastname: String,
    val fathersvillage: String,
    val fathersphonenumber: String,
    val fchwfirstname: String,
    val fchwlastname: String,
    val fchwphonenumber: String,
    val motherbirthdefect: String?,
    val motherbirthdefecttype: String,
    val firstpregnancy: String?,
    val numpregn: String,
    val livingchildren: String,
    val lowbirthweight: String,
    val stillborns: String,
    val miscarriages: String,
    val csections: String,
    val postpartumhemorrhages: String,
    val preginfections: String,
    val highBPpregn: String,
    val pregseizures: String?,
    val othermedhist: String,
    val alcoholconsump: String?,
    val smoking: String?,
    val drugs: String?,
    val drugtypes: String,
    var imageUrl: String? = null,
)
