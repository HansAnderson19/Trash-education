package com.trashed.trasheducation.data.source.remote

import android.util.Log
import com.google.firebase.database.*
import com.trashed.trasheducation.data.source.remote.response.ArticleResponse

class RemoteDataSource {

    fun getAllArticle(label: String ,callback: LoadArticleCallBack){
        val reff: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Article").child(label)
        reff.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val explanation = snapshot.child("explaination").getValue().toString()
                val impact = snapshot.child("impact").getValue().toString()
                val overcome = snapshot.child("overcome").getValue().toString()
                val link1 = snapshot.child("link1").getValue().toString()
                val link2 = snapshot.child("link2").getValue().toString()
                val video = snapshot.child("video").getValue().toString()

                val list = ArticleResponse(explanation, impact, overcome, link1, link2, video)
                callback.onArticleReceived(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error","error : "+error.message)
            }
        })
    }

    interface LoadArticleCallBack{
        fun onArticleReceived(articleResponse: ArticleResponse)
    }

}