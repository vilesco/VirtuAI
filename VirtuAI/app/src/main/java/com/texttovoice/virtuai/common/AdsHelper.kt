package com.texttovoice.virtuai.common

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback



var mInterstitialAd: InterstitialAd? = null
var rewardedAd: RewardedAd? = null

@Composable
fun BannerAdView() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = Constants.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

fun loadInterstitial(context: Context) {
    InterstitialAd.load(
        context,
        Constants.INTERSTITIAL_AD_UNIT_ID,
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        }
    )
}

fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
    val activity = context.findActivity()

    if (mInterstitialAd != null && activity != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null

                loadInterstitial(context)
                onAdDismissed()
            }
        }
        mInterstitialAd?.show(activity)
    }
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
}


fun loadRewarded(context: Context) {
    RewardedAd.load(context, Constants.REWARDED_AD_UNIT_ID,
        AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("TAG", "Ad was loaded.")
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d("TAG", it) }
                rewardedAd = null
            }
        })

}


fun showRewarded(context: Context, onRewarded: () -> Unit) {
    val activity = context.findActivity()

    if (rewardedAd != null && activity != null) {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                rewardedAd = null
//                loadRewarded(context)
            }

            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null

                loadRewarded(context)

            }
        }
        rewardedAd?.show(activity) {
            onRewarded()
        }
    }
}
