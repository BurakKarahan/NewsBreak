package com.burakkarahan.newsbreak.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.databinding.ActivityDetailBinding
import com.burakkarahan.newsbreak.service.RetrofitClientBurak
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var compositeDisponsable: CompositeDisposable? = null

    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compositeDisponsable = CompositeDisposable()

        var textData: String = ""
        var textAuthorName: String = ""
        intent.getStringExtra("name").let {
            if (it.toString() != "null") {
                textAuthorName = it.toString()
                binding.tvDetailAuthor.text = it
            }
        }
        intent.getStringExtra("publishedAt").let {
            if (it.toString() != "null")
                binding.tvDetailPublishedAt.text = it?.replace("GMT+03:00 ", "")
        }
        intent.getStringExtra("author").let {
            if (it.toString() != "null" && it != textAuthorName)
                binding.tvDetailAuthor.text = textAuthorName + " / " + it
        }
        intent.getStringExtra("title").let {
            if (it.toString() != "null") {
                textData = it!!
                binding.tvDetailTitle.text = it
            }
        }
        intent.getStringExtra("description").let {
            if (it.toString() != "null") {
                textData = it!!
            }
        }
        intent.getStringExtra("content").let {
            if (it.toString() != "null") {
                textData = it!!
            }
        }
        intent.getStringExtra("urlToImage").let {
            if (it.toString() != "null")
            {
                Picasso.get()
                    .load(it)
                    .resize(1000, 500)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(binding.ivDetailUrlToImage)
            }
            else
                binding.ivDetailUrlToImage.setImageResource(R.drawable.ic_newspaper)
        }
        binding.tvDetailDescriptionContent.text = textData + "\n\n" + getResources().getString(R.string.detail_lorem_ipsum)


        binding.ivDetailSave.setOnClickListener {
            binding.ivDetailSave.setImageResource(R.drawable.ic_detail_save_true);
            LoadData()
        }

        binding.ivDetailBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun LoadData() {

        sharedPreferences = this?.getSharedPreferences("login", 0)
        sharedPreferences?.getString("id_user", "").toString().let {
            compositeDisponsable?.add(
                RetrofitClientBurak.retrofit.getDataFavorite(it, intent.getStringExtra("publishedAt").toString(), intent.getStringExtra("author").toString(), intent.getStringExtra("urlToImage").toString(), intent.getStringExtra("title").toString(), intent.getStringExtra("content").toString(), intent.getStringExtra("name").toString(), intent.getStringExtra("description").toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisponsable?.clear()
    }

}