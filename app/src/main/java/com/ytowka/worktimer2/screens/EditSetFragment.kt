package com.ytowka.worktimer2.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.databinding.FragmentEditSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSetFragment : Fragment() {

    private lateinit var binding: FragmentEditSetBinding
    private val args: EditSetFragmentArgs by navArgs()
    private lateinit var viewModel: EditSetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentEditSetBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(EditSetViewModel::class.java)
        viewModel.setId = args.setId
        return binding.root
    }
    override fun onStart() {
        super.onStart()

    }
}