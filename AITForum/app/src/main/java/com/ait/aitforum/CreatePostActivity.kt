package com.ait.aitforum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ait.aitforum.data.Post
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE = 102
    }

    var uploadBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        btnSend.setOnClickListener {
            if (uploadBitmap != null) {
                try {
                    uploadPostWithImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else {
                    /* upload has optional parameter */
                    uploadPost()
                }
            }

        btnAttach.setOnClickListener() {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
        }
        requestNeededPermission()
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(this,
                    "I need it for camera", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
        } else {
            btnAttach.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "CAMERA perm granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "CAMERA perm NOT granted", Toast.LENGTH_SHORT).show()
                    btnAttach.visibility = View.GONE
                }
            }
        }
    }

    private fun uploadPost(imageUrl: String = "", imageName : String = "") {
        var post = Post(
            /* user id */
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.displayName!!,
            etTitle.text.toString(),
            etBody.text.toString(),
            imageUrl,
            imageName
        )

        /* collection is the folder in database */
        var postSCollection = FirebaseFirestore.getInstance().collection("posts")
        postSCollection.add(post).addOnSuccessListener {
            Toast.makeText(this@CreatePostActivity, "Success creating post", Toast.LENGTH_LONG).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@CreatePostActivity, "Error : ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    /* Taking care of taking photo and preview */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* if requestCode = requestCode we passed in start activity
        *  AND result_ok (if user actually take the camera)
        * Then save the bitmap to uploadBitMap
        * */
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                uploadBitmap = data!!.extras!!.get("data") as Bitmap
                imgAttach.setImageBitmap(uploadBitmap)
                imgAttach.visibility = View.VISIBLE
        }
    }

    @Throws(Exception::class)
    private fun uploadPostWithImage() {

        val baos = ByteArrayOutputStream()
        /* convert to jpeg file */
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        /* upload this to server */
        val imageInBytes = baos.toByteArray()

        /* connect to firebase */
        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        /* new image go inside this image foler */
        val newImagesRef = storageRef.child("images/$newImage")

        /* put bytes in newImagesRef file */
        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener {
                Toast.makeText(this@CreatePostActivity, it.message, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                newImagesRef.downloadUrl.addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        /* link of the file to download */
                        uploadPost(task.result.toString())
                    }
                })
            }
    }



}
