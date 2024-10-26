package com.example.mergepdf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import android.widget.TextView

class ThemeAdapter(
    private val context: Context,
    private val themes: Array<String>,
    private var selectedPosition: Int
) : BaseAdapter() {

    override fun getCount(): Int = themes.size

    override fun getItem(position: Int): Any = themes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false)

        val radioButton = view.findViewById<RadioButton>(R.id.radioButton)
        val textView = view.findViewById<TextView>(R.id.themeOptionTextView)

        textView.text = themes[position]
        radioButton.isChecked = position == selectedPosition

        return view
    }

    fun updateSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}
