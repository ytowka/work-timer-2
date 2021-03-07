package com.ytowka.worktimer2.screens

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
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
        viewmodel.setup(args.setId)
        binding = FragmentSetPreviewBinding.inflate(inflater)

        binding.cardView.transitionName = "frame${args.setId}"
        binding.textSetNameTitle.transitionName = "name${args.setId}"
        binding.btnPStart.transitionName = "time${args.setId}"

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onStart()
        binding.rvPActionList.layoutManager = LinearLayoutManager(context)
        val adapter = PreviewActionListAdapter()
        binding.rvPActionList.adapter = adapter

        viewmodel.getSet().observe(viewLifecycleOwner){
            binding.textSetNameTitle.text = it.setInfo.name
            binding.textTimeOnButton.text = it.getStringDuration()
            adapter.setup(it.actions)

            binding.btnPStart.setOnClickListener{
                if(viewmodel.started){
                    viewmodel.stop()
                    binding.motionLayoutPreview.transitionToStart()
                }else{
                    viewmodel.start()
                    binding.motionLayoutPreview.transitionToEnd()
                }
            }
            startPostponedEnterTransition()
        }
        binding.btnPEdit.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.btnPEdit to "edit")
            val action = SetPreviewFragmentDirections.editSetFromPreview(args.setId)
            findNavController().navigate(action,extras)
        }
    }
}