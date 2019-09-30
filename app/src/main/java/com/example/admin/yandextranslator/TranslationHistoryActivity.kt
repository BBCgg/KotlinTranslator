package com.example.admin.yandextranslator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_translation_history.*
import kotlinx.android.synthetic.main.list_item.view.*

private var SELECTED_COLLOCATION: Collocation? = null

class TranslationHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_history)

        var db = DataBaseHundler(this)

        printDB(db)

        rmAllBtn.setOnClickListener {
            db.deleteAllData()
            printDB(db)

            Toast.makeText(this, "All Data Deleted", Toast.LENGTH_SHORT).show()
        }

        rmSelectedBtn.setOnClickListener {
            db.deleteData(SELECTED_COLLOCATION)
            printDB(db)
        }

        listView.setOnItemClickListener {
            parent, view, position, id ->

            if (view!!.text1.text == SELECTED_COLLOCATION?.collocation){
                printDB(db)
                SELECTED_COLLOCATION = Collocation("null","null")
                return@setOnItemClickListener
            }
            SELECTED_COLLOCATION = Collocation(view!!.text1!!.text.toString(),view!!.text2!!.text.toString())
        }
    }

    private  fun printDB(db: DataBaseHundler){
        var data = db.readData()

        var collocationEntries = mutableMapOf<String,String>()

        for (entry in data){
            collocationEntries.put(entry.date,entry.collocation)
        }

        Log.d("dataS",data.size.toString())

        var listItems= mutableListOf<Map<String,String>>()


        var adapter = SimpleAdapter(this, listItems,R.layout.list_item, arrayOf("First line","Second line"),
                intArrayOf(R.id.text2,R.id.text1))

        for (entry in collocationEntries){
            var lv = mutableMapOf<String,String>()

            lv.put("First line",entry.key)
            lv.put("Second line",entry.value)

            listItems.add(lv)
        }

        listView.adapter = adapter
    }

}
