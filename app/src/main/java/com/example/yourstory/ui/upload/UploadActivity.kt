package com.example.yourstory.ui.upload

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityUploadBinding
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.network.remote.responses.RegisterResponse
import com.example.yourstory.network.remote.retrofit.ApiConfig
import com.example.yourstory.rotateFile
import com.example.yourstory.ui.Home.HomeActivity
import com.example.yourstory.ui.camera.CameraActivity
import com.example.yourstory.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val MAXIMAL_SIZE = 1000000
    private lateinit var dataStore: DataStore<Preferences>
    private var isLoading: Boolean = false

    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var includeLocation: Boolean = false
    private var userLastPosition: Location? = null

    companion object {
        const val CAMERA_X_RESULT = 200
        const val REQUEST_CODE_GALLERY = 100
        const val REQUEST_CODE_LOCATION_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStore = SingletonDatastore.getInstance(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.camerax.setOnClickListener { startCameraX() }
        binding.library.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            includeLocation = isChecked
            getMyLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startActivityForResult(chooser, REQUEST_CODE_GALLERY)
    }

    private fun uploadImage() {
        isLoading = true
        showLoading(isLoading)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            var uploadImageRequest: Call<RegisterResponse>

            val apiService = ApiConfig.getApiService(dataStore)
            var longitude = userLastPosition?.longitude.toString()?:"null"
            var latitude = userLastPosition?.latitude.toString()?:"null"
            Log.d("Check Position", "longitude ${longitude}")
            Log.d("Check Position", "latitude ${latitude}")

            if(includeLocation){
                uploadImageRequest = apiService.addStory(imageMultipart, description, longitude.toRequestBody(), latitude.toRequestBody())
            }else{
                uploadImageRequest = apiService.addStory(imageMultipart, description, null, null)
            }

            uploadImageRequest.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    isLoading = false
                    showLoading(isLoading)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@UploadActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@UploadActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@UploadActivity, response.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    isLoading = false
                    showLoading(isLoading)
                    Toast.makeText(this@UploadActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            isLoading = false
            showLoading(isLoading)
            Toast.makeText(
                this@UploadActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            val selectedImg = data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun showLoading(isLoading: Boolean) {
        binding.buttonAdd.isEnabled = !isLoading
        if (isLoading) {
            binding.buttonAdd.text = getString(R.string.loading)
        } else {
            binding.buttonAdd.text = getString(R.string.upload)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLastPosition = location
                    Log.d("check position", "position: ${userLastPosition.toString()}")
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}