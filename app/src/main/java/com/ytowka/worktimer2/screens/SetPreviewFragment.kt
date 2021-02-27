package com.ytowka.worktimer2.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.adapters.PreviewActionListAdapter
import com.ytowka.worktimer2.databinding.FragmentSetPreviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPreviewFragment : Fragment() {

    private lateinit var binding: FragmentSetPreviewBinding
    private lateinit var viewmodel: SetPreviewViewModel
    private val args: SetPreviewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewmodel = ViewModelProvider(this).get(SetPreviewViewModel::class.java)
        viewmodel.setId = args.setId
        binding = FragmentSetPreviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.rvPActionList.layoutManager = LinearLayoutManager(context)
        val adapter = PreviewActionListAdapter()
        binding.rvPActionList.adapter = adapter

        viewmodel.getSet().observe(this){
            binding.textSetNameTitle.text = it.setInfo.name
            binding.textTimeOnButton.text = it.getStringDuration()
            adapter.setup(it.actions)
        }
        binding.btnPEdit.setOnClickListener {

        }
        binding.btnPStart.setOnClickListener{

        }
    }
}