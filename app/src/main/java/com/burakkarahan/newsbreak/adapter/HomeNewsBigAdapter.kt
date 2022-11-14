package com.burakkarahan.newsbreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.layout_news_big.view.*

class HomeNewsBigAdapter(private val articleList : ArrayList<Article>, private val listener : Listener) : RecyclerView.Adapter<HomeNewsBigAdapter.RowHolder>(){

    interface Listener {
        fun onItemClick(articleModel: Article)
    }

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(articleModel: Article, position: Int, listener: Listener) {
            itemView.setOnClickListener{
                listener.onItemClick(articleModel)
            }

            articleModel.title.let {
                if (it.toString() != "null")
                    itemView.tvNewsBigTitle.text = it
            }
            articleModel.description.let {
                if (it.toString() != "null")
                    itemView.tvNewsBigContent.text = it
            }
            articleModel.urlToImage.let {
                if (it.toString() != "null")
                {
                    Picasso.get()
                        .load(it)
                        .resize(1000, 500)
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .into(itemView.ivNewsBigUrlToImage)
                }
                else
                    itemView.ivNewsBigUrlToImage.setImageResource(R.drawable.ic_newspaper)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_news_big, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(articleList[position], position, listener)
    }

    override fun getItemCount(): Int {
        return articleList.count()
    }

}