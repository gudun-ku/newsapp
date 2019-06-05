package com.example.bitamirshafiee.newsapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.news_item.view.*

class NewsAdapter(context: Context, arrayList: ArrayList<Data> ): BaseAdapter() {

    var arrayList: ArrayList<Data> = ArrayList()
    var context: Context?

    init {
        this.arrayList = arrayList
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder
        val inflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if(convertView == null) {
            holder = ViewHolder()

            view = inflater.inflate(R.layout.news_item, parent, false)
            holder.sectionName = view.twSectionName.twSectionName
            holder.webTitle = view.twTitle

            view.tag = holder
        } else {
            view = convertView;
            holder = convertView.tag as ViewHolder
        }

        val sectionNameValue = holder.sectionName
        sectionNameValue?.text = arrayList[position].sectionName

        val titleValue = holder.webTitle
        titleValue?.text = arrayList[position].webTitle

        return view
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    class ViewHolder {
        var sectionName: TextView? = null
        var webTitle: TextView? = null
    }
}