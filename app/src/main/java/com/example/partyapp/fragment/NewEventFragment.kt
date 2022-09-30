package com.example.partyapp.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.partyapp.R
import com.example.partyapp.service.NewEventService
import com.example.partyapp.utils.setPreferenceObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_new_event.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class NewEventFragment : Fragment() {
    lateinit var newEventService: NewEventService
    private val GALLERY = 111
    private val CAMERA = 112
    private val PERMISSION_ALL = 2
    private lateinit var pref: SharedPreferences
    var downLoadUrl = ""

    private var PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private var pathPhoto = ""
    private lateinit var wallpaperDirectory: File
    private var resizedPhoto: Bitmap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newEventService = NewEventService(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val des: EditText = view.findViewById(R.id.desc_new)
        val tit: EditText = view.findViewById(R.id.title_new)
        val image: ImageView = view.findViewById(R.id.imageView2)

        val remoteDB = Firebase.firestore

        pref = requireContext().getSharedPreferences(
            "sharedPreferences",
            AppCompatActivity.MODE_PRIVATE
        )

        new_event.setOnClickListener {
            newEventService.saveEventName(tit.text.toString())
            newEventService.saveEventDescr(des.text.toString())
            if (resizedPhoto != null) {
                setPreferenceObject(requireContext(), resizedPhoto, "photo", pref)
            }

            val event = hashMapOf(
                "imageUrl" to downLoadUrl,
                "title" to tit.text.toString(),
                "description" to des.text.toString()
            )

            remoteDB.collection("event").add(event).addOnSuccessListener {
            }

            fragmentManager?.popBackStack()
        }

        add_photo.setOnClickListener {
            new_event.isEnabled = false
            uploadPhoto()
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        if (requestCode == GALLERY) {
            if (data != null) {

                try {
                    var bitmap: Bitmap? = null

                    val selectedPhotoUri = data.data

                    try {
                        if (selectedPhotoUri != null) {

                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().contentResolver,
                                    selectedPhotoUri
                                )
                            } else {

                                val options = BitmapFactory.Options()
                                bitmap = BitmapFactory.decodeStream(
                                    requireContext().contentResolver.openInputStream(
                                        selectedPhotoUri
                                    ),
                                    null,
                                    options
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (bitmap != null) {

                        val input = requireContext().contentResolver.openInputStream(data.data!!)
                        var newBitmap = bitmap

                        if (input != null) {
                            val exif =
                                ExifInterface(input)

                            if (exif.getAttribute(ExifInterface.TAG_MODEL) == "SM-A315F") {
                                val matrix = Matrix()
                                matrix.postRotate(90F)
                                newBitmap = Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    0,
                                    bitmap.width,
                                    bitmap.height,
                                    matrix,
                                    true
                                )
                            }

                            input.close()
                        }


                        pathPhoto = saveImage(newBitmap!!)

                        val scaleBitmap = scaleCenterCrop(newBitmap, 800, 800)

                        resizedPhoto = Bitmap.createScaledBitmap(scaleBitmap!!, 800, 800, true)

                        Glide.with(this).load(resizedPhoto!!).into(imageView2)

                        val storage = FirebaseStorage.getInstance();
                        val storageRef = storage.reference
                        val mountainsRef = storageRef.child(
                            "${
                                (Calendar.getInstance()
                                    .timeInMillis)
                            }.jpg"
                        )
                        imageView2.isDrawingCacheEnabled = true
                        imageView2.buildDrawingCache()
                        val bitmap = resizedPhoto
                        val baos = ByteArrayOutputStream()
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()

                        val uploadTask = mountainsRef.putBytes(data)
                        uploadTask.addOnFailureListener {

                        }.addOnSuccessListener { taskSnapshot ->
                            mountainsRef.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    downLoadUrl = task.result.toString()
                                    new_event.isEnabled = true
                                } else {
                                    new_event.isEnabled = true
                                    downLoadUrl = ""
                                }
                            }
                        }

                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    }

    private fun uploadPhoto() {
        setupPermission()
    }


    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun setupPermission() {
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this.requireActivity(), PERMISSIONS, PERMISSION_ALL)
        } else {
            //showPictureDialog()
            choosePhotoFromGallery()
        }
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun saveImage(myBitmap: Bitmap): String {
        val code = requireContext().packageManager.checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            requireContext().packageName
        )
        if (code != PackageManager.PERMISSION_GRANTED) {
            setupPermission()
        }
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + "/Photo"
        )
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()

            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {

            e1.printStackTrace()
        }

        return ""
    }

    private fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val sourceWidth = source.width
        val sourceHeight = source.height

        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(newWidth, newHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
    }


}