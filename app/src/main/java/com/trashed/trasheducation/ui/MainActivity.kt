package com.trashed.trasheducation.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trashed.trasheducation.databinding.ActivityMainBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity: AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private var state: Boolean = false
    private var result = ""
    private var modelOutput: TensorBuffer? = null
    private var interpreter: Interpreter? = null
    private var modelFile: File? = null
    private var options = Interpreter.Options()
    private val IMAGE_MEAN = 128
    private val IMAGE_STD = 128.0f

    companion object{
        const val CAMERA_CODE = 98
        const val GALLERY_CODE = 99
        const val REQUEST_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        checkStateLayout(state)

        activityMainBinding.TakePictureButton.setOnClickListener {
            camera()
        }

        activityMainBinding.SelectImageButton.setOnClickListener {
            gallery()
        }

    }

    private fun camera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, CAMERA_CODE)
            }
        }
    }

    private fun gallery(){
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, GALLERY_CODE)
            }
        }
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val img = activityMainBinding.PreviewImage
        state = true

        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CODE){
                checkStateLayout(state)
                img.setImageBitmap(data?.extras?.get("data") as Bitmap)
            }else if (requestCode == GALLERY_CODE){
                checkStateLayout(state)
                img.setImageURI(data?.data)
            }
        }

        activityMainBinding.PredictButton.setOnClickListener {
            if (resultCode == RESULT_OK){
                if (requestCode == CAMERA_CODE){
                    val bmp = data?.extras?.get("data") as Bitmap
                    makePredict(bmp)
                }else if (requestCode == GALLERY_CODE){
                    val uri = data?.data
                    if (uri != null){
                        val imageStream = applicationContext.contentResolver.openInputStream(uri)
                        val bmpImage = BitmapFactory.decodeStream(imageStream)
                        makePredict(bmpImage)
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun makePredict(bmp: Bitmap?) {
        //Processing and Resizing Picture
        if (bmp != null){
            val resized = Bitmap.createScaledBitmap(bmp, 256, 256, true)
            val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 256 * 256 * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(256 * 256)
            resized.getPixels(intValues, 0, resized.width, 0, 0, resized.width, resized.height)
            var pixel = 0
            for (i in 0..255) {
                for (j in 0..255) {
                    val finals = intValues[pixel++]
                    byteBuffer.putFloat(((finals shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    byteBuffer.putFloat(((finals shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    byteBuffer.putFloat(((finals and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
            //Get model input
            modelOutput = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
            interpreter?.run(byteBuffer, modelOutput?.buffer)
            //Get Label Class
            val TRASH_LABELS = "label.txt"
            var trashlabels: List<String?>? = null
            try {
                trashlabels = FileUtil.loadLabels(applicationContext, TRASH_LABELS)
            } catch (e: IOException) {
                Log.e("tfliteSupport", "Error reading label file", e)
            }
            //Predict Process
            val probabilityProcessor = TensorProcessor.Builder()
                .add(CastOp(DataType.FLOAT32))
                .build()
            if (trashlabels != null) {
                // Map of labels and their corresponding probability
                val labels = TensorLabel(trashlabels, probabilityProcessor.process(modelOutput))
                // Create a map to access the result based on label
                val resultsMap = labels.mapWithFloatValue
                var max = 0f
                var keyLoop = ""
                for (key in resultsMap.keys) {
                    val value = resultsMap[key]
                    val values = value
                    if (values != null){
                        if (max < values){
                            max = values
                            keyLoop = key
                        }
                    }
                    Log.i("Info", "$key $value")
                }
                val roundOff = String.format("%.2f", max)
                result = "$keyLoop $roundOff"
                Log.i("Info", "The label is $result")
                activityMainBinding.Text2.append(result)
                modelOutput = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
            }
        }
    }

    private fun checkStateLayout(state: Boolean){
        if (state){
            activityMainBinding.PredictButton.visibility = View.VISIBLE
            activityMainBinding.Text2.visibility = View.VISIBLE
            activityMainBinding.Text3.visibility = View.VISIBLE
            activityMainBinding.ArticleButton.visibility = View.VISIBLE
        }else{
            activityMainBinding.PredictButton.visibility = View.INVISIBLE
            activityMainBinding.Text2.visibility = View.INVISIBLE
            activityMainBinding.Text3.visibility = View.INVISIBLE
            activityMainBinding.ArticleButton.visibility = View.INVISIBLE
        }
    }

}