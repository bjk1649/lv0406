package com.example.lv0406

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var dataArr = ArrayList<String>()
    lateinit var add_btn : Button
    lateinit var add_text : EditText
    lateinit var dialogView : View
    lateinit var listview1 : ListView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        add_btn  = findViewById<Button>(R.id.add_btn)

        listview1= findViewById<ListView>(R.id.main_listview)
        val adapter2 = ArrayAdapter(this,android.R.layout.simple_list_item_1,dataArr)
//        val adapter = MainListAdapter()
        listview1.adapter = adapter2


        fun readlist(){
            var inFs : FileInputStream
            try{
                inFs = openFileInput("subsclist.txt")
                var txt = ByteArray(500)
                inFs.read(txt)
                inFs.close()
                var token = txt.toString(Charsets.UTF_8).split('\n')
                for (i in 0..(token.size-2)) {

                    adapter2.add(token[i])}
                adapter2.notifyDataSetChanged()

            }catch (e:IOException){
                Toast.makeText(applicationContext,"환영합니다",Toast.LENGTH_SHORT).show()
            }
        }
        readlist()

        listview1.setOnItemLongClickListener  {parent, view, position, id ->
            val selected_item = view as TextView
            Toast.makeText(this,selected_item.text.toString()+"삭제됨",Toast.LENGTH_SHORT).show()
            adapter2.remove(selected_item.text.toString())
            adapter2.notifyDataSetChanged()
            var file= File("applemusic.txt")
            file.delete()

            var outFs2 : FileOutputStream = openFileOutput("subsclist.txt",Context.MODE_PRIVATE)

            for (i in 0..(dataArr.size-1)){
                outFs2.write(dataArr[i].toByteArray())

            }

            outFs2.close()

            return@setOnItemLongClickListener true
        }
        listview1.setOnItemClickListener { adapterView, view, i, l ->
            val clickedSubs = dataArr[i]
            val myIntent = Intent(this@MainActivity,subsDetailActivity::class.java)
            myIntent.putExtra("subsname",clickedSubs)
            startActivity(myIntent)
        }

        add_btn.setOnClickListener {
            dialogView = View.inflate(this@MainActivity,R.layout.dialog1,null)
            var dlg = AlertDialog.Builder(this@MainActivity)
            dlg.setTitle("새로운 구독 추가")
            dlg.setView(dialogView)
            dlg.setPositiveButton("취소"){dialog, which->
                Toast.makeText(this@MainActivity,"취소",Toast.LENGTH_SHORT).show()
            }

            dlg.setNegativeButton("추가하기"){dialog, which ->
                Toast.makeText(applicationContext,"추가완료",Toast.LENGTH_SHORT).show()
                add_text = dialogView.findViewById<EditText>(R.id.add_text)
                adapter2.add(add_text.text.toString())
                adapter2.notifyDataSetChanged()

                var outFs : FileOutputStream = openFileOutput("subsclist.txt",Context.MODE_APPEND)
                outFs.write((add_text.text.toString()+"\n").toByteArray())
                outFs.close()
            }
            dlg.show()

        }



    }

    fun setValues(){

    }

    inner class MainListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return dataArr.size
        }

        override fun getItem(position: Int): Any? {
            return dataArr[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
            var myConverView:View? = converView
            if (converView==null){
                myConverView = layoutInflater.inflate(R.layout.main_list_item,null)
            }

            var item_name :TextView? = myConverView?.findViewById<TextView>(R.id.textView1)
            item_name?.text = dataArr[position]
            return myConverView!!
        }
    }
}