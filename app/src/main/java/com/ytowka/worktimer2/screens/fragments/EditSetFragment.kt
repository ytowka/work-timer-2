package com.ytowka.worktimer2.screens.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.databinding.FragmentEditSetBinding
import com.ytowka.worktimer2.screens.viewmodels.EditSetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSetFragment : Fragment() {


    private lateinit var binding: FragmentEditSetBinding
    private val args: EditSetFragmentArgs by navArgs()
    private lateinit var viewModel: EditSetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentEditSetBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(EditSetViewModel::class.java)

        //if this fragment opened for new action set, setId = -1
        binding.toolbar2.title = requireContext().getString(if(args.setId == -1) R.string.creating else R.string.editing)
        viewModel.initViewModel(args.setId).observe(viewLifecycleOwner){

        }
        return binding.root
    }
}