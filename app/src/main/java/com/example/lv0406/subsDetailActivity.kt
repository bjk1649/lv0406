package com.example.lv0406

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class subsDetailActivity : AppCompatActivity() {
    lateinit var subs_name : TextView
    lateinit var plan_name : TextView
    lateinit var total_price : TextView
    lateinit var pay_date : TextView
    lateinit var total_member : TextView
    lateinit var personal_price : TextView
    lateinit var btn_modify : Button
    lateinit var btn_member : Button
    lateinit var ed_plan :EditText
    lateinit var ed_price :EditText
    lateinit var ed_date :EditText

    lateinit var dialogView2 : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subs_detail)
        var data :String?
        data = intent.getStringExtra("subsname")

        subs_name=findViewById<TextView>(R.id.tv_subs_name)
        subs_name.text = data
        plan_name=findViewById<TextView>(R.id.tv_plan_name)
        total_price=findViewById<TextView>(R.id.tv_total_price)
        pay_date=findViewById<TextView>(R.id.tv_pay_date)
        total_member=findViewById<TextView>(R.id.tv_total_member)
        personal_price=findViewById<TextView>(R.id.tv_personal_price)
        btn_modify=findViewById<Button>(R.id.btn_modify)
        btn_member=findViewById<Button>(R.id.btn_add_member)
        readInfo(subs_name.text.toString())
        btn_modify.setOnClickListener {
            dialogView2 = View.inflate(this@subsDetailActivity,R.layout.layout,null)
            var dlg = AlertDialog.Builder(this@subsDetailActivity)
            dlg.setTitle("새로운 구독 추가")
            dlg.setView(dialogView2)
            dlg.setPositiveButton("취소"){dialog, which->
                Toast.makeText(this@subsDetailActivity,"취소", Toast.LENGTH_SHORT).show()
            }

            dlg.setNegativeButton("추가하기"){dialog, which ->

                ed_plan = dialogView2.findViewById<EditText>(R.id.ed_add_plan)
                ed_price = dialogView2.findViewById<EditText>(R.id.ed_add_price)
                ed_date = dialogView2.findViewById<EditText>(R.id.ed_add_paydate)
                plan_name.text=ed_plan.text.toString()
                total_price.text=ed_price.text.toString()
                pay_date.text=ed_date.text.toString()

                var outFs2 : FileOutputStream = openFileOutput((data+".txt"), Context.MODE_PRIVATE)
                outFs2.write((data+"\t"
                        +ed_plan.text.toString()+"\t"
                        +ed_price.text.toString()+"\t"
                        +ed_date.text.toString()

                        ).toByteArray())
                outFs2.close()
                Toast.makeText(applicationContext,"추가완료",Toast.LENGTH_SHORT).show()
                btn_member.visibility=View.VISIBLE
            }
            dlg.show()


        }

    }
    fun readInfo(subs_name:String?){
        var inFs : FileInputStream
        try{
            inFs = openFileInput(subs_name+".txt")
            var txt = ByteArray(500)
            inFs.read(txt)
            inFs.close()
            var token = txt.toString(Charsets.UTF_8).split('\t')

            plan_name.text=token[1]
            total_price.text=token[2]
            pay_date.text=token[3]
            btn_member.visibility=View.VISIBLE
        }
        catch (e: IOException){
            Toast.makeText(applicationContext,"저장된 정보 없음",Toast.LENGTH_SHORT).show()
        }
    }
}