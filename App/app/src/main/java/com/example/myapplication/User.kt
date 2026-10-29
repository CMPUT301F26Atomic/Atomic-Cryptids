package com.atomiccryptids.campsiteapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue

interface User {
    val role: String
    fun saveUser() {}


    fun optNotification(boolean: Boolean): String {
        return if (boolean) "user will receive notification" else "user won't receive notifications"
        /*
        I am too tired and its late. I'll fix this part later
         */
    }
    fun deleteProfile(){}
}