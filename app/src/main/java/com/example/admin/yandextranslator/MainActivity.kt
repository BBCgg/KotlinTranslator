package com.example.admin.yandextranslator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val translateButton = findViewById<ImageButton>(R.id.translateBtn)
        val text_for_translate = findViewById<TextInputEditText>(R.id.text_for_translate)
        val TVtranslator = findViewById<TextView>(R.id.TVtranslator)
        val progress_bar = findViewById<ProgressBar>(R.id.progress_bar)
        val spinner1 = findViewById<Spinner>(R.id.languages1)
        val spinner2 = findViewById<Spinner>(R.id.languages2)
        val switchLanguages = findViewById<ImageButton>(R.id.switchLanguages)
        val translationHistoryBtn = findViewById<Button>(R.id.translationHistoryBtn)

        var languagePair = "en-ru" //default language pair

        val context = this
        var db = DataBaseHundler(context)

        val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault())
        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
            }
            override fun onBeginningOfSpeech() {
            }
            override fun onRmsChanged(v: Float) {
            }
            override fun onBufferReceived(bytes: ByteArray) {
            }
            override fun onEndOfSpeech() {
                recordSpeechBtn.setBackgroundColor(Color.parseColor("#00000000"))
            }
            override fun onError(i: Int) {
                recordSpeechBtn.setBackgroundColor(Color.parseColor("#00000000"))
            }
            override fun onResults(bundle: Bundle) {
                val matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                     text_for_translate.setText(matches[0].toString())
                }
            }
            override fun onPartialResults(bundle: Bundle) {
            }
            override fun onEvent(i: Int, bundle: Bundle) {
            }
        })


        //Set Data In Spinners
        val languages: Array<String> = LanguagesForSpinner().getLangsEN()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.setAdapter(adapter)
        spinner2.setAdapter(adapter)
        spinner1.setSelection(languages.indexOf("English"))
        spinner2.setSelection(languages.indexOf("Russian"))

        recordSpeechBtn.setOnClickListener {
            recordSpeechBtn.setBackgroundColor(Color.parseColor("#3ec13e"))

            mSpeechRecognizer.startListening(mSpeechRecognizerIntent)

        }

        translateButton.setOnClickListener {
            val textToBeTranslated = text_for_translate.text.toString()
            if (textToBeTranslated == "")
                return@setOnClickListener

            progress_bar.setVisibility(View.VISIBLE)
            //Translation operation
            TVtranslator.text = Translate(textToBeTranslated, languagePair)
            progress_bar.setVisibility(View.INVISIBLE)

            var time = Calendar.getInstance().time.toString()
            time = time.substring(0,time.indexOf("GMT"))

            var collocation = Collocation(textToBeTranslated + " -> " + TVtranslator.text, time)

            db.insertData(collocation)
        }

        //Synchronization Language Pair and Selected Language In Spinners
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languagePair = "${LanguagesForSpinner().getLangCodeEN(position)}" + "-" + languagePair.substringAfter("-")
                Log.d("spinner test",languagePair)
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languagePair = languagePair.substringBefore("-") + "-" + "${LanguagesForSpinner().getLangCodeEN(position)}"
                Log.d("spinner test",languagePair)
            }
        }

        //Switch Languages
        switchLanguages.setOnClickListener {
            languagePair = languagePair.substringAfter("-") + "-" + languagePair.substringBefore("-")
            Log.d("spinner test",languagePair)

            val s1 = spinner1.selectedItemId.toInt()
            spinner1.setSelection(spinner2.selectedItemId.toInt())
            spinner2.setSelection(s1)
        }

        //Start translation history activity
        translationHistoryBtn.setOnClickListener {
            val i = Intent(this, TranslationHistoryActivity::class.java)
            startActivity(i)
        }

        clearBtn.setOnClickListener{
            text_for_translate.text.clear()

            Toast.makeText(this, "Сleaned", Toast.LENGTH_SHORT).show()
        }

        copyBtn.setOnClickListener{
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", TVtranslator.text.toString())
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Translation Copied To Clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    //Function for calling executing the Translator Background Task
    private fun Translate(textToBeTranslated: String, languagePair: String): String {
        val translatorBackgroundTask = TranslatorBackgroundTask(this)
        val translationResult = translatorBackgroundTask.execute(textToBeTranslated, languagePair) // Returns the translated text as a String
        return translationResult.get()
    }
}
