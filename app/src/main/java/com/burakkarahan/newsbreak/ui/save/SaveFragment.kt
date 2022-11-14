package com.burakkarahan.newsbreak.ui.save

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.burakkarahan.newsbreak.ui.DetailActivity
import com.burakkarahan.newsbreak.adapter.SaveVerticalAdapter
import com.burakkarahan.newsbreak.databinding.FragmentSaveBinding
import com.burakkarahan.newsbreak.model.Save
import com.burakkarahan.newsbreak.service.RetrofitClientBurak
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SaveFragment : Fragment(), SaveVerticalAdapter.Listener {

    private var _binding: FragmentSaveBinding? = null

    private var compositeDisponsable: CompositeDisposable? = null

    private var saveModelReyclerView: ArrayList<Save>? = null

    private var saveVerticalAdapter: SaveVerticalAdapter? = null

    var sharedPreferences: SharedPreferences? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(SaveViewModel::class.java)

        _binding = FragmentSaveBinding.inflate(inflater, container, false)
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

        newsBigLoadData()

        return root
    }

    private fun newsBigLoadData() {

        val linearLayoutVertical = LinearLayoutManager(context)
        linearLayoutVertical.orientation = LinearLayoutManager.VERTICAL
        binding.rvSave.layoutManager = linearLayoutVertical

        sharedPreferences = activity?.getSharedPreferences("login", 0)
        sharedPreferences?.getString("id_user", "").toString().let {
            if (it != "")
            {
                compositeDisponsable?.add(
                    RetrofitClientBurak.retrofit.getDataFavorite(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse))
            }
        }
    }

    private fun handleResponse(save: List<Save>) {

        save.let {
            saveModelReyclerView = ArrayList(it)
            saveModelReyclerView?.let {

                for (saveModel : Save in saveModelReyclerView!!)
                {
                    println(saveModel.title)
                    println(saveModel.urlToImage)
                    println(saveModel.content)
                }

                saveVerticalAdapter = SaveVerticalAdapter(it, this@SaveFragment)
                binding.rvSave.adapter = saveVerticalAdapter
            }
        }

    }

    override fun onItemClick(saveModel: Save) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("name", saveModel.name)
        intent.putExtra("publishedAt", saveModel.publishedAt.toString())
        intent.putExtra("title", saveModel.title)
        intent.putExtra("author", saveModel.author)
        intent.putExtra("urlToImage", saveModel.urlToImage)
        intent.putExtra("description", saveModel.description)
        intent.putExtra("content", saveModel.content)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisponsable?.clear()
        _binding = null
    }

}