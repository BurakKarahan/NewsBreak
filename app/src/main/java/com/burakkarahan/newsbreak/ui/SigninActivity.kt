package com.burakkarahan.newsbreak.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.burakkarahan.newsbreak.databinding.ActivitySigninBinding
import com.burakkarahan.newsbreak.model.ResultModel
import com.burakkarahan.newsbreak.service.RetrofitClientBurak
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    private var compositeDisponsable: CompositeDisposable? = null

    var tf1: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compositeDisponsable = CompositeDisposable()

        tf1 = Typeface.createFromAsset(applicationContext.assets, "fonts/DamionRegular.ttf")
        binding.tvSignin.setTypeface(tf1)

        binding.btnSignin.setOnClickListener {
            binding.btnSignin.isEnabled = false
            loadData()
            binding.btnSignin.isEnabled = true
        }

    }

    private fun loadData() {
        compositeDisponsable?.add(
            RetrofitClientBurak.retrofit.getDataSignin(binding.etSigninName.text.toString(),binding.etSigninName.text.toString(),etSigninMail.text.toString(),binding.etSigninPassword.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))
    }

    private fun handleResponse(resultModel: ResultModel) {
        if (resultModel.result == "true")
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            SweetAlertDialog(this@SigninActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops...")
                .setContentText("Login failed, try again")
                .setConfirmText("Ok")
                //.setConfirmClickListener { }
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisponsable?.clear()
    }
}
