package com.trashed.trasheducation.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.trashed.trasheducation.databinding.ActivityMainBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var ImageURI : Uri
    private var state: Boolean = false
    private var result = ""
    private var modelOutput: TensorBuffer? = null
    private var interpreter: Interpreter? = null
    private var modelFile: File? = null
    private var options = Interpreter.Options()
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var reference: StorageReference = storage.getReference()
    private val IMAGE_MEAN = 128
    private val IMAGE_STD = 128.0f

    private var imageBitmap: Bitmap? = null


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
            activityMainBinding.Text2.text = ""
            camera()
        }

        activityMainBinding.SelectImageButton.setOnClickListener {
            activityMainBinding.Text2.text = " "
            gallery()
        }


        activityMainBinding.BackButton.setOnClickListener(){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        activityMainBinding.ArticleButton.setOnClickListener(){
            val text = activityMainBinding.Text2.text
            startActivity(
                Intent(this, ArticleActivity::class.java)
                    .putExtra("label", text)
            )
        }

        //Download model function
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

        if (savedInstanceState != null){
            val mBitmap = savedInstanceState.getParcelable<Bitmap>("bitmap")
            activityMainBinding.PreviewImage.setImageBitmap(mBitmap)
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

    private fun uploadImage(){
        val filename: String? = ImageURI.getLastPathSegment()
        val ref: StorageReference = reference.child("Images/"+ filename)

        ref.putFile(ImageURI)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Successfully Upload", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@MainActivity, "Failed to Upload", Toast.LENGTH_SHORT).show()
            }
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val img = activityMainBinding.PreviewImage
        state = true

        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CODE){
                checkStateLayout(state)
                img.setImageBitmap(data?.extras?.get("data") as Bitmap)
                imageBitmap = data?.extras?.get("data") as Bitmap
            }else if (requestCode == GALLERY_CODE){
                checkStateLayout(state)
                ImageURI = data?.data!!
                activityMainBinding.PreviewImage.setImageURI(ImageURI)
                if (ImageURI != null){
                    val imageStream = applicationContext.contentResolver.openInputStream(ImageURI)
                    val bmpImage = BitmapFactory.decodeStream(imageStream)
                    imageBitmap = bmpImage
                }
            }
        }

        activityMainBinding.PredictButton.setOnClickListener {
            if (resultCode == RESULT_OK){
                if (requestCode == CAMERA_CODE){
                    val bmp = data?.extras?.get("data") as Bitmap
                    val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
                    val now = Date()
                    val fileName: String = formatter.format(now).toString()
                    val file = File(this.cacheDir,"image" + fileName) //Get Access to a local file.
                    file.delete() // Delete the File, just in Case, that there was still another File
                    file.createNewFile()
                    val fileOutputStream = file.outputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
                    val bytearray = byteArrayOutputStream.toByteArray()
                    fileOutputStream.write(bytearray)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    byteArrayOutputStream.close()

                    val URI = file.toUri()
                    ImageURI = URI
                    uploadImage()
                    makePredict(bmp)
                }else if (requestCode == GALLERY_CODE){
                    val uri = data?.data
                    if (uri != null){
                        val imageStream = applicationContext.contentResolver.openInputStream(uri)
                        val bmpImage = BitmapFactory.decodeStream(imageStream)
                        uploadImage()
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
                var real = "$keyLoop"
                Log.i("Info", "The label is $result")
                activityMainBinding.Text2.text = "$real"
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (imageBitmap != null) outState.putParcelable("bitmap", imageBitmap)
    }

}