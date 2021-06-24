package com.ytowka.worktimer2.adapters

import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.databinding.ItemActiontypeBinding
import com.ytowka.worktimer2.utils.C.Companion.isColorLight

class ActionTypesAdapter(
    actionTypes: List<ActionType>,
    private val onClick: (ActionType) -> Unit,
    private val onLongClick: (ActionType) -> Unit,
    private val defaultTypes: List<ActionType>
) : RecyclerView.Adapter<ActionTypesAdapter.ActionTypeViewHolder>(){

    private var actionTypes: List<ActionType> = actionTypes.toMutableList().apply {
        this.add(0,defaultTypes[0])
        this.add(1,defaultTypes[1])
        this.add(defaultTypes[2])
    }

    fun setUp(actionTypes: List<ActionType>){
        val newList = actionTypes.toMutableList()
        newList.add(0,defaultTypes[0])
        newList.add(1,defaultTypes[1])
        newList.add(defaultTypes[2])
        this.actionTypes = newList
        Log.i("debug",newList.toString())
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionTypeViewHolder {
        return ActionTypeViewHolder(ItemActiontypeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: ActionTypeViewHolder, position: Int) {
        holder.bind(actionTypes[position])
    }
    override fun getItemCount(): Int {
        return actionTypes.size
    }
    inner class ActionTypeViewHolder(private val binding: ItemActiontypeBinding): RecyclerView.ViewHolder(binding.root){
        private lateinit var actionType: ActionType
        init {
            binding.text.setOnClickListener {
                onClick(actionType)
            }
            binding.text.setOnLongClickListener {
                onLongClick(actionType)
                true
            }
        }
        fun bind(actionType: ActionType){
            binding.text.setTextColor(binding.root.context.getColor(if(isColorLight(actionType.color)) R.color.on_light else R.color.on_dark))

            this.actionType = actionType
            binding.text.text = actionType.name
            binding.text.backgroundTintList = (ColorStateList.valueOf(actionType.color))
        }
    }
}