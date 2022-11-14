package com.burakkarahan.newsbreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.model.Article
import com.burakkarahan.newsbreak.model.Save
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_search_vertical.view.*

class SaveVerticalAdapter (private val saveList : ArrayList<Save>, private val listener : SaveVerticalAdapter.Listener) : RecyclerView.Adapter<SaveVerticalAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(saveeModel: Save)
    }

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(saveModel: Save, position: Int, listener: SaveVerticalAdapter.Listener) {
            itemView.setOnClickListener{
                listener.onItemClick(saveModel)
            }

            saveModel.title.let {
                if (it.toString() != "null")
                    itemView.tvNewsSearchVerticalTitle.text = it
            }
            saveModel.description.let {
                if (it.toString() != "null")
                    itemView.tvNewsSearchVerticaldescription.text = it
            }
            saveModel.urlToImage.let {
                if (it.toString() != "null")
                {
                    Picasso.get()
                        .load(it)
                        .resize(1000, 500)
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .into(itemView.ivNewsSearchVerticalUrlToImage)
                }
                else
                    itemView.ivNewsSearchVerticalUrlToImage.setImageResource(R.drawable.ic_newspaper)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_search_vertical, parent, false)
        return SaveVerticalAdapter.RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(saveList[position], position, listener)
    }

    override fun getItemCount(): Int {
        return saveList.count()
    }
}