package com.burakkarahan.newsbreak.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.burakkarahan.newsbreak.databinding.ActivityLoginBinding
import com.burakkarahan.newsbreak.model.Login
import com.burakkarahan.newsbreak.service.RetrofitClientBurak
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var compositeDisponsable: CompositeDisposable? = null

    var sharedPreferences: SharedPreferences? = null

    var tf1: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this?.getSharedPreferences("login", 0)
        sharedPreferences?.getString("id_user", "").toString().let {
            if (it != "")
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

        compositeDisponsable = CompositeDisposable()

        tf1 = Typeface.createFromAsset(applicationContext.assets, "fonts/DamionRegular.ttf")
        binding.tvLogin.setTypeface(tf1)

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener{
            binding.btnLogin.isEnabled = false
            loadData()
            binding.btnLogin.isEnabled = true
        }
    }

    private fun loadData() {
        compositeDisponsable?.add(
            RetrofitClientBurak.retrofit.getDataLogin(binding.etLoginMail.text.toString(), binding.etLoginPassword.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))
    }

    private fun handleResponse(login: Login) {
        if (login.result == "true")
        {
            sharedPreferences = applicationContext.getSharedPreferences("login", 0)
            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
            editor.putString("id_user", login.id_user)
            editor.putString("name", login.name)
            editor.putString("surname", login.surname)
            editor.putString("mail", login.mail)
            editor.commit()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.WARNING_TYPE)
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