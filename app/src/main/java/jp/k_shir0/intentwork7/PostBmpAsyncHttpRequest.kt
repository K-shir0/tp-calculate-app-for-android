package jp.k_shir0.intentwork7

import android.os.AsyncTask
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.lang.Exception


class PostBmpAsyncHttpRequest() : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {

        // 通信処理
        // TODO urlを追加
        val url: String = ""

        val mediaType: MediaType = MediaType.parse("application/json")!!

        val json = JSONObject()
        json.put("result_image", params[0].toString())

        // リクエストのボディ
        val requestBody: RequestBody = RequestBody.create(mediaType, json.toString())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .build()

        try {
            val response: Response = okHttpClient.newCall(request).execute()

            val responseCode = response.code()

            if (!response.isSuccessful) {
                println("error!!");
            }
            if (response.body() != null) {
                // 一度しか呼べない
//                println("body: " + response.body()!!.string())

                Log.i("返却", "test")
                return response.body()!!.string()
            }

        } catch (e: Exception) {
            Log.d("error", e.toString())
        }


        return "test"
    }

}
