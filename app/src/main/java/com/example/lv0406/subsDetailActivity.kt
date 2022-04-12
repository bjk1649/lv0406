package com.example.lv0406

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Member

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
    lateinit var ed_name :EditText
    lateinit var ed_phone :EditText
    lateinit var dialogView : View
    lateinit var dialogView2 : View
    lateinit var member_lv : ListView



    var memberArr = ArrayList<MemberInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subs_detail)
        
        var data :String?
        
        title = "구독 정보"
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
        member_lv = findViewById<ListView>(R.id.lv_detail)


        val adapter1 = MainListAdapter(this,memberArr)
        member_lv.adapter=adapter1

        readInfo(subs_name.text.toString())

        member_lv.setOnItemClickListener { adapterView, view, i, l ->
            var dlg1 = AlertDialog.Builder(this@subsDetailActivity)
            dlg1.setTitle("멤버 삭제")
            dlg1.setMessage("선택한 멤버를 삭제하시겠습니까?")
            dlg1.setNegativeButton("확인"){dialog, which ->
                memberArr.removeAt(i)
                adapter1.updateList(memberArr)
                deleteMember(data)
                if (memberArr.size!=0){
                    total_member.text  = memberArr.size.toString()
                    personal_price.text = (total_price.text.toString().toIntOrNull()!! /memberArr.size).toString()
                }
                else {
                    total_member.text = "0"
                    personal_price.text = "0"
                }
                Toast.makeText(this@subsDetailActivity,"멤버 삭제", Toast.LENGTH_SHORT).show()

            }
            dlg1.setPositiveButton("취소"){dialog, which ->
                Toast.makeText(this@subsDetailActivity,"취소", Toast.LENGTH_SHORT).show()
            }
            dlg1.show()

        }
        btn_member.setOnClickListener {

            dialogView = View.inflate(this@subsDetailActivity,R.layout.member_add,null)
            var dlg = AlertDialog.Builder(this@subsDetailActivity)
            dlg.setTitle("새로운 멤버 추가")
            dlg.setView(dialogView)
            dlg.setPositiveButton("취소"){dialog, which->
                Toast.makeText(this@subsDetailActivity,"취소", Toast.LENGTH_SHORT).show()
            }

            dlg.setNegativeButton("추가하기"){dialog, which ->

                ed_name = dialogView.findViewById<EditText>(R.id.ed_add_name)
                ed_phone = dialogView.findViewById<EditText>(R.id.ed_add_phone)

                var new_mem = MemberInfo(ed_name.text.toString(),ed_phone.text.toString(),data!!)
                memberArr.add(new_mem)
                adapter1.updateList(memberArr)
                total_member.text = memberArr.size.toString()
                personal_price.text = (total_price.text.toString().toInt() / total_member.text.toString().toInt()).toString()
                updateMember(new_mem,data)
                Toast.makeText(applicationContext,"추가완료",Toast.LENGTH_SHORT).show()
            }
            dlg.show()


        }



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

                if (!chkNum(ed_price.text.toString())){
                    Toast.makeText(applicationContext,"총 가격란에는 숫자만 입력해주세요",Toast.LENGTH_SHORT).show()

                }
                else {
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
            }
            dlg.show()


        }



    }

    fun readInfo(subs_name:String?){


        total_member.text = memberArr.size.toString()

        var inFs : FileInputStream
        try{

            inFs = openFileInput(subs_name+".txt")
            if (inFs.available()>2) {

                var txt = ByteArray(500)
                inFs.read(txt)
                inFs.close()
                var token = txt.toString(Charsets.UTF_8).split('\t')

                plan_name.text = token[1]
                total_price.text = token[2]
                pay_date.text = token[3]
                btn_member.visibility=View.VISIBLE
                readMember(subs_name)
            }
            else Toast.makeText(applicationContext,"저장된 정보 없음",Toast.LENGTH_SHORT).show()
        }
        catch (e: IOException){
            Toast.makeText(applicationContext,"저장된 정보 없음",Toast.LENGTH_SHORT).show()
        }



    }

    fun readMember(subs_name:String?){
        var inFs1 : FileInputStream
        try{
            inFs1 = openFileInput(subs_name+"_member.txt")
            var txt = ByteArray(500)
            inFs1.read(txt)
            inFs1.close()
            var token = txt.toString(Charsets.UTF_8).split('\n')
            for (i in 0..(token.size-2)) {
                var nameAndPhone = token[i].split('\t')
                var new_mem = MemberInfo(nameAndPhone[0],nameAndPhone[1], subs_name.toString())
                memberArr.add(new_mem)
            }
        }catch (e:IOException){
            Toast.makeText(applicationContext,"환영합니다",Toast.LENGTH_SHORT).show()
        }
        if (memberArr.size!=0){
            total_member.text  = memberArr.size.toString()
            personal_price.text = (total_price.text.toString().toIntOrNull()!! /memberArr.size).toString()
        }
    }
    

    fun deleteMember(subs_name: String?){


        var outFs : FileOutputStream = openFileOutput(subs_name+"_member.txt",Context.MODE_PRIVATE)
        for (newMemberInfo in memberArr){
            var new_name = newMemberInfo.name
            var new_phone = newMemberInfo.phone_num
            outFs.write((new_name+"\t"+new_phone+"\n").toByteArray())
        }

        outFs.close()
    }

    fun updateMember(newMemberInfo: MemberInfo, subs_name: String){
        var new_name = newMemberInfo.name
        var new_phone = newMemberInfo.phone_num
        var outFs : FileOutputStream = openFileOutput(subs_name+"_member.txt",Context.MODE_APPEND)
        outFs.write((new_name+"\t"+new_phone+"\n").toByteArray())
        outFs.close()
    }
    fun chkNum(str: String) : Boolean {
        var temp: Char
        var result = true
        for (i in 0 until str.length) {
            temp = str.elementAt(i)
            if (temp.toInt() < 48 || temp.toInt() > 57) {
                result = false
            }
        }
        return result
    }


}