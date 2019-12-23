package com.elevintech.rotateimageintent

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import com.github.florent37.runtimepermission.RuntimePermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        button1.setOnClickListener {
            askPermission()
        }

        button2.setOnClickListener{
            askPermissionNormal()
        }
    }

    fun askPermission(){

        RuntimePermission.askPermission(this)
            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .onAccepted{
                // DO YOUR ACTIONS HERE

                // SAMPLE ACTION 1
//                openGallery()

                // SAMPLE ACTION 2
                openCamera()
            }
            .ask()

    }

    fun askPermissionNormal(){

        RuntimePermission.askPermission(this)
            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .onAccepted{
                // DO YOUR ACTIONS HERE

                // SAMPLE ACTION 1
//                openGallery()

                // SAMPLE ACTION 2
                openCameraNormal()
            }
            .ask()

    }

    fun openGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }

    fun openCamera(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var file = File(this.externalCacheDir, "[FILE NAME]")
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 1)

    }

    fun openCameraNormal(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var file = File(this.externalCacheDir, "[FILE NAME]")
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 2)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            val bitmap = uriStringToBitmap(imageUri.toString())
            val rotatedBitmap = RotateBitmapAuto(bitmap)
            imageView1.setImageBitmap(rotatedBitmap)
        } else if (requestCode == 2){
            imageView1.setImageURI(imageUri)
        }



    }

    fun uriStringToBitmap(uriString : String) : Bitmap {

        val uri = uriString.toUri()
        val uriPath = uri.path
        val bitmap = BitmapFactory.decodeFile(uriPath)

        return bitmap

    }

    fun RotateBitmapAuto(source: Bitmap) : Bitmap{

        // TODO minimum API for this function is 24+, need to find solution for < 24 API phones
        val new = contentResolver.openInputStream(imageUri!!);
        var exifInterface = ExifInterface(new)

        var orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        var rotation = 0

        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> rotation = 0
            ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
        }

        println("rotation is: " + rotation.toString())

        var matrix = Matrix()
        matrix.setRotate(rotation.toFloat())

        var bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return bitmap

    }
}
