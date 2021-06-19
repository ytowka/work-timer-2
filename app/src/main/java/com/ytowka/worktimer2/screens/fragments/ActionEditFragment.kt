package com.ytowka.worktimer2.screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.FragmentActionEditBinding
import com.ytowka.worktimer2.screens.viewmodels.ActionEditViewModel

class ActionEditFragment : Fragment() {

    lateinit var binding: FragmentActionEditBinding
    lateinit var viewmodel: ActionEditViewModel
    private val args: ActionEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActionEditBinding.inflate(inflater)
        viewmodel = ViewModelProvider(this).get(ActionEditViewModel::class.java)
        viewmodel.setup(args.actId).observe(viewLifecycleOwner){
            initViews(it)
        }
        return inflater.inflate(R.layout.fragment_action_edit, container, false)
    }

    private fun initViews(action: Action){

    }
}