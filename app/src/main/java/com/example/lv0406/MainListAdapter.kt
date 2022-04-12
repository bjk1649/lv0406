package com.example.lv0406

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MainListAdapter(val context : Context, var memberlist: ArrayList<MemberInfo>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.member_info, null)

        val member_name = view.findViewById<TextView>(R.id.tv_member_name)
        val phone_num = view.findViewById<TextView>(R.id.tv_phone_num)


        val member = memberlist[position]
        member_name.text = member.name
        phone_num.text = member.phone_num

        return view
    }

    override fun getItem(position: Int): Any {
        return memberlist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return memberlist.size
    }
    fun updateList(data : ArrayList<MemberInfo>){
        memberlist= data
        notifyDataSetChanged()
    }
}