package com.ytowka.worktimer2.screens.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.ytowka.worktimer2.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditorActivity : AppCompatActivity() {
    val args: EditorActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
    }
    fun exit(){
        finish()
    }
}