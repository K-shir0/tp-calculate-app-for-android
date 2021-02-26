package jp.k_shir0.intentwork7

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.graphics.Bitmap

import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

import android.util.Base64
import android.widget.EditText
import jp.k_shir0.intentwork7.dto.ResultData
import jp.k_shir0.intentwork7.dto.ResultEditTextData
import kotlinx.serialization.json.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // EditTextをまとめて管理するDataClass
        val resultInputData = ResultEditTextData(
            findViewById(R.id.editTPNumber),
            findViewById(R.id.editPERFECTNumber),
            findViewById(R.id.editGOODNumber),
            findViewById(R.id.editBADNumber),
            findViewById(R.id.editMISSNumber),
            findViewById(R.id.editBlackPerfectNumber)
        )

        // 写真取得用のButton
        val pickPhotoButton: Button = findViewById(R.id.pickImageButton)

        pickPhotoButton.setOnClickListener {
            Log.d("onClickButton", "ボタンが押されました")
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, PICK_IMAGE)
            Log.i("test", "test")
        }

        // リセットボタン
        val resetButton: Button = findViewById(R.id.resetButton)

        resetButton.setOnClickListener {
            resetValue(resultInputData)
        }

        // 計算ボタン
        val calcButton: Button = findViewById(R.id.calculateButton)

        calcButton.setOnClickListener {
            val result: ResultData = getValue(resultInputData)

            setValue(result, resultInputData)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("PICK_IMAGE", resultCode.toString()) // 0 // -1
        Log.d("PICK_IMAGE", requestCode.toString()) // 1 // 1

        // EditTextをまとめて管理するDataClass
        val resultInputData = ResultEditTextData(
            findViewById(R.id.editTPNumber),
            findViewById(R.id.editPERFECTNumber),
            findViewById(R.id.editGOODNumber),
            findViewById(R.id.editBADNumber),
            findViewById(R.id.editMISSNumber),
            findViewById(R.id.editBlackPerfectNumber)
        )

        val tpEditText: EditText = findViewById(R.id.editTPNumber)
        val perfectEditText: EditText = findViewById(R.id.editPERFECTNumber)
        val goodEditText: EditText = findViewById(R.id.editGOODNumber)
        val badEditText: EditText = findViewById(R.id.editBADNumber)
        val missEditText: EditText = findViewById(R.id.editMISSNumber)
        val blackPerfectEditText: EditText = findViewById(R.id.editBlackPerfectNumber)


        if (requestCode == PICK_IMAGE && resultCode == -1) {
            Log.d("onActivityResult", "写真を選択できました")

            val photo: ImageView = findViewById(R.id.imageView)

            // Uriを取得
            val uri: Uri? = data?.data
            photo.setImageURI(uri)

            Log.d("onActivityResult", uri.toString())

            val imageStream = contentResolver.openInputStream(uri!!)
            val bitmapImage: Bitmap = BitmapFactory.decodeStream(imageStream)


            // 写真をBase64にする
            val baos = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val encImage: String = Base64.encodeToString(b, Base64.DEFAULT)

            Log.d("Base64", encImage)
            Log.d("URI", uri.toString())

            val asyncHttpTask = PostBmpAsyncHttpRequest()

            val test = asyncHttpTask.execute(encImage)

            val result = Json.decodeFromString(ResultData.serializer(), test.get())

            setValue(result, resultInputData)
        }
    }

    private fun resetValue(
        resultEditTextData: ResultEditTextData
    ) {
        resultEditTextData.tpEditText.setText("")
        resultEditTextData.perfectEditText.setText("")
        resultEditTextData.goodEditText.setText("")
        resultEditTextData.badEditText.setText("")
        resultEditTextData.missEditText.setText("")
        resultEditTextData.blackPerfectEditText.setText("")
    }

    private fun getValue(
        resultEditTextData: ResultEditTextData
    ): ResultData {
        return ResultData(
            resultEditTextData.tpEditText.text.toString().toFloatOrNull() ?: 0F,
            resultEditTextData.perfectEditText.text.toString().toIntOrNull() ?: 0,
            resultEditTextData.goodEditText.text.toString().toIntOrNull() ?: 0,
            resultEditTextData.badEditText.text.toString().toIntOrNull() ?: 0,
            resultEditTextData.missEditText.text.toString().toIntOrNull() ?: 0,
        )
    }

    private fun setValue(
        result: ResultData,
        resultEditTextData: ResultEditTextData
    ) {

        resultEditTextData.tpEditText.setText(result.tp.toString())
        resultEditTextData.perfectEditText.setText(result.perfect.toString())
        resultEditTextData.goodEditText.setText(result.good.toString())
        resultEditTextData.badEditText.setText(result.bad.toString())
        resultEditTextData.missEditText.setText(result.miss.toString())

        resultEditTextData.blackPerfectEditText.setText(
            calculate(result).toString()
        )
    }


    private fun calculate(result: ResultData): Int {
        val totalNote = result.perfect + result.good + result.bad + result.miss

        var blackPerfect: Int = 0

        if (result.tp.toInt() == 100) {
            blackPerfect = ((100 - result.tp) / 30 * totalNote).roundToInt()
        } else {
            blackPerfect = (
                    (totalNote * (100 - result.tp)) / 30 - (1 / 3) * (7 * result.good + 10 * (result.miss + result.bad))
                    ).roundToInt()
        }

        return blackPerfect
    }
}
