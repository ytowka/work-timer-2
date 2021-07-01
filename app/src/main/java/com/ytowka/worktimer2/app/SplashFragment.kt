package com.ytowka.worktimer2.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.screens.timer.SetPreviewFragmentDirections
import com.ytowka.worktimer2.screens.timer.TimerService
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce

class SplashFragment : Fragment(R.layout.fragment_splash) {

    lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity() as MainActivity).get(SplashViewModel::class.java)

        //connecting to service for checking is timer already started
        val intentService = Intent(context, TimerService::class.java)
        intentService.action = C.ACTION_CHECK_IS_LAUNCHED

        requireActivity().bindService(
            intentService,
            viewModel.serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        viewModel.isTimerInitedLiveData.observe(viewLifecycleOwner) {
            requireContext().unbindService(viewModel.serviceConnection)
            if (it) {
                jumpToTimer()
            } else {
                jumpToList()
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun jumpToTimer() {
        jumpToList()
        val action = SetPreviewFragmentDirections.toTimer(-1, null)
        findNavController().navigate(action)
    }

    private fun jumpToList() {
        findNavController().navigate(R.id.to_list, null, navOptions {
            popUpTo(R.id.splashFragment, popUpToBuilder = {
                inclusive = true
            })
        })
    }
}