package com.ytowka.worktimer2.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.ItemEditActionBinding
import com.ytowka.worktimer2.utils.C
import java.util.*

class EditActionListAdapter(
    private var actionList: MutableList<Action>,
    val actionEditCallback: ActionEditCallback
) : RecyclerView.Adapter<EditActionListAdapter.EditActionViewHolder>(){

    fun setUp(actions: List<Action>){
        val oldList = actionList
        actionList = actions.toMutableList()

        val diffResult = DiffUtil.calculateDiff(
            ActionListDiffCallback(
                oldList, actions
            )
        )
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditActionViewHolder {
        return EditActionViewHolder(
            ItemEditActionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: EditActionViewHolder, position: Int) {
        holder.bind(position, actionList[position])
    }
    override fun getItemCount(): Int = actionList.size

    // **Cough**
    fun onItemMove(fromPos: Int, toPos: Int){
        notifyItemMoved(fromPos, toPos)
        val buffer = actionList[fromPos]
        actionList.removeAt(fromPos)
        actionList.add(toPos,buffer)
        actionEditCallback.onMove(actionList)
    }
    fun remove(pos: Int){
        actionEditCallback.onDelete(actionList[pos])
    }

    inner class EditActionViewHolder(private val binding: ItemEditActionBinding): RecyclerView.ViewHolder(
        binding.root
    ){
        lateinit var action: Action
        init {
            binding.editTextTextMM.addTextChangedListener{
                actionEditCallback.onMinutesEdit(action, it.toString())
            }
            binding.editTextTextSS.addTextChangedListener{
                actionEditCallback.onSecondsEdit(action, it.toString())
            }
            binding.textViewActionName.addTextChangedListener{
                actionEditCallback.onNameEdit(action, it.toString())
            }
        }

        fun bind(pos: Int, action: Action){
            this.action = action

            val time = action.getStringDuration().split(":")
            binding.editTextTextMM.setText(if (time.size == 1) "00" else time[0])
            binding.editTextTextSS.setText(if (time.size == 1) time[0] else time[1])

            binding.textViewActionIndex.text = pos.toString()
            binding.textViewActionName.setText(action.name)

            if(action.exactTimeDefine){
                binding.editTextTextSS.isEnabled = true
                binding.editTextTextMM.isEnabled = true

                binding.imageViewReplay.visibility = View.INVISIBLE

                binding.editTextTextSS.visibility = View.VISIBLE
                binding.editTextTextMM.visibility = View.VISIBLE
                binding.sep.visibility = View.VISIBLE
            }else{
                binding.editTextTextSS.isEnabled = false
                binding.editTextTextMM.isEnabled = false

                binding.imageViewReplay.visibility = View.VISIBLE

                binding.editTextTextSS.visibility = View.INVISIBLE
                binding.editTextTextMM.visibility = View.INVISIBLE
                binding.sep.visibility = View.INVISIBLE
            }
            val textColor = binding.root.context.getColor(if (C.isColorLight(action.color)) R.color.on_light else R.color.on_dark)
            binding.textViewActionIndex.setTextColor(textColor)
            binding.textViewActionIndex.backgroundTintList = ColorStateList.valueOf(action.color)
        }
    }

    private class ActionListDiffCallback(val oldList: List<Action>, val newList: List<Action>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            var same = true
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]

            if(old.duration != new.duration) same = false
            if(old.color != new.color) same = false
            if(old.name != old.name) same = false

            return same
        }
    }
    interface ActionEditCallback{
        fun onNameEdit(action: Action, newName: String)
        fun onSecondsEdit(action: Action, seconds: String)
        fun onMinutesEdit(action: Action, minutes: String)
        fun onMove(actions: List<Action>)
        fun onDelete(action: Action)
    }
}