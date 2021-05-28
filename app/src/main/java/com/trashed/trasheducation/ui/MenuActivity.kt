package com.trashed.trasheducation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.trashed.trasheducation.databinding.ActivityMenuBinding
import org.tensorflow.lite.Interpreter
import java.io.File

class MenuActivity : AppCompatActivity() {

    private lateinit var activityMenuBinding: ActivityMenuBinding
    private var interpreter: Interpreter? = null
    private var modelFile: File? = null
    private var options = Interpreter.Options()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMenuBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(activityMenuBinding.root)

        val remoteModel = FirebaseCustomRemoteModel.Builder("trash_edu").build()
        Log.d("Info",remoteModel.toString())
        val conditions = FirebaseModelDownloadConditions.Builder()
            .build()
        Log.d("Info", conditions.toString())
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
            .addOnSuccessListener {
                Log.i("Info", "Switching to download model")
                FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                    .addOnCompleteListener {
                        modelFile = it.result
                        val model = modelFile
                        if (model != null){
                            interpreter = Interpreter(model, options)
                        }
                    }
            }
            .addOnFailureListener {
                Log.i("Info","Failed to download model")
            }

        val btnStart = activityMenuBinding.buttonStart
        btnStart.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}