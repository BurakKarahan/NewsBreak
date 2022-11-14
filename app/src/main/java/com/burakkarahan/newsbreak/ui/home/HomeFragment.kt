package com.burakkarahan.newsbreak.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.burakkarahan.newsbreak.ui.DetailActivity
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.adapter.HomeNewsBigAdapter
import com.burakkarahan.newsbreak.adapter.HomeNewsSmallAdapter
import com.burakkarahan.newsbreak.adapter.HomeNewsSmallVerticalAdapter
import com.burakkarahan.newsbreak.databinding.FragmentHomeBinding
import com.burakkarahan.newsbreak.model.Article
import com.burakkarahan.newsbreak.model.Root
import com.burakkarahan.newsbreak.service.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class HomeFragment : Fragment(), HomeNewsBigAdapter.Listener, HomeNewsSmallAdapter.Listener, HomeNewsSmallVerticalAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null

    private var compositeDisponsable: CompositeDisposable? = null

    var sharedPreferences: SharedPreferences? = null

    private var articleModelsBigReyclerView: ArrayList<Article>? = null
    private var articleModelsSmallReyclerView: ArrayList<Article>? = null
    private var articleModelsSmallVerticalReyclerView: ArrayList<Article>? = null

    private var newsBigAdapter: HomeNewsBigAdapter? = null
    private var newsSmallAdapter: HomeNewsSmallAdapter? = null
    private var newsSmallVerticalAdapter: HomeNewsSmallVerticalAdapter? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        categoryEvent()

        compositeDisponsable = CompositeDisposable()

        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#3F51b5")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()

        newsBigLoadData()
        newsSmallLoadData()
        newsSmallVerticalLoadData()

        pDialog.cancel()

        return root
    }

    private fun newsBigLoadData() {

        val linearLayoutManagerHorizontal = LinearLayoutManager(context)
        linearLayoutManagerHorizontal.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvNewsBig.layoutManager = linearLayoutManagerHorizontal

        compositeDisponsable?.add(
            RetrofitClient.retrofit.getData("us", "general","relevancy", getString(R.string.pageSize), getString(R.string.apiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::newsBighandleResponse))
    }

    private fun newsBighandleResponse(root: Root) {

        root?.let {
            articleModelsBigReyclerView = ArrayList(it.articles)
            articleModelsBigReyclerView?.let {

                for (articleModel : Article in articleModelsBigReyclerView!!)
                {
                    println(articleModel.title)
                    println(articleModel.urlToImage)
                    println(articleModel.content)
                }

                newsBigAdapter = HomeNewsBigAdapter(it, this@HomeFragment)
                binding.rvNewsBig.adapter = newsBigAdapter
            }

        }
    }

    private fun newsSmallLoadData() {

        val linearLayoutManagerHorizontal = LinearLayoutManager(context)
        linearLayoutManagerHorizontal.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvNewsSmall.layoutManager = linearLayoutManagerHorizontal

        /*val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvNewsSmall.layoutManager = gridLayoutManager*/

        compositeDisponsable?.add(
            RetrofitClient.retrofit.getData("bbc-news", getString(R.string.pageSize), getString(R.string.apiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::newsSmallhandleResponse))
    }

    private fun newsSmallhandleResponse(root: Root) {

        root?.let {
            articleModelsSmallReyclerView = ArrayList(it.articles)
            articleModelsSmallReyclerView?.let {
                newsSmallAdapter = HomeNewsSmallAdapter(it, this@HomeFragment)
                binding.rvNewsSmall.adapter = newsSmallAdapter
            }
        }
    }

    private fun newsSmallVerticalLoadData() {

        val linearLayoutManagerHorizontal = LinearLayoutManager(context)
        linearLayoutManagerHorizontal.orientation = LinearLayoutManager.VERTICAL
        binding.rvNewsSmallVertical.layoutManager = linearLayoutManagerHorizontal

        /*val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvNewsSmall.layoutManager = gridLayoutManager*/

        compositeDisponsable?.add(
            RetrofitClient.retrofit.getData("cnn", getString(R.string.pageSize), getString(R.string.apiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::newsSmallVerticalhandleResponse))
    }

    private fun newsSmallVerticalhandleResponse(root: Root) {

        root?.let {
            articleModelsSmallVerticalReyclerView = ArrayList(it.articles)
            articleModelsSmallVerticalReyclerView?.let {
                newsSmallVerticalAdapter = HomeNewsSmallVerticalAdapter(it, this@HomeFragment)
                binding.rvNewsSmallVertical.adapter = newsSmallVerticalAdapter
            }
        }
    }

    private fun categorySelected(categoryName : String) {

        if (categoryName != "All")
        {
            binding.tvNewsTitle.text = categoryName

            binding.llSmallRecyclerView.visibility = View.GONE
            binding.llSmallVerticalRecyclerView.visibility = View.GONE

            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.rvNewsBig.layoutManager = linearLayoutManager

            compositeDisponsable?.add(
                RetrofitClient.retrofit.getData("us", categoryName.lowercase(),"relevancy", getString(R.string.pageSize), getString(R.string.apiKey))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::newsBighandleResponse))
        }
        else
        {
            binding.tvNewsTitle.text = "Popular news"

            binding.llSmallRecyclerView.visibility = View.VISIBLE
            binding.llSmallVerticalRecyclerView.visibility = View.VISIBLE

            newsBigLoadData()
            newsSmallLoadData()
        }
    }

    fun categoryEvent() {

        val arrayLinearLayout = ArrayList<LinearLayout>()
        arrayLinearLayout.add(binding.llCategoryAll)
        arrayLinearLayout.add(binding.llCategoryBusiness)
        arrayLinearLayout.add(binding.llCategoryEntertainment)
        arrayLinearLayout.add(binding.llCategoryHealth)
        arrayLinearLayout.add(binding.llCategoryScience)
        arrayLinearLayout.add(binding.llCategorySports)
        arrayLinearLayout.add(binding.llCategoryTechnology)

        val arrayTextView = ArrayList<TextView>()
        arrayTextView.add(binding.tvCategoryAll)
        arrayTextView.add(binding.tvCategoryBusiness)
        arrayTextView.add(binding.tvCategoryEntertainment)
        arrayTextView.add(binding.tvCategoryHealth)
        arrayTextView.add(binding.tvCategoryScience)
        arrayTextView.add(binding.tvCategorySports)
        arrayTextView.add(binding.tvCategoryTechnology)

        binding.llCategoryAll.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryAll, arrayTextView, binding.tvCategoryAll)
            categorySelected("All")
        }
        binding.llCategoryBusiness.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryBusiness, arrayTextView, binding.tvCategoryBusiness)
            binding.ivCategoryBusiness.setImageResource(R.drawable.ic_category_business_active);
            categorySelected("Business")
        }
        binding.llCategoryEntertainment.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryEntertainment, arrayTextView, binding.tvCategoryEntertainment)
            binding.ivCategoryEntertainment.setImageResource(R.drawable.ic_category_entertainment_active);
            categorySelected("Entertainment")
        }
        binding.llCategoryHealth.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryHealth, arrayTextView, binding.tvCategoryHealth)
            binding.ivCategoryHealth.setImageResource(R.drawable.ic_category_healt_active);
            categorySelected("Health")
        }
        binding.llCategoryScience.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryScience, arrayTextView, binding.tvCategoryScience)
            binding.ivCategoryScience.setImageResource(R.drawable.ic_category_science_active);
            categorySelected("Science")
        }
        binding.llCategorySports.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategorySports, arrayTextView, binding.tvCategorySports)
            binding.ivCategorySports.setImageResource(R.drawable.ic_category_sports_active);
            categorySelected("Sports")
        }
        binding.llCategoryTechnology.setOnClickListener {
            categoryActivePassive(arrayLinearLayout, binding.llCategoryTechnology, arrayTextView, binding.tvCategoryTechnology)
            binding.ivCategoryTechnology.setImageResource(R.drawable.ic_category_technology_active);
            categorySelected("Technology")
        }

    }

    fun categoryActivePassive(
        arrayLienarLayout : ArrayList<LinearLayout>, activeLinearLayout : LinearLayout? = null,
        arrayTextView: ArrayList<TextView>, activeTextView : TextView? = null) {

        for (ll in arrayLienarLayout){
            if (ll == activeLinearLayout)
                ll?.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btn_category_background_active))
            else
                ll?.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btn_category_background_passive))
        }

        binding.ivCategoryBusiness.setImageResource(R.drawable.ic_category_business_passive);
        binding.ivCategoryEntertainment.setImageResource(R.drawable.ic_category_entertainment_passive);
        binding.ivCategoryHealth.setImageResource(R.drawable.ic_category_healt_passive);
        binding.ivCategoryScience.setImageResource(R.drawable.ic_category_science_passive);
        binding.ivCategorySports.setImageResource(R.drawable.ic_category_sports_passive);
        binding.ivCategoryTechnology.setImageResource(R.drawable.ic_category_technology_passive);

        for (tv in arrayTextView){
            if (tv == activeTextView)
                tv?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            else
                tv?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
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

        //articleModel.source?.name?.let { Log.i("asdasdasd", it) }
        //Log.i("asdasdasd", articleModel.title.toString() + " / "+ articleModel.description + " / " + articleModel.content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisponsable?.clear()
        _binding = null
    }
}