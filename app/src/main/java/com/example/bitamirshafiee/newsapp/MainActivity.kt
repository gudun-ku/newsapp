package com.example.bitamirshafiee.newsapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    val TAG: String = this.javaClass.simpleName
    val apikey: String = "f1618ca6-017d-41e1-95b8-cf1867d80cabde"


    var listData = ArrayList<Data>()
    var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun searchWord(view: View) {
        pageNumber = 1
        val stringUrl = "https://content.guardianapis.com/search?q=${editText?.text}&tag=politics/politics&api-key=$apikey&page=$pageNumber"

        listData.clear()
        var myAsyncTask = MyAsyncTask()
        myAsyncTask.execute(stringUrl)
    }

    fun loadMore(view: View) {
        pageNumber += 1
        val stringUrl = "https://content.guardianapis.com/search?q=${editText?.text}&tag=politics/politics&api-key=$apikey&page=$pageNumber"

        val myAsyncTask = MyAsyncTask()
        myAsyncTask.execute(stringUrl)
    }

    inner class MyAsyncTask : AsyncTask<String, Void, ArrayList<Data>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Data> {
            var url = createUrl(params[0])

            var jsonResponse:String? = ""
            try {
              jsonResponse = makeHttpRequest(url)
            } catch (e: IOException){
                Log.e("MainActivity", "Problems making HTTP request $e")
            }

            return  extractFeaturesFromResponse(jsonResponse)
        }

        override fun onPostExecute(result: ArrayList<Data>?) {
            if (result != null) {
                    updateUi(result)
                }
        }
    }

    fun updateUi(list: ArrayList<Data>) {
        listView?.adapter = NewsAdapter(this, list)
    }


    fun extractFeaturesFromResponse(guardianJson: String?):ArrayList<Data> {
        try {
            val baseJson = JSONObject(guardianJson)
            val response = baseJson.getJSONObject("response")
            val newsArray = response.getJSONArray("results")
            for (i in 0..9) {
                val item = newsArray.getJSONObject(i)
                val sectionName = item.getString("sectionName")
                val webTitle = item.getString("webTitle")
                val webUrl = item.getString("webUrl")

                val data = Data(sectionName,webTitle,webUrl)
                listData.add(data)

            }
        } catch (e: JSONException) {
            Log.e(TAG,"Problem parsing the news JSON results $e")
        }

        return listData
    }

    fun makeHttpRequest(url: URL?): String {
        var jsonResponse: String = ""
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            urlConnection = url?.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("Accept", "application/json")
            urlConnection.setRequestProperty("api-key","f1618ca6-017d-41e1-95b8-cf1867d80cabde")
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.connect()

            if (urlConnection?.responseCode == 200) {
                inputStream = urlConnection?.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.d("MainActivity", "Response code: ${urlConnection?.responseCode}")
            }

            urlConnection.disconnect()
            inputStream?.close()

        } catch (e: IOException) {
            Log.e("MainActivity", "Error response code: ${urlConnection?.responseCode}")
        }

        return jsonResponse

    }

    fun readFromStream(stream: InputStream):String {
        var output = StringBuilder()
        val inputStreamReader = InputStreamReader(stream, Charset.forName("UTF-8"))
        val reader = BufferedReader(inputStreamReader)
        var line: String? = reader.readLine()
        while(line != null) {
            output.append(line)
            line = reader.readLine()
        }
        return output.toString()
    }

    fun createUrl(stringUrl: String?): URL?  {
        var url: URL?
        try {
            url = URL(stringUrl);
        } catch (e: MalformedURLException) {
            Log.e("MainActivity", "Error with creating url$e")
            return null
        }

        return url
    }
}
