package com.example.ffixvalert.bindingadapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.ffixvalert.R
import com.example.ffixvalert.constant.PrefKeys
import com.example.ffixvalert.model.ServerStatus

@BindingAdapter("android:setCheckBox")
fun setCheckBox(checkBox: CheckBox, server: ServerStatus){
    val prefs = checkBox.context.getSharedPreferences(PrefKeys.PACKAGE_NAME,Context.MODE_PRIVATE)
    val savedList = prefs.getStringSet(PrefKeys.SERVER_SET,null)
    savedList?.let{
        if(it.contains(server.serverName)){
            checkBox.isChecked = true
        }
    }
}

@BindingAdapter("android:setImageIcon")
fun setImageIcon(view: ImageView, server: ServerStatus){
    when(server.canCreateCharacter){
        true -> view.setImageResource(R.drawable.ic_baseline_check_circle_24)
        false -> view.setImageResource(R.drawable.ic_baseline_cancel_24)
    }
}

@BindingAdapter("android:setTimerIcon")
fun setTimerIcon(view: ImageButton, imageResourceId: LiveData<Int>){
    imageResourceId.value?.let{
        view.setImageResource(it)
    }

}