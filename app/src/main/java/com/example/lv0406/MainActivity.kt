package com.example.lv0406

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        add_btn  = findViewById<Button>(R.id.add_btn)

        listview1= findViewById<ListView>(R.id.main_listview)
        val adapter2 = ArrayAdapter(this,android.R.layout.simple_list_item_1,dataArr)
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
//            Toast.makeText(this,filesDir.toString()+"/"+selected_item.text.toString()+".txt",Toast.LENGTH_SHORT).show()
            adapter2.remove(selected_item.text.toString())
            adapter2.notifyDataSetChanged()



            var f1  = File(filesDir.toString()+"/"+selected_item.text.toString()+"_member.txt")
            f1.delete()
            var f  = File(filesDir.toString()+"/"+selected_item.text.toString()+".txt")
            f.delete()
            var outFs2 : FileOutputStream = openFileOutput("subsclist.txt", MODE_PRIVATE)
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
                add_text = dialogView.findViewById<EditText>(R.id.add_text)

                when{
                    dataArr.contains(add_text.text.toString()) -> Toast.makeText(applicationContext,"등록 실패 : 중복된 구독 이름입니다.",Toast.LENGTH_SHORT).show()

                    else -> {
                        Toast.makeText(applicationContext,"추가완료",Toast.LENGTH_SHORT).show()

                        adapter2.add(add_text.text.toString())
                        adapter2.notifyDataSetChanged()

                        var outFs : FileOutputStream = openFileOutput("subsclist.txt",Context.MODE_APPEND)
                        outFs.write((add_text.text.toString()+"\n").toByteArray())
                        outFs.close()
                    }

                }


            }
            dlg.show()

        }



    }


}