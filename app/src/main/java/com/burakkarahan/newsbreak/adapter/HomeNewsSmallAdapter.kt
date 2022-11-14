package com.burakkarahan.newsbreak.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_news_big.view.*
import kotlinx.android.synthetic.main.layout_news_small.view.*

class HomeNewsSmallAdapter(private val articleList : ArrayList<Article>, private val listener : HomeNewsSmallAdapter.Listener) : RecyclerView.Adapter<HomeNewsSmallAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(articleModel: Article)
    }

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(articleModel: Article, position: Int, listener: HomeNewsSmallAdapter.Listener) {
            itemView.setOnClickListener{
                listener.onItemClick(articleModel)
            }

            articleModel.title.let {
                if (it.toString() != "nul")
                    itemView.tvNewsSmallTitle.text = it
            }
            articleModel.urlToImage.let {
                if(it.toString() != "null")
                {
                    Picasso.get()
                        .load(it)
                        .resize(1000, 500)
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .into(itemView.ivNewsSmallUrlToImage)
                }
                else
                    itemView.ivNewsSmallUrlToImage.setImageResource(R.drawable.ic_newspaper)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_news_small, parent, false)
        return HomeNewsSmallAdapter.RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(articleList[position], position, listener)
    }

    override fun getItemCount(): Int {
        return articleList.count()
    }
}