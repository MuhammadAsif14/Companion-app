//package com.example.companionek
//
//
//class Users {
//    var profilepic: String? = null
//    var mail: String? = null
//    var userName: String? = null
//    var password: String? = null
//    var userId: String? = null
//
//
//    constructor()
//    constructor(
//        userId: String?,
//        userName: String?,
//        maill: String?,
//        password: String?,
//        profilepic: String?,
//    ){
//        this.userId = userId
//        this.userName = userName
//        mail = maill
//        this.password = password
//        this.profilepic = profilepic
//    }
//
//}

package com.example.companionek

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Users(
    var userId: String? = null,
    var userName: String? = null,
    var mail: String? = null,
    var password: String? = null,
    var status:String?="offline",
    var profilepic: String? = null
) : Parcelable{


}

