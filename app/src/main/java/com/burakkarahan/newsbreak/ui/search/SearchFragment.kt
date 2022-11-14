package com.burakkarahan.newsbreak.ui.search

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.burakkarahan.newsbreak.ui.DetailActivity
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.adapter.SearchVerticalAdapter
import com.burakkarahan.newsbreak.databinding.FragmentSearchBinding
import com.burakkarahan.newsbreak.model.Article
import com.burakkarahan.newsbreak.model.Root
import com.burakkarahan.newsbreak.service.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchFragment : Fragment(), SearchVerticalAdapter.Listener {

    private var _binding: FragmentSearchBinding? = null

    private var compositeDisponsable: CompositeDisposable? = null

    private var articleModelsReyclerView: ArrayList<Article>? = null
    private var searchVerticalAdapter: SearchVerticalAdapter? = null

    var sharedPreferences: SharedPreferences? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val sa = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                sa.titleText = "Attention!"
                sa.contentText = "Are you sure you want to exit the application?"
                sa.cancelText = "No"
                sa.setCancelClickListener(null)
                sa.confirmText = "Yes"
                sa.setConfirmClickListener {
                    sa.cancel()

                    sharedPreferences = context?.getSharedPreferences("login", 0)
                    sharedPreferences?.edit()?.remove("id_user")?.commit();

                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    activity?.finish()
                }
                sa.show()
            }
        })

        compositeDisponsable = CompositeDisposable()

        newsLoadData(false, "")

        binding.etSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                newsLoadData(true, binding.etSearch.text.toString())
                return@OnKeyListener true
            }
            false
        })

        return root
    }

    private fun newsLoadData(searchBeforeAfter: Boolean, searchWord: String) {

        val linearLayoutManagerVerical = LinearLayoutManager(context)
        linearLayoutManagerVerical.orientation = LinearLayoutManager.VERTICAL
        binding.rvSearch.layoutManager = linearLayoutManagerVerical

        if (searchBeforeAfter == true)
        {
            compositeDisponsable?.add(
                RetrofitClient.retrofit.getDataSearch(searchWord,getString(R.string.pageSize), getString(
                    R.string.apiKey))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse))
        }
        else
        {
            compositeDisponsable?.add(
                RetrofitClient.retrofit.getData("us", "general","relevancy", getString(R.string.pageSize), getString(R.string.apiKey))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse))
        }

    }

    private fun handleResponse(root: Root) {

        root?.let {
            articleModelsReyclerView = ArrayList(it.articles)
            articleModelsReyclerView?.let {

                for (articleModel : Article in articleModelsReyclerView!!)
                {
                    println(articleModel.title)
                    println(articleModel.urlToImage)
                    println(articleModel.content)
                }

                searchVerticalAdapter = SearchVerticalAdapter(it, this@SearchFragment)
                binding.rvSearch.adapter = searchVerticalAdapter
            }

        }
    }

    override fun onItemClick(articleModel: Article) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("name", articleModel.source?.name)
        intent.putExtra("publishedAt", articleModel.publishedAt.toString())
        intent.putExtra("title", articleModel.title)
        intent.putExtra("author", articleModel.author)
        intent.putExtra("urlToImage", articleModel.urlToImage)
        intent.putExtra("description", articleModel.description)
        intent.putExtra("content", articleModel.content)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisponsable?.clear()
        _binding = null
    }
}