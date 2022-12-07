package com.example.mobileteamproj

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.example.mobileteamproj.InputLocationActivity.Companion.ADDRESS_KEY
import com.example.mobileteamproj.InputLocationActivity.Companion.LATITUDE_KEY
import com.example.mobileteamproj.InputLocationActivity.Companion.LONGITUDE_KEY


class InputActivity : AppCompatActivity() {

    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var locationResult: ActivityResultLauncher<Intent>

    private lateinit var toolbar: Toolbar
    private lateinit var cameraButton: ImageButton
    private lateinit var foodImageView: ImageView
    private lateinit var mapButton: Button
    private lateinit var addressText: TextView
    private lateinit var registerButton: Button
    private lateinit var foodNameTextField: AppCompatEditText
    private lateinit var evaluationTextField: AppCompatEditText
    private lateinit var foodNumTextField: TextView
    private lateinit var mealTimeTextField : TextView
    private lateinit var kcalTextFiled : TextView

    private var foodDb:FoodDB?=null

    private var address: String? = null
        set(value) {
            field = value
            addressText.text = value
        }

    private var imageBitmap: Bitmap? = null
        set(value) {
            value?.let {
                field = value
                foodImageView.setImageBitmap(value)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        init()

        initView()

        initListener()

        //DB
        foodDb=FoodDB.getInstance(this)



    }

    @SuppressLint("CheckResult")
    private fun init() {
        galleryResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val imageUri = it.data?.data
                    imageUri?.let {
                        imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(source)
                        }
                    }
                }
            }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val extras: Bundle? = it.data?.extras
                imageBitmap = extras?.get("data") as Bitmap?
            }
        }

        locationResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val data = it.data ?: return@registerForActivityResult
                    // 사용 할거면 사용.
//                    latitude = data.getStringExtra(LATITUDE_KEY)?.toDoubleOrNull()
//                    longitude = data.getStringExtra(LONGITUDE_KEY)?.toDoubleOrNull()
                    address = data.getStringExtra(ADDRESS_KEY)
                }
            }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        cameraButton = findViewById(R.id.camera_button)
        foodImageView = findViewById(R.id.food_image_view)
        mapButton = findViewById(R.id.map_button)
        addressText = findViewById(R.id.address_text)
        registerButton = findViewById(R.id.register_button)
        foodNameTextField = findViewById(R.id.food_name_text_field)
        evaluationTextField = findViewById(R.id.evaluation_text_field)
        foodNumTextField=findViewById(R.id.food_number_text_filed)
        mealTimeTextField=findViewById(R.id.meal_time_text_filed)
        kcalTextFiled=findViewById(R.id.kcal_text_field)
    }

    private fun initListener() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        cameraButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this).apply {
                this.setTitle("음식 사진 입력")
                this.setMessage("방법을 골라주세요")
                this.setPositiveButton("카메라") { _, _ ->
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        cameraResult.launch(takePictureIntent)
                    }
                }
                this.setNegativeButton("갤러리") { _, _ ->
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    galleryResult.launch(intent)
                }
            }
            dialog.show()
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, InputLocationActivity::class.java)
            locationResult.launch(intent)
        }

        registerButton.setOnClickListener {
            val bitmap = imageBitmap ?: run {
                showToast("음식 사진을 입력해주세요.")
                return@setOnClickListener
            }

            val address = if (address.isNullOrEmpty()) run {
                showToast("식사 장소를 입력해주세요.")
                return@setOnClickListener
            } else address!!

            val foodName = if (foodNameTextField.text.isNullOrEmpty()) run {
                showToast("음식 이름을 입력해주세요.")
                return@setOnClickListener
            } else foodNameTextField.text!!

            val foodNum = if (foodNumTextField.text.isNullOrEmpty()) run {
                showToast("음식 수량을 입력해주세요.")
                return@setOnClickListener
            } else foodNameTextField.text!!

            val mealTime = if (mealTimeTextField.text.isNullOrEmpty()) run {
                showToast("식사 시간을 입력해주세요. ex)OOOO-OO-OO")
                return@setOnClickListener
            } else foodNameTextField.text!!

            val totalKcal=if (kcalTextFiled.text.isNullOrEmpty()) run {
                showToast("칼로리 정보를 입력해주세요.")
                return@setOnClickListener
            } else foodNameTextField.text!!

            val evaluation = if (evaluationTextField.text.isNullOrEmpty()) run {
                showToast("음식 평가를 입력해주세요.")
                return@setOnClickListener
            } else foodNameTextField.text


            // DB에 저장
            val r=Runnable{
                //data 읽고 쓸때 쓰레드 사용할 것
                val newfood=Food()
                newfood.foodUrl=bitmap.toString()
                newfood.eatPlace=address.toString()
                newfood.foodName=foodName.toString()
                newfood.foodNum=foodNum.toString().toInt()
                newfood.eatTime=mealTime.toString()
                newfood.foodKcal=totalKcal.toString().toInt()
                newfood.foodComment=evaluation.toString()
            }

            val thread=Thread(r)
            thread.start()

            finish()

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}