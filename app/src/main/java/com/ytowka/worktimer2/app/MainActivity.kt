package com.ytowka.worktimer2.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.databinding.ActivityMainBinding
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    /*override fun onResume() {
        super.onResume()
        binding.fragment.findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
            var string = ""
            binding.fragment.findNavController().backStack.forEach{
                string+="${it.destination.label}, "
            }

            Log.i("nav_debug", "back stack: $string")
        }
    }*/
}