package com.svap.chat.ui.authenticate

import android.os.Bundle
import com.fantasy.utils.extentions.goto
import com.fantasy.utils.extentions.gotoFinish
import com.fantasy.utils.extentions.gotoNewTask
import com.fantasy.utils.extentions.hideSystemUI
import com.svap.chat.R
import com.svap.chat.base.BaseActivity
import com.svap.chat.databinding.ActivitySplashBinding
import com.svap.chat.ui.home.HomeActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        mBinding.root.postDelayed({
            if(mSharePresenter.isLoggedId){
                gotoNewTask(HomeActivity::class.java)
            }else{
                gotoNewTask(LoginActivity::class.java)
            }
        }, 2000)
    }

}