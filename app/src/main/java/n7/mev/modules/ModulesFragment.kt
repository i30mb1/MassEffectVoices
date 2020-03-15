package n7.mev.modules

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import n7.mev.R
import n7.mev.databinding.BottomDrawerBinding
import n7.mev.databinding.DialogRateAppBinding
import n7.mev.databinding.ModulesFragmentBinding
import n7.mev.main.MainActivity
import n7.mev.purchaseUtils.IabHelper
import n7.mev.purchaseUtils.IabHelper.*
import n7.mev.util.SnackbarUtils
import java.text.SimpleDateFormat
import java.util.*

class ModulesFragment : Fragment(R.layout.modules_fragment) {
    private val sku_beer = "once_per_month_subscription"
    private val viewModel: ModulesViewModel by viewModels()
    private lateinit var binding: ModulesFragmentBinding
    private var scaleDown: ObjectAnimator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ModulesFragmentBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
            it.fragment = this
        }

        setupListeners()
        setupToolbar()
        setupPagedListAdapter()
        checkDayForDialogProm()
        setupAnimation()
    }

    fun test(view: View?): Boolean {
//        openActivityFromModule();
//        startActionMode();
//        sendErrorResponse();
        return true
    }

    private fun sendErrorResponse() {
        val intent = Intent(Intent.ACTION_APP_ERROR)
        startActivity(intent)
    }

    private fun startActionMode() {
        val actionMode = (activity as ModulesActivity?)!!.startSupportActionMode(object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.menu_multi_download, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                // при вызове invalidate
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                // обработка нажатий на айтемы меню
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
        })
    }

    private fun openActivityFromModule() {
        if (viewModel!!.modulesCanBeInstall().contains("avina")) {
            return
        }
        val packageName = "n7.mev.avina"
        val avinaSampleClassName = "$packageName.SecretActivity"
        val intent = Intent()
        intent.setClassName(packageName, avinaSampleClassName)
        startActivity(intent)
    }

    fun showAvailableModules(view: View?) {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        val binding: BottomDrawerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.bottom_drawer, null, false)
        bottomSheetDialog.setContentView(binding.root)
        for (module in viewModel!!.modulesCanBeInstall()) {
            binding.navBottomDrawer.menu.add(module)
        }
        binding.navBottomDrawer.setNavigationItemSelectedListener { item ->
            bottomSheetDialog.dismiss()
            viewModel!!.installModule(item.title.toString())
            true
        }
        bottomSheetDialog.show()
    }

    private fun setupAnimation() {
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                binding!!.ivModulesFragmentN7,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f)
        )
        scaleDown.setDuration(1000L)
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE)
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE)
    }

    override fun onPause() {
        if (scaleDown != null) {
            scaleDown!!.cancel()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (scaleDown != null) {
            scaleDown!!.start()
        }
    }

    private fun setupListeners() {
        viewModel!!.showSnackbar.observe(this, Observer { snackbarMessageResourceId -> SnackbarUtils.showSnackbar(view, getString(snackbarMessageResourceId!!)) })
        viewModel!!.startConfirmationDialog.observe(this, Observer { viewModel!!.startConfirmationDialog(activity) })
    }

    private fun checkDayForDialogProm() {
        val dialogShowed = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DIALOG_SHOWED, false)
        if (dialogShowed) return
        val currentDayInString = SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().time)
        val currentDay = currentDayInString.toInt()
        val lastDay = PreferenceManager.getDefaultSharedPreferences(context).getInt(LAST_DAY, 0)
        if (currentDay == lastDay + 1) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(DIALOG_SHOWED, true).apply()
            showDialogRate()
        } else {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(LAST_DAY, currentDay).apply()
        }
    }

    private fun showDialogRate() {
        if (context == null) return
        val binding: DialogRateAppBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_rate_app, null, false)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window.attributes.windowAnimations = R.style.DialogTheme
        dialog.show()
        binding.bDialogRateApp.setOnClickListener {
            openAppStore()
            dialog.dismiss()
        }
    }

    fun showDialogDonate() {
        if (context == null) return
        val binding: DialogRateAppBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_rate_app, null, false)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(binding.root)
        binding.tvDialogRateAppTitle.setText(R.string.dialog_donate_title)
        binding.tvDialogRateAppSummary.setText(R.string.dialog_donate_summary)
        val dialog = builder.create()
        dialog.window.attributes.windowAnimations = R.style.DialogTheme
        dialog.show()
        binding.bDialogRateApp.setOnClickListener {
            setupPurchaseHelper()
            dialog.dismiss()
        }
    }

    private fun setupPurchaseHelper() {
        //todo key
        val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB"
        val mHelper = IabHelper(activity, base64EncodedPublicKey)
        mHelper.startSetup { result ->
            if (result.isSuccess) {
                buy(mHelper)
            }
        }
    }

    private fun buy(mHelper: IabHelper?) {
        try {
            val requestCodePurchase = 1
            mHelper!!.launchPurchaseFlow(activity, sku_beer, requestCodePurchase, OnIabPurchaseFinishedListener { result, info ->
                if (mHelper == null) return@OnIabPurchaseFinishedListener
                if (result.isFailure) return@OnIabPurchaseFinishedListener
                checkInventor(mHelper)
            })
        } catch (e: IabAsyncInProgressException) {
            e.printStackTrace()
        }
    }

    private fun checkInventor(mHelper: IabHelper) {
        try {
            mHelper.queryInventoryAsync(QueryInventoryFinishedListener { result, inv ->
                if (result!!.isFailure) return@QueryInventoryFinishedListener
                if (inv!!.hasPurchase(sku_beer)) {
                    try {
                        mHelper.consumeAsync(inv.getPurchase(sku_beer)) { purchase, result ->
                            Snackbar.make(binding!!.root, "NICE", Snackbar.LENGTH_LONG).show()
                            MediaPlayer.create(context, R.raw.money).start()
                        }
                    } catch (e: IabAsyncInProgressException) {
                        e.printStackTrace()
                    }
                }
            })
        } catch (e: IabAsyncInProgressException) {
            e.printStackTrace()
        }
    }

    private fun openAppStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context!!.packageName)))
        } catch (a: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context!!.packageName)))
        }
    }

    private fun setupToolbar() {
        (activity as ModulesActivity?)!!.setSupportActionBar(binding!!.toolbar)
        activity!!.title = ""
    }

    fun openModule(view: View?, moduleName: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(MODULE_NAME, moduleName)
        if (activity != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                    Pair(binding!!.ivModulesFragmentN7, "iv2") //                    new Pair<View, String>(view.findViewById(R.id.iv_modules_item_icon), "iv")
            )
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    fun showDialogDeleteModule(moduleName: String?): Boolean {
        if (context != null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage(getString(R.string.dialog_delete_module))
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                viewModel!!.deleteModule(moduleName)
                dialog.dismiss()
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.window.attributes.windowAnimations = R.style.DialogTheme
            dialog.show()
        }
        return true
    }

    private fun setupPagedListAdapter() {
        binding!!.rvModulesFragment.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = ModulesPagedListAdapter(this)
        binding!!.rvModulesFragment.adapter = adapter
        viewModel!!.availableModules.observe(this, Observer { strings -> adapter.addAll(strings) })
    }

    companion object {
        const val MODULE_NAME = "MODULE_NAME"
        private const val LAST_DAY = "LAST_DAY"
        private const val DIALOG_SHOWED = "DIALOG_SHOWED"
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }
    }
}