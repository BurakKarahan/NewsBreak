package com.burakkarahan.newsbreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_news_small_vertical.view.*
import kotlinx.android.synthetic.main.layout_search_vertical.view.*

class SearchVerticalAdapter (private val articleList : ArrayList<Article>, private val listener : SearchVerticalAdapter.Listener) : RecyclerView.Adapter<SearchVerticalAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(articleModel: Article)
    }

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(articleModel: Article, position: Int, listener: SearchVerticalAdapter.Listener) {
            itemView.setOnClickListener{
                listener.onItemClick(articleModel)
            }


            articleModel.title.let {
                if (it.toString() != "null")
                    itemView.tvNewsSearchVerticalTitle.text = it
            }
            articleModel.description.let {
                if (it.toString() != "null")
                    itemView.tvNewsSearchVerticaldescription.text = it
            }
            articleModel.urlToImage.let {
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
        return SearchVerticalAdapter.RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(articleList[position], position, listener)
    }

    override fun getItemCount(): Int {
        return articleList.count()
    }
}