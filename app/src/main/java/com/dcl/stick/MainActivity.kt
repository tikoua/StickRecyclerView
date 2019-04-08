package com.dcl.stick

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dcl.example.R
import com.dcl.stickrecyclerview.StickHelper
import com.dcl.stickrecyclerview.logi
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_label.*
import kotlinx.android.synthetic.main.item_normal.*

class MainActivity : AppCompatActivity() {
    private val data by lazy { mutableListOf<String>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        bindData(data)
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    private fun initData() {
        for (i in 'a'..'z') {
            for (j in 0..20) {
                data.add("${i}${j}")
            }
        }
        logi("  " + data.toString())
    }

    private fun bindData(data: MutableList<String>) {
        rv_01.layoutManager = LinearLayoutManager(this)
        val adapter = SelfAdapter()
        adapter.setData(data)
        rv_01.adapter = adapter
    }

}

class SelfAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickHelper {

    private var data: List<String>? = null
    private val labels by lazy { mutableMapOf<Int, String>() }
    private val Type_Label = 1
    private val Type_Item = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type_Label -> SelfLabelHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_label,
                    parent,
                    false
                )
            )
            else -> SelfIttemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false))
        }
    }

    override fun getItemCount(): Int {
        data?.let {
            return it.size + labels.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SelfLabelHolder -> labels[position]?.let { holder.bindData(it) }
            is SelfIttemHolder -> {
                val preLabelCount = labels.count { it.key < position }
                data?.get(position - preLabelCount)?.let { holder.bindData(it) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (labels.containsKey(position)) {
            return Type_Label
        } else {
            return Type_Item
        }
    }

    override fun isFloatType(position: Int): Boolean {
        return getItemViewType(position) == Type_Label
    }

    override fun isFloatMembers(position: Int): Boolean {
        return getItemViewType(position) == Type_Item
    }

    fun setData(data: List<String>) {
        this.data = data
        labels.clear()
        for (i in 0 until data.size) {
            val label = data[i]
            val pre = label.substring(0, 1)
            val labelSize = labels.size
            if (labelSize == 0) {
                labels.put(0, pre)
            } else {
                val maxPosition = labels.maxBy { it.key }!!
                if (maxPosition.value != pre) {
                    labels[i + labelSize] = pre
                }
            }
        }
        logi(labels.toString())
    }
}

class SelfLabelHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bindData(label: String) {
        tv_item_label.text = label
    }
}

class SelfIttemHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bindData(name: String) {
        tv_item_normal.text = name
    }
}

