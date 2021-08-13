package com.svap.chat.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.fantasy.utils.extentions.getDialogWithBinding
import com.fantasy.utils.extentions.goto
import com.fantasy.utils.extentions.gotoNewTask
import com.fantasy.utils.extentions.makeStatusBarTransparent
import com.google.gson.Gson
import com.svap.chat.R
import com.svap.chat.base.BaseVmActivity
import com.svap.chat.databinding.ActivityHomeBinding
import com.svap.chat.databinding.DialogCountrySelectBinding
import com.svap.chat.ui.authenticate.CountryBottomSheet
import com.svap.chat.ui.authenticate.LoginActivity
import com.svap.chat.ui.chat.RecentChatActivity
import com.svap.chat.ui.home.fragments.EditProfileFragment
import com.svap.chat.ui.home.fragments.HomeFragment
import com.svap.chat.ui.home.fragments.SupportFragment
import com.svap.chat.ui.home.viewModel.HomeViewModel
import com.svap.chat.utils.NO_NETWORK_ERROR
import com.svap.chat.utils.SOCKET_LOCAL
import com.svap.chat.utils.bindingAdapter.getAge
import com.svap.chat.utils.bindingAdapter.setProfileImageUrl
import com.svap.chat.utils.country.Country
import com.svap.chat.utils.country.getCountryFlag
import com.svap.chat.utils.extentions.showToast
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter

