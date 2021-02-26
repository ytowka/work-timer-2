package com.ytowka.worktimer2.adapters

import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.databinding.ItemSetBinding

class SetListAdapter( var onItemClicked: (ActionSet) -> Unit = {}, var onItemLongClicked: (ActionSet) -> Unit = {}) : RecyclerView.Adapter<SetListAdapter.SetViewHolder>() {
    private var list = listOf<ActionSet>()
    private val checkedList = mutableListOf<ActionSet>()

    fun update(setList: List<ActionSet>){
        val oldList = list
        list = setList
        val diffResult = DiffUtil.calculateDiff(SetListDiffCallback(
            oldList,setList
        ))
        checkedList.clear()
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SetViewHolder(ItemSetBinding.inflate(inflater,parent,false))
    }

    fun getCheckedActions(): List<ActionSet>{
        return checkedList
    }

    fun unCheckAll(){
        val checkedListCopy = checkedList.toList()
        checkedList.clear()
        checkedListCopy.forEach {
            notifyItemChanged(list.indexOf(it))
            Log.i("debug","updated: ${it}")
        }
    }
    fun checkItem(actionSet: ActionSet, checked: Boolean){
        val id = list.indexOf(actionSet)
        Log.i("debug","index: ${id}; ${list.size}")

        if(checked){
            if(!checkedList.contains(actionSet)) checkedList.add(actionSet)
        }else {
            if(checkedList.contains(actionSet)) checkedList.remove(actionSet)
        }

        notifyItemChanged(id)
    }
    fun checkItem(actionSet: ActionSet){
        val id = list.indexOf(actionSet)
        Log.i("debug","index: ${id}; ${list.size}")

        if(checkedList.contains(actionSet)){
            checkedList.remove(actionSet)
        }else {
            checkedList.add(actionSet)
        }
        /*val debugList = "list: ["+list.joinToString {
            it.setInfo.setId.toString()
        }+"]"
        Log.i("debug","selected ${actionSet.setInfo.setId} "+debugList)*/
        notifyItemChanged(id)
    }
    fun checkedItemsCount(): Int = checkedList.size


    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val actionSet = list[position]
        holder.bind(actionSet)
        holder.checked = checkedList.contains(list[position])
    }
    override fun getItemCount(): Int = list.size

    inner class SetViewHolder(private val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root){
        lateinit var actionSet: ActionSet
        
        var checked = false
        set(value) {
            //binding.itemCardLayout.background.setTint(binding.root.resources.getColor(R.color.cardViewSelected))
            //binding.itemCardLayout.background.setColorFilter(binding.root.resources.getColor(R.color.cardViewSelected),PorterDuff.Mode.ADD)
            if(value){
                binding.itemCardLayout.setBackgroundColor(binding.root.resources.getColor(R.color.cardViewSelected))
            }else{
                binding.itemCardLayout.setBackgroundColor(binding.root.resources.getColor(R.color.cardViewBg))
            }

            field = value
        }

        init {
            binding.cardSetInfo.setOnClickListener {
                onItemClicked(actionSet)
            }
            binding.cardSetInfo.setOnLongClickListener {
                onItemLongClicked(actionSet)
                true
            }
        }
        fun bind(actionSet: ActionSet){
            this.actionSet = actionSet
            binding.textApproximateTime.text = actionSet.getStringDuration()
            binding.textSetName.text = actionSet.setInfo.name
        }
    }
}
private class SetListDiffCallback(val oldList: List<ActionSet>, val newList: List<ActionSet>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition].setInfo.setId == newList[newItemPosition].setInfo.setId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var same = true
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        if(old.setInfo.name != new.setInfo.name) same = false
        if(old.getTotalDuration() != new.getTotalDuration()) same = false
        return same
    }
}
