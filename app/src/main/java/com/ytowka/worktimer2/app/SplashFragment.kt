package com.ytowka.worktimer2.app

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
import com.ytowka.worktimer2.screens.fragments.SetPreviewFragmentDirections
import com.ytowka.worktimer2.utils.C.Companion.observeOnce

class SplashFragment : Fragment(R.layout.fragment_splash){

    lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity() as MainActivity).get(SplashViewModel::class.java)
        viewModel.isTimerInitedLiveData.observe(viewLifecycleOwner){
            if(it){
                jumpToTimer()
                Log.i("debug","start with launched timer")
            }else{
                jumpToList()
                Log.i("debug","start clean")
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    private fun jumpToTimer() {
        jumpToList()
        val action = SetPreviewFragmentDirections.toTimer(-1, null)
        findNavController().navigate(action)
    }

    private fun jumpToList(){
        findNavController().navigate(R.id.to_list,null, navOptions {
            popUpTo(R.id.splashFragment, popUpToBuilder = {
                inclusive = true
            })
        })
    }
}