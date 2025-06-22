package com.colman.dailypulse.features.user

import com.colman.dailypulse.domian.user.OnSignIn
import com.colman.dailypulse.domian.user.OnSignUp

class UserUseCases (
    val signIn: OnSignIn,
    val signUp: OnSignUp,
//    val signOut: OnSignOut,
)