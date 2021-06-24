package com.plogging.ecorun.util.extension

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.plogging.ecorun.R
import io.reactivex.Single

fun googleIntent(context: Context, fragment: FragmentActivity): Intent {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(fragment.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    return googleSignInClient.signInIntent
}

fun googleLoginSingle(context: Context, fragment: Fragment): Single<String> {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(fragment.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    return Single.create { emitter ->
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                emitter.onSuccess(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSingle", "Google sign in failed", e)
            }
        }.launch(googleSignInClient.signInIntent)
    }
}

fun firebaseAuthWithGoogle(idToken: String, fragment: FragmentActivity): Single<FirebaseUser?> {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    return Single.create { emitter ->
        auth.signInWithCredential(credential)
            .addOnCompleteListener(fragment) { task ->
                if (task.isSuccessful) emitter.onSuccess(auth.currentUser!!)
            }
    }
}





