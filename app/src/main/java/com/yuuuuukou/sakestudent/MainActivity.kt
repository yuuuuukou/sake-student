package com.yuuuuukou.sakestudent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val sharedPreferenceKey = "com.yuuuuukou.sakestudent.tasting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // イベントリスナの初期設定
        initializeView()

        // 保存済みデータの初期設定
        initializeData()
    }

    /**
     * クリアボタン用OnLongClickイベンドのリスナ
     * - 長押し削除の旨を表示
     */
    private inner class ClearButtonOnClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            Toast.makeText(applicationContext, R.string.btn_clear_toast, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * クリアボタン用OnLongClickイベンドのリスナ
     * - 入力内容を破棄する
     * - ダイアログ出したいけど一旦長押しで事故回避
     */
    private inner class ClearButtonOnLongClickListener : View.OnLongClickListener {
        override fun onLongClick(p0: View?): Boolean {
            clearDisplayEdit()
            return true
        }
    }

    /**
     * テキスト出力ボタン用OnClickイベンドのリスナ
     * - クリップボードに出力する
     */
    private inner class ExportButtonOnClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            exportTastingComments()
        }
    }

    /**
     * FocusChangeイベンドのリスナ
     * - フォーカスが外れたタイミングでキーボードをしまう
     */
    private inner class FocusChangeListener : View.OnFocusChangeListener {
        override fun onFocusChange(view: View, hasFocus: Boolean) {
            if (!hasFocus) {
                // キーボードを非表示にする
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    /**
     * CheckedChangeイベンドのリスナ
     * - チェック状態が変わった時点でSharedPreferenceを更新する
     */
    private inner class CheckedChangeListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(view: CompoundButton, isChecked: Boolean) {
            val sharedPref: SharedPreferences =
                getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(applicationContext.resources.getResourceEntryName(view.id), isChecked)
            editor.apply()
        }
    }

    /**
     * 銘柄欄用のTextWatcher
     * (TextWatcherで呼び出し元ViewのIDを取れない？のでViewごとに用意)
     */
    private inner class TextWatcherForSakeName : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updateSharedPreferencesText(
                applicationContext.resources.getResourceEntryName(R.id.etSakeName),
                editable.toString()
            )
        }
    }

    /**
     * 特徴欄用のTextWatcher
     * (TextWatcherで呼び出し元ViewのIDを取れない？のでViewごとに用意)
     */
    private inner class TextWatcherForFeature : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updateSharedPreferencesText(
                applicationContext.resources.getResourceEntryName(R.id.etFeature),
                editable.toString()
            )
        }
    }

    /**
     * 感想他欄用のTextWatcher
     * (TextWatcherで呼び出し元ViewのIDを取れない？のでViewごとに用意)
     */
    private inner class TextWatcherForNote : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updateSharedPreferencesText(
                applicationContext.resources.getResourceEntryName(R.id.etNote),
                editable.toString()
            )
        }
    }

    /**
     * SharedPreferenceへの文字列データの保存処理
     */
    private fun updateSharedPreferencesText(key: String, value: String) {
        // 入力データの保存
        val sharedPref: SharedPreferences =
            getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString(key, value)
        editor.apply()
    }

    /**
     * テキスト出力処理
     */
    private fun exportTastingComments() {
        val sharedPref: SharedPreferences =
            getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
        var tmpList = mutableListOf<String>()

        val s = StringBuilder()

        // 銘柄名称
        tmpList = addList(sharedPref, tmpList, R.id.etSakeName, R.id.tvSakeName)
        s.append(tmpList.joinToString("、"))
        tmpList = mutableListOf()

        // 外観
        s.append("\n${getString(R.string.tv_appearance)}")
        s.append("\n\t${getString(R.string.tv_transparency)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbTransparency1, R.string.cb_transparency1)
        tmpList = addList(sharedPref, tmpList, R.id.cbTransparency2, R.string.cb_transparency2)
        tmpList = addList(sharedPref, tmpList, R.id.cbTransparency3, R.string.cb_transparency3)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_shade)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbShade1, R.string.cb_shade1)
        tmpList = addList(sharedPref, tmpList, R.id.cbShade2, R.string.cb_shade2)
        tmpList = addList(sharedPref, tmpList, R.id.cbShade3, R.string.cb_shade3)
        tmpList = addList(sharedPref, tmpList, R.id.cbShade4, R.string.cb_shade4)
        tmpList = addList(sharedPref, tmpList, R.id.cbShade5, R.string.cb_shade5)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_color_tone)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone1, R.string.cb_color_tone1)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone2, R.string.cb_color_tone2)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone3, R.string.cb_color_tone3)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone4, R.string.cb_color_tone4)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone5, R.string.cb_color_tone5)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone6, R.string.cb_color_tone6)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone7, R.string.cb_color_tone7)
        tmpList = addList(sharedPref, tmpList, R.id.cbColorTone8, R.string.cb_color_tone8)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        // 香り
        s.append("\n${getString(R.string.tv_fragrance)}")

        s.append("\n\t${getString(R.string.tv_first_impression_fragrance)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance1, R.string.cb_first_impression_fragrance1)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance2, R.string.cb_first_impression_fragrance2)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance3, R.string.cb_first_impression_fragrance3)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance4, R.string.cb_first_impression_fragrance4)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance5, R.string.cb_first_impression_fragrance5)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance6, R.string.cb_first_impression_fragrance6)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFragrance7, R.string.cb_first_impression_fragrance7)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_feature)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature1, R.string.cb_feature1)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature2, R.string.cb_feature2)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature3, R.string.cb_feature3)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature4, R.string.cb_feature4)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature5, R.string.cb_feature5)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature6, R.string.cb_feature6)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature7, R.string.cb_feature7)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature8, R.string.cb_feature8)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature9, R.string.cb_feature9)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature10, R.string.cb_feature10)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature11, R.string.cb_feature11)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature12, R.string.cb_feature12)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature13, R.string.cb_feature13)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature14, R.string.cb_feature14)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature15, R.string.cb_feature15)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature16, R.string.cb_feature16)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature17, R.string.cb_feature17)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature18, R.string.cb_feature18)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature19, R.string.cb_feature19)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature20, R.string.cb_feature20)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature21, R.string.cb_feature21)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature22, R.string.cb_feature22)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature23, R.string.cb_feature23)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature24, R.string.cb_feature24)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature25, R.string.cb_feature25)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature26, R.string.cb_feature26)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature27, R.string.cb_feature27)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature28, R.string.cb_feature28)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature29, R.string.cb_feature29)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature30, R.string.cb_feature30)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature31, R.string.cb_feature31)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature32, R.string.cb_feature32)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature33, R.string.cb_feature33)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature34, R.string.cb_feature34)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature35, R.string.cb_feature35)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature36, R.string.cb_feature36)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature37, R.string.cb_feature37)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature38, R.string.cb_feature38)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature39, R.string.cb_feature39)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature40, R.string.cb_feature40)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature41, R.string.cb_feature41)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature42, R.string.cb_feature42)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature43, R.string.cb_feature43)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature44, R.string.cb_feature44)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature45, R.string.cb_feature45)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature46, R.string.cb_feature46)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature47, R.string.cb_feature47)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature48, R.string.cb_feature48)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature49, R.string.cb_feature49)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature50, R.string.cb_feature50)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature51, R.string.cb_feature51)
        tmpList = addList(sharedPref, tmpList, R.id.cbFeature52, R.string.cb_feature52)
        tmpList = addList(sharedPref, tmpList, R.id.etFeature, R.string.tv_feature)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        // 味わい
        s.append("\n${getString(R.string.tv_flavor)}")

        s.append("\n\t${getString(R.string.tv_first_impression_flavor)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFlavor1, R.string.cb_first_impression_flavor1)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFlavor2, R.string.cb_first_impression_flavor2)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFlavor3, R.string.cb_first_impression_flavor3)
        tmpList = addList(sharedPref, tmpList, R.id.cbFirstImpressionFlavor4, R.string.cb_first_impression_flavor4)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_bubble_size)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleSize1, R.string.cb_bubble_size1)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleSize2, R.string.cb_bubble_size2)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleSize3, R.string.cb_bubble_size3)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleSize4, R.string.cb_bubble_size4)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleSize5, R.string.cb_bubble_size5)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_bubble_volume)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume1, R.string.cb_bubble_volume1)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume2, R.string.cb_bubble_volume2)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume3, R.string.cb_bubble_volume3)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume4, R.string.cb_bubble_volume4)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume5, R.string.cb_bubble_volume5)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume6, R.string.cb_bubble_volume6)
        tmpList = addList(sharedPref, tmpList, R.id.cbBubbleVolume7, R.string.cb_bubble_volume7)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_taste)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste1, R.string.cb_taste1)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste2, R.string.cb_taste2)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste3, R.string.cb_taste3)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste4, R.string.cb_taste4)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste5, R.string.cb_taste5)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste6, R.string.cb_taste6)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste7, R.string.cb_taste7)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste8, R.string.cb_taste8)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste9, R.string.cb_taste9)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste10, R.string.cb_taste10)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste11, R.string.cb_taste11)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste12, R.string.cb_taste12)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste13, R.string.cb_taste13)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste14, R.string.cb_taste14)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste15, R.string.cb_taste15)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste16, R.string.cb_taste16)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste17, R.string.cb_taste17)
        tmpList = addList(sharedPref, tmpList, R.id.cbTaste18, R.string.cb_taste18)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_expanse)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse1, R.string.cb_taste1)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse2, R.string.cb_taste2)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse3, R.string.cb_taste3)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse4, R.string.cb_taste4)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse5, R.string.cb_taste5)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse6, R.string.cb_taste6)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse7, R.string.cb_taste7)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse8, R.string.cb_taste8)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse9, R.string.cb_taste9)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse10, R.string.cb_taste10)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse11, R.string.cb_taste11)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse12, R.string.cb_taste12)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse13, R.string.cb_taste13)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse14, R.string.cb_taste14)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse15, R.string.cb_taste15)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse16, R.string.cb_taste16)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse17, R.string.cb_taste17)
        tmpList = addList(sharedPref, tmpList, R.id.cbExpanse18, R.string.cb_taste18)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_lingering_taste)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste1, R.string.cb_taste1)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste2, R.string.cb_taste2)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste3, R.string.cb_taste3)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste4, R.string.cb_taste4)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste5, R.string.cb_taste5)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste6, R.string.cb_taste6)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste7, R.string.cb_taste7)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste8, R.string.cb_taste8)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste9, R.string.cb_taste9)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste10, R.string.cb_taste10)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste11, R.string.cb_taste11)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste12, R.string.cb_taste12)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste13, R.string.cb_taste13)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste14, R.string.cb_taste14)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste15, R.string.cb_taste15)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste16, R.string.cb_taste16)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste17, R.string.cb_taste17)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTaste18, R.string.cb_taste18)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_lingering_taste_length)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTasteLength1, R.string.cb_lingering_taste_length1)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTasteLength2, R.string.cb_lingering_taste_length2)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTasteLength3, R.string.cb_lingering_taste_length3)
        tmpList = addList(sharedPref, tmpList, R.id.cbLingeringTasteLength4, R.string.cb_lingering_taste_length4)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_balance)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance1, R.string.cb_balance1)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance2, R.string.cb_balance2)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance3, R.string.cb_balance3)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance4, R.string.cb_balance4)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance5, R.string.cb_balance5)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance6, R.string.cb_balance6)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance7, R.string.cb_balance7)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance8, R.string.cb_balance8)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance9, R.string.cb_balance9)
        tmpList = addList(sharedPref, tmpList, R.id.cbBalance10, R.string.cb_balance10)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_intensity)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbIntensity1, R.string.cb_intensity1)
        tmpList = addList(sharedPref, tmpList, R.id.cbIntensity2, R.string.cb_intensity2)
        tmpList = addList(sharedPref, tmpList, R.id.cbIntensity3, R.string.cb_intensity3)
        tmpList = addList(sharedPref, tmpList, R.id.cbIntensity4, R.string.cb_intensity4)
        tmpList = addList(sharedPref, tmpList, R.id.cbIntensity5, R.string.cb_intensity5)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        s.append("\n\t${getString(R.string.tv_sweetness)}")
        tmpList = addList(sharedPref, tmpList, R.id.cbSweetness1, R.string.cb_sweetness1)
        tmpList = addList(sharedPref, tmpList, R.id.cbSweetness2, R.string.cb_sweetness2)
        tmpList = addList(sharedPref, tmpList, R.id.cbSweetness3, R.string.cb_sweetness3)
        tmpList = addList(sharedPref, tmpList, R.id.cbSweetness4, R.string.cb_sweetness4)
        tmpList = addList(sharedPref, tmpList, R.id.cbSweetness5, R.string.cb_sweetness5)
        s.append("\n\t\t${tmpList.joinToString("、")}")
        tmpList = mutableListOf()

        // 感想他
        s.append("\n${getString(R.string.tv_note)}")
        tmpList = addList(sharedPref, tmpList, R.id.etNote, R.string.tv_note)
        s.append("\n\t${tmpList.joinToString("、")}")

        // クリップボードへコピー
        copyToClipboard(applicationContext, "", s.toString())
        Toast.makeText(applicationContext, R.string.btn_export_toast, Toast.LENGTH_SHORT).show()
    }

    /**
     * リスト追加処理
     * - SharedPreferenceを一度リストに突っ込んでからjoinToStringで整形して出力する
     */
    private fun addList(
        sharedPref: SharedPreferences,
        tmpList: MutableList<String>,
        resourceId: Int,
        stringId: Int
    ): MutableList<String> {

        val sharedPrefKey = applicationContext.resources.getResourceEntryName(resourceId)

        var tmpString: String? = null
        if (sharedPrefKey.startsWith("cb")) {
            // チェックボックス
            val isChecked = sharedPref.getBoolean(
                applicationContext.resources.getResourceEntryName(resourceId),
                false
            )
            if (isChecked) {
                // チェック有りならテキストを設定する
                tmpString = getString(stringId)
            }
        } else {
            // チェックボックス以外
            tmpString = sharedPref.getString(
                applicationContext.resources.getResourceEntryName(resourceId),
                null
            )
            tmpString = tmpString?.replace("\n", "\n\t")
        }

        if (tmpString != null) {
            tmpList.add(tmpString)
        }
        return tmpList
    }

    /**
     * クリップボードへコピー
     * ref: https://qiita.com/CUTBOSS/items/97669c712449510fe7f0
     */
    private fun copyToClipboard(context: Context, label: String, text: String): Boolean {
        try {
            val clipboardManager: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Viewの初期化
     * - イベントリスナの設定
     */
    private fun initializeView() {
        findViewById<Button>(R.id.btnClear).setOnClickListener(ClearButtonOnClickListener())
        findViewById<Button>(R.id.btnClear).setOnLongClickListener(ClearButtonOnLongClickListener())
        findViewById<Button>(R.id.btnExport).setOnClickListener(ExportButtonOnClickListener())

        findViewById<EditText>(R.id.etSakeName).addTextChangedListener(TextWatcherForSakeName())
        findViewById<EditText>(R.id.etSakeName).onFocusChangeListener = FocusChangeListener()

        findViewById<EditText>(R.id.etFeature).addTextChangedListener(TextWatcherForFeature())
        findViewById<EditText>(R.id.etSakeName).onFocusChangeListener = FocusChangeListener()

        findViewById<EditText>(R.id.etNote).addTextChangedListener(TextWatcherForNote())
        findViewById<EditText>(R.id.etSakeName).onFocusChangeListener = FocusChangeListener()

        findViewById<CheckBox>(R.id.cbTransparency1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbTransparency2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbTransparency3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbShade1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbShade2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbShade3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbShade4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbShade5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone6).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone7).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbColorTone8).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance4).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance5).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance6).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance7).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFeature1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature6).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature7).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature8).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature9).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature10).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature11).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature12).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature13).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature14).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature15).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature16).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature17).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature18).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature19).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature20).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature21).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature22).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature23).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature24).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature25).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature26).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature27).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature28).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature29).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature30).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature31).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature32).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature33).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature34).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature35).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature36).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature37).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature38).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature39).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature40).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature41).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature42).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature43).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature44).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature45).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature46).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature47).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature48).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature49).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature50).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature51).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFeature52).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor4).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleSize1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBubbleSize2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBubbleSize3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBubbleSize4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBubbleSize5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBubbleVolume1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume4).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume5).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume6).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume7).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbTaste1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste6).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste7).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste8).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste9).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste10).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste11).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste12).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste13).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste14).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste15).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste16).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste17).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbTaste18).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse6).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse7).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse8).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse9).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse10).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse11).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse12).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse13).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse14).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse15).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse16).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse17).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbExpanse18).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbLingeringTaste1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste4).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste5).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste6).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste7).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste8).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste9).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste10).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste11).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste12).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste13).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste14).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste15).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste16).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste17).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste18).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength1).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength2).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength3).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength4).setOnCheckedChangeListener(
            CheckedChangeListener()
        )
        findViewById<CheckBox>(R.id.cbBalance1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance6).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance7).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance8).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance9).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbBalance10).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbIntensity1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbIntensity2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbIntensity3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbIntensity4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbIntensity5).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbSweetness1).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbSweetness2).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbSweetness3).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbSweetness4).setOnCheckedChangeListener(CheckedChangeListener())
        findViewById<CheckBox>(R.id.cbSweetness5).setOnCheckedChangeListener(CheckedChangeListener())
    }

    /**
     * データの初期化
     * - データが保存されていればそれを設定する
     */
    private fun initializeData() {
        val sharedPref: SharedPreferences =
            getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)

        findViewById<EditText>(R.id.etSakeName).setText(
            sharedPref.getString(
                applicationContext.resources.getResourceEntryName(
                    R.id.etSakeName
                ), null
            )
        )
        findViewById<EditText>(R.id.etFeature).setText(
            sharedPref.getString(
                applicationContext.resources.getResourceEntryName(
                    R.id.etFeature
                ), null
            )
        )
        findViewById<EditText>(R.id.etNote).setText(
            sharedPref.getString(
                applicationContext.resources.getResourceEntryName(
                    R.id.etNote
                ), null
            )
        )

        findViewById<CheckBox>(R.id.cbTransparency1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTransparency1),
            false
        )
        findViewById<CheckBox>(R.id.cbTransparency2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTransparency2),
            false
        )
        findViewById<CheckBox>(R.id.cbTransparency3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTransparency3),
            false
        )
        findViewById<CheckBox>(R.id.cbShade1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbShade1),
            false
        )
        findViewById<CheckBox>(R.id.cbShade2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbShade2),
            false
        )
        findViewById<CheckBox>(R.id.cbShade3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbShade3),
            false
        )
        findViewById<CheckBox>(R.id.cbShade4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbShade4),
            false
        )
        findViewById<CheckBox>(R.id.cbShade5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbShade5),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone1),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone2),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone3),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone4),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone5),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone6),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone7),
            false
        )
        findViewById<CheckBox>(R.id.cbColorTone8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbColorTone8),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance1),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance2),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance3),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance4),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance5),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance6),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFragrance7),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature1),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature2),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature3),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature4),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature5),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature6),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature7),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature8),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature9).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature9),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature10).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature10),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature11).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature11),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature12).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature12),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature13).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature13),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature14).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature14),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature15).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature15),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature16).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature16),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature17).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature17),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature18).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature18),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature19).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature19),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature20).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature20),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature21).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature21),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature22).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature22),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature23).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature23),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature24).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature24),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature25).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature25),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature26).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature26),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature27).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature27),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature28).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature28),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature29).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature29),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature30).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature30),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature31).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature31),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature32).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature32),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature33).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature33),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature34).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature34),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature35).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature35),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature36).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature36),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature37).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature37),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature38).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature38),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature39).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature39),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature40).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature40),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature41).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature41),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature42).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature42),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature43).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature43),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature44).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature44),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature45).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature45),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature46).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature46),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature47).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature47),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature48).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature48),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature49).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature49),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature50).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature50),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature51).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature51),
            false
        )
        findViewById<CheckBox>(R.id.cbFeature52).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFeature52),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFlavor1),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFlavor2),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFlavor3),
            false
        )
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbFirstImpressionFlavor4),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleSize1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleSize1),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleSize2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleSize2),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleSize3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleSize3),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleSize4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleSize4),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleSize5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleSize5),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume1),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume2),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume3),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume4),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume5),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume6),
            false
        )
        findViewById<CheckBox>(R.id.cbBubbleVolume7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBubbleVolume7),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste1),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste2),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste3),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste4),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste5),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste6),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste7),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste8),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste9).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste9),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste10).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste10),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste11).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste11),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste12).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste12),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste13).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste13),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste14).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste14),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste15).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste15),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste16).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste16),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste17).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste17),
            false
        )
        findViewById<CheckBox>(R.id.cbTaste18).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbTaste18),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse1),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse2),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse3),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse4),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse5),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse6),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse7),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse8),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse9).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse9),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse10).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse10),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse11).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse11),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse12).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse12),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse13).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse13),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse14).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse14),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse15).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse15),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse16).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse16),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse17).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse17),
            false
        )
        findViewById<CheckBox>(R.id.cbExpanse18).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbExpanse18),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste1),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste2),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste3),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste4),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste5),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste6),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste7),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste8),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste9).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste9),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste10).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste10),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste11).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste11),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste12).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste12),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste13).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste13),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste14).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste14),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste15).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste15),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste16).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste16),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste17).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste17),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTaste18).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTaste18),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTasteLength1),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTasteLength2),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTasteLength3),
            false
        )
        findViewById<CheckBox>(R.id.cbLingeringTasteLength4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbLingeringTasteLength4),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance1),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance2),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance3),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance4),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance5),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance6).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance6),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance7).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance7),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance8).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance8),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance9).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance9),
            false
        )
        findViewById<CheckBox>(R.id.cbBalance10).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbBalance10),
            false
        )
        findViewById<CheckBox>(R.id.cbIntensity1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbIntensity1),
            false
        )
        findViewById<CheckBox>(R.id.cbIntensity2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbIntensity2),
            false
        )
        findViewById<CheckBox>(R.id.cbIntensity3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbIntensity3),
            false
        )
        findViewById<CheckBox>(R.id.cbIntensity4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbIntensity4),
            false
        )
        findViewById<CheckBox>(R.id.cbIntensity5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbIntensity5),
            false
        )
        findViewById<CheckBox>(R.id.cbSweetness1).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbSweetness1),
            false
        )
        findViewById<CheckBox>(R.id.cbSweetness2).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbSweetness2),
            false
        )
        findViewById<CheckBox>(R.id.cbSweetness3).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbSweetness3),
            false
        )
        findViewById<CheckBox>(R.id.cbSweetness4).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbSweetness4),
            false
        )
        findViewById<CheckBox>(R.id.cbSweetness5).isChecked = sharedPref.getBoolean(
            applicationContext.resources.getResourceEntryName(R.id.cbSweetness5),
            false
        )
    }

    /**
     * 画面の編集内容を破棄する
     */
    private fun clearDisplayEdit() {
        findViewById<EditText>(R.id.etSakeName).text = null
        findViewById<EditText>(R.id.etFeature).text = null
        findViewById<EditText>(R.id.etNote).text = null

        findViewById<CheckBox>(R.id.cbTransparency1).isChecked = false
        findViewById<CheckBox>(R.id.cbTransparency2).isChecked = false
        findViewById<CheckBox>(R.id.cbTransparency3).isChecked = false
        findViewById<CheckBox>(R.id.cbShade1).isChecked = false
        findViewById<CheckBox>(R.id.cbShade2).isChecked = false
        findViewById<CheckBox>(R.id.cbShade3).isChecked = false
        findViewById<CheckBox>(R.id.cbShade4).isChecked = false
        findViewById<CheckBox>(R.id.cbShade5).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone1).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone2).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone3).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone4).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone5).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone6).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone7).isChecked = false
        findViewById<CheckBox>(R.id.cbColorTone8).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance1).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance2).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance3).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance4).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance5).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance6).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFragrance7).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature1).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature2).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature3).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature4).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature5).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature6).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature7).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature8).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature9).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature10).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature11).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature12).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature13).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature14).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature15).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature16).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature17).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature18).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature19).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature20).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature21).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature22).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature23).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature24).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature25).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature26).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature27).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature28).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature29).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature30).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature31).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature32).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature33).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature34).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature35).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature36).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature37).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature38).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature39).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature40).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature41).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature42).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature43).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature44).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature45).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature46).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature47).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature48).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature49).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature50).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature51).isChecked = false
        findViewById<CheckBox>(R.id.cbFeature52).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor1).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor2).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor3).isChecked = false
        findViewById<CheckBox>(R.id.cbFirstImpressionFlavor4).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleSize1).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleSize2).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleSize3).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleSize4).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleSize5).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume1).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume2).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume3).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume4).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume5).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume6).isChecked = false
        findViewById<CheckBox>(R.id.cbBubbleVolume7).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste1).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste2).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste3).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste4).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste5).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste6).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste7).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste8).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste9).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste10).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste11).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste12).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste13).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste14).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste15).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste16).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste17).isChecked = false
        findViewById<CheckBox>(R.id.cbTaste18).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse1).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse2).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse3).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse4).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse5).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse6).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse7).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse8).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse9).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse10).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse11).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse12).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse13).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse14).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse15).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse16).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse17).isChecked = false
        findViewById<CheckBox>(R.id.cbExpanse18).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste1).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste2).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste3).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste4).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste5).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste6).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste7).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste8).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste9).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste10).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste11).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste12).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste13).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste14).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste15).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste16).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste17).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTaste18).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTasteLength1).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTasteLength2).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTasteLength3).isChecked = false
        findViewById<CheckBox>(R.id.cbLingeringTasteLength4).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance1).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance2).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance3).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance4).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance5).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance6).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance7).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance8).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance9).isChecked = false
        findViewById<CheckBox>(R.id.cbBalance10).isChecked = false
        findViewById<CheckBox>(R.id.cbIntensity1).isChecked = false
        findViewById<CheckBox>(R.id.cbIntensity2).isChecked = false
        findViewById<CheckBox>(R.id.cbIntensity3).isChecked = false
        findViewById<CheckBox>(R.id.cbIntensity4).isChecked = false
        findViewById<CheckBox>(R.id.cbIntensity5).isChecked = false
        findViewById<CheckBox>(R.id.cbSweetness1).isChecked = false
        findViewById<CheckBox>(R.id.cbSweetness2).isChecked = false
        findViewById<CheckBox>(R.id.cbSweetness3).isChecked = false
        findViewById<CheckBox>(R.id.cbSweetness4).isChecked = false
        findViewById<CheckBox>(R.id.cbSweetness5).isChecked = false
    }
}