class HomeActivity : BaseVmActivity<ActivityHomeBinding, HomeViewModel>(
        R.layout.activity_home,
        HomeViewModel::class
) {
    var mSocket: Socket? = null
    var isConnected = true
    val TAG_SOCKET = "SOCKET_CHAT_Home"
    private val mAllCountriesList = ArrayList<Country>()
    private var mCurrentCountry = "101"

    private var mHomeFragment:HomeFragment?= null
    private val mSheet: CountryBottomSheet by lazy {
        CountryBottomSheet(
                mAllCountriesList
        ) {
            mSharePresenter.chooseCountryId = it.Id
            mSharePresenter.chooseCountryName = it.Name
            mSharePresenter.chooseCountryFlag = it.Flag
            mCurrentCountry = it.Id
            setCountry()
            updateHome()
            mHomeFragment?.onConnect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent(true)
        initProfile()
        updateHome()
    }

    override fun initObserver() {
        super.initObserver()
        mViewModel.mAllCountries.observe(this, Observer {
            mAllCountriesList.clear()
            mAllCountriesList.addAll(it)
            mAllCountriesList.map { c ->
                c.Flag = getCountryFlag(c.SortName)
            }

            mSharePresenter.allCountry = Gson().toJson(mAllCountriesList)
            if (intent.getBooleanExtra("show", false)) {
                showCountrySelection()
            } else {
                mCurrentCountry = mSharePresenter.chooseCountryId
                setCountry()
                updateHome()
                mHomeFragment?.onConnect()
            }
        })
    }

    private fun updateHome() {
        if(mHomeFragment == null){
            mHomeFragment = HomeFragment(mCurrentCountry)
        }
        updateFragmentContainer(mHomeFragment!!)
    }

    private fun setCountry() {
        mAllCountriesList.singleOrNull { it.Id == mCurrentCountry }?.let {
            mSharePresenter.chooseCountryFlag = it.Flag
        }
        mBinding.tvCountry.text = mSharePresenter.chooseCountryName
        mBinding.ivFlag.setImageResource(mSharePresenter.chooseCountryFlag)
    }

    override fun initListener() {
        super.initListener()

        mBinding.ivMenu.setOnClickListener {
            openDrawer()
        }

        mBinding.tvCountry.setOnClickListener {
            mSheet.show(supportFragmentManager, "")
        }

        mBinding.ivChat.setOnClickListener {
            destroySocket()
            goto(RecentChatActivity::class.java)
        }

        mBinding.navigationView.run {
            tvHome.setOnClickListener {
                closeDrawer()
                val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is HomeFragment) {
                    return@setOnClickListener
                }
                setTitle("")

                updateFragmentContainer(mHomeFragment!!)
            }

            tvSupport.setOnClickListener {
                closeDrawer()
                val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is SupportFragment) {
                    return@setOnClickListener
                }

                setTitle("Support")
                updateFragmentContainer(SupportFragment())
            }

            tvRateUs.setOnClickListener {
                closeDrawer()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data =
                        Uri.parse("https://play.google.com/store/apps/developer?id=${packageName}")
                startActivity(Intent.createChooser(intent, "Open with"))
            }

            tvEdit.setOnClickListener {
                closeDrawer()
                val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is EditProfileFragment) {
                    return@setOnClickListener
                }
                setTitle("Edit Profile")
                updateFragmentContainer(EditProfileFragment())
            }

            tvLogout.setOnClickListener {
                closeDrawer()
                mSharePresenter.clearData()
                gotoNewTask(LoginActivity::class.java)
            }
        }

        mBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerOpened(drawerView: View) {
                initProfile()
            }
        })
    }

    private fun initProfile() {
        mBinding.navigationView.tvName.text = mSharePresenter.getCurrentUser().first_name + " " +
                mSharePresenter.getCurrentUser().last_name
        mBinding.navigationView.tvAge.text = "Age, " + getAge(mSharePresenter.getCurrentUser().dob)
        setProfileImageUrl(
                mBinding.navigationView.ivProfile,
                mSharePresenter.getCurrentUser().image_name
        )
    }

    private fun updateFragmentContainer(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    private fun setTitle(title: String = "") {
        super.setTitle(title)
        mBinding.tvTitle.text = title
        mBinding.tvTitle.visibility = if (title == "") View.GONE else View.VISIBLE
        mBinding.tvCountry.visibility = if (title == "") View.VISIBLE else View.GONE
        mBinding.ivFlag.visibility = if (title == "") View.VISIBLE else View.GONE
        mBinding.ivChat.visibility = if (title == "") View.VISIBLE else View.GONE
    }

    private fun openDrawer() {
        mBinding.drawerLayout.openDrawer(Gravity.START)
    }

    private fun closeDrawer() {
        mBinding.drawerLayout.closeDrawer(Gravity.START)
    }

    private fun showCountrySelection() {
        getDialogWithBinding<DialogCountrySelectBinding>(
                R.layout.dialog_country_select,
                false
        ) { dialog, binding ->
            dialog.show()
            var country: Country? = null
            binding.tvCountry.setOnClickListener {
                val sheet = CountryBottomSheet(
                        mAllCountriesList
                ) {
                    country = it
                    binding.tvCountry.text = it.Name
                }
                sheet.show(supportFragmentManager, "")
            }

            binding.actionBtn.setOnClickListener {
                if (country == null) {
                    showToast("Please select country")
                    return@setOnClickListener
                }
                dialog.dismiss()

                mSharePresenter.chooseCountryId = country?.Id ?: ""
                mSharePresenter.chooseCountryName = country?.Name ?: ""
                mSharePresenter.chooseCountryFlag = country?.Flag ?: 0
                mCurrentCountry = country?.Id ?: ""

                setCountry()
                updateHome()
                mHomeFragment?.onConnect()
            }
        }
    }

    fun setChatIcon(isRead: Int) {
        mBinding.ivChat.setImageResource(if (isRead == 0) R.drawable.ic_chat_grey_read else R.drawable.ic_chat_grey)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment) {
            super.onBackPressed()
        } else {
            setTitle("")
            updateFragmentContainer(mHomeFragment!!)
        }
    }

    override fun onStart() {
        super.onStart()
        initSocket()
        Handler(Looper.getMainLooper()).postDelayed({
            mSocket?.connect()
        }, 100)
        Log.d(TAG_SOCKET, "onStart " + mSocket?.connected() + " " + mSocket?.id())
    }

    override fun onStop() {
        super.onStop()
        destroySocket()
    }

    private fun initSocket() {
        try {
            Log.d(TAG_SOCKET, "Current_user " + mSharePresenter.token)
            val mOptions = IO.Options()
            mOptions.query = "token=${mSharePresenter.token}&name=${mSharePresenter.getCurrentUser().user_name}"
            mSocket = IO.socket(SOCKET_LOCAL, mOptions)
            initSocketIo()
        } catch (e: SocketIOException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    private fun initSocketIo() {
        mSocket?.on(Socket.EVENT_CONNECT, onConnect)
        mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mHomeFragment?.initSocket()
    }

    fun destroySocket() {
        mSocket?.disconnect()
        mSocket?.close()
        mSocket = null
    }

    private val onConnect = Emitter.Listener {
        this@HomeActivity.runOnUiThread(object : Runnable {
            override fun run() {
                Log.d(TAG_SOCKET, "connected ")
                if(mAllCountriesList.isEmpty()){
                    mViewModel.getAllCountries()
                }else{
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                    if (currentFragment is HomeFragment) {
                        currentFragment.onConnect()
                    }
                }
            }
        })
    }

    private val onDisconnect = Emitter.Listener {
        this@HomeActivity.runOnUiThread(object : Runnable {
            override fun run() {
                isConnected = false
                Log.d(TAG_SOCKET, "$this disConnected")
            }
        })
    }

    private val onConnectError = Emitter.Listener {
        this@HomeActivity.runOnUiThread {
            if (!isConnected) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is HomeFragment) {
                    currentFragment.onConnectionError(NO_NETWORK_ERROR)
                }
            }
            Log.d(TAG_SOCKET, "onConnectError " + Gson().toJson(it))
        }
    }

}