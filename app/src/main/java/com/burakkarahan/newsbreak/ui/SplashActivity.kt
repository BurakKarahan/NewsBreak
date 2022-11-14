package com.burakkarahan.newsbreak.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.burakkarahan.newsbreak.R
import com.burakkarahan.newsbreak.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    var tf1: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tf1 = Typeface.createFromAsset(applicationContext.assets, "fonts/DamionRegular.ttf")
        binding.tvSplash.setTypeface(tf1)

        val animasyon = AnimationUtils.loadAnimation(this, R.anim.transition)
        binding.tvSplash.startAnimation(animasyon)
        binding.ivSplash.startAnimation(animasyon)
        val intent = Intent(this, LoginActivity::class.java)

        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(intent)
                    finish()
                }
            }
        }
        timer.start()

    }
}