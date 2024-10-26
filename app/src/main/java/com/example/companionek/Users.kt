package com.example.companionek


class Users {
    var profilepic: String? = null
    var mail: String? = null
    var userName: String? = null
    var password: String? = null
    var userId: String? = null
    

    constructor()
    constructor(
        userId: String?,
        userName: String?,
        maill: String?,
        password: String?,
        profilepic: String?,
    ) {
        this.userId = userId
        this.userName = userName
        mail = maill
        this.password = password
        this.profilepic = profilepic
    }

}