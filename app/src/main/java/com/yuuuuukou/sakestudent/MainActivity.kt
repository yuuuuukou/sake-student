package com.yuuuuukou.sakestudent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    /**
     * クリップボードへコピー
     * ref: https://qiita.com/CUTBOSS/items/97669c712449510fe7f0
     */
    fun copyToClipboard(context: Context, label: String, text: String): Boolean {
        try {
            val clipboardManager: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
            return true
        } catch (e: Exception) {
            return false
        }
    }
}