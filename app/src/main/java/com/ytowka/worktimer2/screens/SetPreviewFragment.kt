package com.ytowka.worktimer2.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.databinding.FragmentSetPreviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPreviewFragment : Fragment() {

    private lateinit var binding: FragmentSetPreviewBinding
    private lateinit var viewmodel: SetPreviewViewModel
    private val args: SetPreviewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewmodel = ViewModelProvider(this).get(SetPreviewViewModel::class.java)
        binding = FragmentSetPreviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        viewmodel.setId = args.setId
        super.onStart()
        viewmodel.getSet().observe(this){
            binding.previewToolbar.title = it.setInfo.name
        }
    }
}