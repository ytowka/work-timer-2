package com.ytowka.worktimer2.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.databinding.FragmentTimersListBinding


class TimersListFragment : Fragment() {

    lateinit var binding: FragmentTimersListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentTimersListBinding.inflate(inflater)
        return binding.root
    }


}