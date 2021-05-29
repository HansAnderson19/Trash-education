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

        val btnStart = activityMenuBinding.buttonStart
        btnStart.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}