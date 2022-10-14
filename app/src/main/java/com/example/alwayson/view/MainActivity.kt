package com.example.alwayson.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alwayson.R
import com.example.alwayson.data.Domain
import com.example.alwayson.data.DomainApi
import com.example.alwayson.databinding.ActivityMainBinding
import com.example.alwayson.databinding.CustomDialogFragmentBinding
import com.example.alwayson.databinding.CustomDialogWebBinding
import com.example.alwayson.utils.NetworkHelper
import com.example.alwayson.utils.RetrofitClient
import com.example.alwayson.utils.Status
import com.example.alwayson.utils.Utils.Companion.getJsonDataFromAsset
import com.example.alwayson.viewModel.MainVieModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var customDialog: AlertDialog? = null

    private val mainViewModel: MainVieModel by viewModels()

    private lateinit var mainActivityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityMainBinding.root)

        val jsonFileString = getJsonDataFromAsset(applicationContext, "url.json")
        val obj = JSONObject(jsonFileString!!)

        val partnerName = obj.getString("PartnerName").toString()
        val configURL = obj.getString("ConfigURL").toString()
        val path = obj.getString("fileName").toString()
        val backgroundColor = obj.getString("backgroundColor")

        mainActivityMainBinding.splashContent.setBackgroundColor(Color.parseColor(backgroundColor))


        val alwaysonurl = configURL + path

        val networkHelper = NetworkHelper(this)

        val configStr="/assets/json/config.json"

        Log.d("MainAc", "Status:  ${networkHelper.isNetworkConnected()}")

        mainViewModel.fetchUrls(alwaysonurl)

        mainViewModel.urls.observe(this, { it1 ->

            Log.d("MainAc", "Status:  ${networkHelper.isNetworkConnected()}")
            customDialog?.dismiss()
            var count = 0
            when (it1.status) {
                Status.SUCCESS -> {
                    // loading_content.visibility = View.VISIBLE

                    val domainList = it1.data!!.domains

                    if (partnerName == it1.data.Name) {


                        for (i in domainList.indices) {

                            val concatenated =
                                "https://" + domainList[i] + configStr

                            Log.d("Main", "concatenated[i]: $concatenated")


                            val apiInterface =
                                RetrofitClient.getClient("https://" + domainList[i] + "/")
                                    .create(DomainApi::class.java)


                            val call: Call<Domain> = apiInterface.getDomaonApi(concatenated)

                            /*  apiInterface.getDomaonApi(concatenated).subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(object :Observer<Domain>{
                                      override fun onSubscribe(d: Disposable) {
                                      }

                                      override fun onNext(t: Domain) {
                                          if (networkHelper.isNetworkConnected() && t.response.isSuccessful && response.body() != null && response.body()
                                                  .toString().contains("PartnerId")
                                          ) {

                                              Log.d("TAG", "Body:  ${response.body()}")


                                              val currentUrl = response.raw().request.url.toString()
                                                  .replace("/assets/json/config.json", "")


                                              val intent =
                                                  Intent(this@MainActivity, WebActivity::class.java)
                                              intent.putExtra("currentUrl", currentUrl)
                                              //  intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                              startActivity(
                                                  intent
                                              )
                                              overridePendingTransition(
                                                  R.anim.slide_in_right,
                                                  R.anim.slide_out_left
                                              )

                                              if (this@MainActivity.isFinishing && this@MainActivity.isDestroyed) {

                                                  mainActivityMainBinding.splashContent.visibility = View.GONE
                                              }

                                          }
                                      }

                                      override fun onError(e: Throwable) {
                                      }

                                      override fun onComplete() {
                                      }

                                  })*/
                            call.enqueue(object : Callback<Domain?> {

                                override fun onResponse(
                                    call: Call<Domain?>,
                                    response: Response<Domain?>
                                ) {

                                    if (networkHelper.isNetworkConnected() && response.isSuccessful && response.body() != null && response.body()
                                            .toString().contains("PartnerId")
                                    ) {

                                        Log.d("TAG", "Body:  ${response.body()}")


                                        val currentUrl = response.raw().request.url.toString()
                                            .replace(configStr, "")


                                        val intent =
                                            Intent(this@MainActivity, WebActivity::class.java)
                                        intent.putExtra("currentUrl", currentUrl)
                                        //  intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(
                                            intent
                                        )
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )

                                        if (this@MainActivity.isFinishing && this@MainActivity.isDestroyed) {

                                            mainActivityMainBinding.splashContent.visibility =
                                                View.GONE
                                        }

                                    } else {
                                        Log.d("TAG","Request Error : " + response.errorBody());
                                    }
                                }



                                override fun onFailure(
                                    call: Call<Domain?>,
                                    t: Throwable
                                ) {
                                    Log.d("TAG", "CHKA")

                                    val errorUrl = call.request().url
                                    Log.d(
                                        "onFailure",
                                        "non workable domain:  $errorUrl"
                                    )

                                    mainViewModel.postErrorSites(
                                        "mDg9KkqCX91F8l2F7Q3hG67tI9wtF2kq93bPOCHLjDqFNM9sXcRIA",
                                        "AlwaysON-Android",
                                        "joymatik",
                                        errorUrl.toString().replace(configStr, "")
                                    )

                                    count++
                                    if (count == domainList.size) {
                                        pageErrorDialog()
                                    }
                                    call.cancel()
                                }
                            })
                        }
                    }
                }


                Status.LOADING -> {

                    if (networkHelper.isNetworkConnected()) {
                        mainActivityMainBinding.loadingTxt.visibility = View.VISIBLE

                        //val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                        //progressbar.startAnimation(rotateAnimation)
                    } else {
                        showCustomDialog(alwaysonurl)
                    }
                }


                Status.ERROR -> {
                    mainActivityMainBinding.splashContent.visibility = View.GONE
                    Toast.makeText(this, it1.message, Toast.LENGTH_LONG).show()

                    showCustomDialog(alwaysonurl)

                }
            }
        })
    }


    private fun showCustomDialog(path: String) {
        val dialogBinding: CustomDialogFragmentBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.custom_dialog_fragment,
                null,
                false
            )

        customDialog = AlertDialog.Builder(this, 0).create()

        customDialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setView(dialogBinding.root)
            setCancelable(false)
        }?.show()


        dialogBinding.retryBtn.setOnClickListener {
            mainViewModel.fetchUrls(path)
        }
    }


    private fun pageErrorDialog() {
        var pageErrorDialogBinding: CustomDialogWebBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.custom_dialog_web,
                null,
                false
            )

        val customDialog1 = AlertDialog.Builder(this, 0).create()

        customDialog1.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setView(pageErrorDialogBinding!!.root)
            setCancelable(false)
        }.show()


        pageErrorDialogBinding!!.closeBtn.setOnClickListener {
            customDialog1.dismiss()
            pageErrorDialogBinding = null
            finish()
        }
    }
}


