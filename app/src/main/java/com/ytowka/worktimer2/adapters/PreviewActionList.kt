package com.ytowka.worktimer2.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.ItemPreviewActionBinding

class PreviewActionListAdapter : RecyclerView.Adapter<PreviewActionViewHolder>(){
    private var list = listOf<Action>()

    fun setup(list: List<Action>){
        this.list = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewActionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviewActionViewHolder(ItemPreviewActionBinding.inflate(inflater, parent, false))
    }
    override fun onBindViewHolder(holder: PreviewActionViewHolder, position: Int) {
        holder.bind(list[position])
    }
    override fun getItemCount(): Int = list.size
}
class PreviewActionViewHolder(private val binding: ItemPreviewActionBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(action: Action){
        val bg = ContextCompat.getDrawable(binding.root.context,R.drawable.shape_rounded)
        bg?.setTint(action.color)
        binding.imagePColor.background = bg
        //binding.imagePColor.setBackgroundColor(action.color)

        binding.imegePReplay.visibility = if(action.exactTimeDefine) View.GONE else View.VISIBLE
        binding.textPActionTime.visibility = if(action.exactTimeDefine) View.VISIBLE else View.GONE

        binding.textPActionTime.text = action.getStringDuration()
        binding.textPActionName.text = action.name
    }
}