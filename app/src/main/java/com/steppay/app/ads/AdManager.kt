package com.steppay.app.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdManager {

    private var appOpenAd: AppOpenAd? = null
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    var isAppOpenAdLoading = false
        private set

    fun loadAppOpenAd(context: Context, onLoaded: (() -> Unit)? = null) {
        if (appOpenAd != null || isAppOpenAdLoading) return
        isAppOpenAdLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AdConfig.APP_OPEN_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenAdLoading = false
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    isAppOpenAdLoading = false
                }
            }
        )
    }

    fun showAppOpenAd(activity: Activity, onDismissed: (() -> Unit)? = null) {
        val ad = appOpenAd
        if (ad != null) {
            ad.show(activity)
            appOpenAd = null
            onDismissed?.invoke()
        } else {
            onDismissed?.invoke()
        }
    }

    fun loadRewardedAd(context: Context, onLoaded: (() -> Unit)? = null) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdConfig.REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: (Int) -> Unit, onFailed: () -> Unit) {
        val ad = rewardedAd
        if (ad != null) {
            ad.show(activity) { rewardItem ->
                onRewardEarned(rewardItem.amount.coerceAtLeast(100))
                rewardedAd = null
                loadRewardedAd(activity)
            }
        } else {
            onFailed()
            loadRewardedAd(activity)
        }
    }

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdConfig.INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: (() -> Unit)? = null) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed?.invoke()
                }
                
                override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onAdDismissed?.invoke()
                }
            }
            ad.show(activity)
        } ?: run {
            onAdDismissed?.invoke()
        }
    }

    fun loadRewardedInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            AdConfig.REWARDED_INTERSTITIAL_ID,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedInterstitialAd = null
                }
            }
        )
    }

    fun showRewardedInterstitialAd(activity: Activity, onRewardEarned: (Int) -> Unit) {
        rewardedInterstitialAd?.let { ad ->
            ad.show(activity) { rewardItem ->
                onRewardEarned(rewardItem.amount.coerceAtLeast(50))
            }
            rewardedInterstitialAd = null
            loadRewardedInterstitialAd(activity)
        }
    }
}
