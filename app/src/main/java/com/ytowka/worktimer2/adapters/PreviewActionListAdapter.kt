package com.ytowka.worktimer2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.ItemPreviewActionBinding
import com.ytowka.worktimer2.utils.C.Companion.TAG

class PreviewActionListAdapter(val onClick: (Action) -> Unit) : RecyclerView.Adapter<PreviewActionListAdapter.PreviewActionViewHolder>(){
    private var actionList = listOf<Action>()

    var currentAction: Action? = null
    set(value) {
        val oldActionId = actionList.indexOf(field)
        field = value
        notifyItemChanged(oldActionId)
        val index = actionList.indexOf(value)
        notifyItemChanged(index)
    }

    fun setup(list: List<Action>){
        actionList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewActionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviewActionViewHolder(ItemPreviewActionBinding.inflate(inflater, parent, false))
    }
    override fun onBindViewHolder(holder: PreviewActionViewHolder, position: Int) {
        holder.bind(actionList[position])
    }
    override fun getItemCount(): Int = actionList.size

    inner class PreviewActionViewHolder(private val binding: ItemPreviewActionBinding) : RecyclerView.ViewHolder(binding.root){
        private lateinit var action: Action
        init {
            binding.root.setOnClickListener {
                onClick(action)
            }
        }
        fun bind(action: Action){
            this.action = action

            val bg = ContextCompat.getDrawable(binding.root.context,R.drawable.shape_rounded25)
            bg?.setTint(action.color)
            binding.imagePColor.background = bg


            if(action == currentAction){
                //binding.layout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.current_action_bg))
                binding.layout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.current_action_bg))
            }else{
                binding.layout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.cardViewBg))
                //binding.layout.backgroundTintList = null
            }

            binding.imegePReplay.visibility = if(action.exactTimeDefine) View.GONE else View.VISIBLE
            binding.textPActionTime.visibility = if(action.exactTimeDefine) View.VISIBLE else View.GONE

            binding.textPActionTime.text = action.getStringDuration()
            binding.textPActionName.text = action.name
        }
    }
}
