package com.example.admin.yandextranslator

import android.content.Context
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class TranslatorBackgroundTask
internal constructor(
        private var ctx: Context) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String? {
        //String variables
        val textToBeTranslated = params[0]
        val languagePair = params[1]

        try {
            //Set up the translation call URL            
            val yandexKey = "trnsl.1.1.20190927T103645Z.cbd9cbbc6495380f.a5dfc1bc0e3bc94e0aedf16913b741b6ea0079d3"
            val yandexUrl = ("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                    + "&text=" + textToBeTranslated + "&lang=" + languagePair)
            val yandexTranslateURL = URL(yandexUrl)

            //Set Http Conncection, Input Stream, and Buffered Reader
            val httpJsonConnection = yandexTranslateURL.openConnection() as HttpURLConnection
            val inputStream = httpJsonConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            //Read Text
            var resultString = bufferedReader.readText()

            //Close and disconnect
            bufferedReader.close()
            inputStream.close()
            httpJsonConnection.disconnect()

            //Making answer readable
            resultString = resultString.substring(resultString.indexOf("[")+2,resultString.indexOf("]")-1)

            //Log.d("Translation Result in background:", resultString)
            return resultString

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(result: String) {}

    override fun onProgressUpdate(vararg values: Void) {
        super.onProgressUpdate(*values)
    }
}
