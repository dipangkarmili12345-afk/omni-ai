package com.example.data.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RewardedAdState(
    val isShowing: Boolean = false,
    val secondsRemaining: Int = 5,
    val isCompleted: Boolean = false,
    val rewardAmount: Int = 10,
    val adTitle: String = "Google AdMob Test Sponsored Video"
)

class AdsManager {

    companion object {
        // Official Google AdMob Test Ad Unit IDs
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private val _rewardedAdState = MutableStateFlow<RewardedAdState?>(null)
    val rewardedAdState: StateFlow<RewardedAdState?> = _rewardedAdState.asStateFlow()

    private val _showInterstitial = MutableStateFlow(false)
    val showInterstitial: StateFlow<Boolean> = _showInterstitial.asStateFlow()

    private var actionCounter = 0

    fun showRewardedAd(onRewardEarned: (Int) -> Unit) {
        _rewardedAdState.value = RewardedAdState(
            isShowing = true,
            secondsRemaining = 5,
            isCompleted = false,
            rewardAmount = 10
        )
    }

    suspend fun runRewardedAdTimer(onRewardEarned: (Int) -> Unit) {
        var current = _rewardedAdState.value ?: return
        while (current.secondsRemaining > 0) {
            delay(1000)
            val updatedSec = current.secondsRemaining - 1
            current = current.copy(secondsRemaining = updatedSec)
            _rewardedAdState.value = current
        }
        _rewardedAdState.value = current.copy(isCompleted = true)
        onRewardEarned(10)
    }

    fun dismissRewardedAd() {
        _rewardedAdState.value = null
    }

    fun triggerActionForInterstitial(isPremium: Boolean) {
        if (isPremium) return
        actionCounter++
        // Show interstitial every 4 actions
        if (actionCounter % 4 == 0) {
            _showInterstitial.value = true
        }
    }

    fun dismissInterstitial() {
        _showInterstitial.value = false
    }
}
