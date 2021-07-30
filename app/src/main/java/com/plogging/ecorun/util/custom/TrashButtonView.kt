package com.plogging.ecorun.util.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.plogging.ecorun.R
import com.plogging.ecorun.databinding.CustomTrashButtonBinding

class TrashButtonView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        getAttrs(attrs)

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        getAttrs(attrs, defStyleAttr)
    }

    private lateinit var trashNameView: TextView
    private lateinit var iconImageView: ImageView
    private lateinit var minusBtn: ImageView
    private lateinit var plusBtn: ImageView
    private lateinit var trashCountView: TextView

    private fun init(context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_trash_button, this, false)
        addView(view)
        val binding = CustomTrashButtonBinding.bind(view)

        trashNameView = binding.tvTrash
        iconImageView = binding.ivTrash
        minusBtn = binding.ivTrashMinus
        plusBtn = binding.ivTrashPlus
        trashCountView = binding.tvTrashNum

        clickListener()
    }

    private fun clickListener() {
        minusBtn.setOnClickListener {
            if (trashCountView.text.toString().toInt() == 0) return@setOnClickListener
            trashCountView.text = (trashCountView.text.toString().toInt() - 1).toString()
        }
        plusBtn.setOnClickListener {
            if (trashCountView.text.toString().toInt() == 9999) return@setOnClickListener
            trashCountView.text = (trashCountView.text.toString().toInt() + 1).toString()
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attrs: AttributeSet?) {
        //아까 만들어뒀던 속성 attrs 를 참조함
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrashButton)
        setTypeArray(typedArray)
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrashButton, defStyle, 0)
        setTypeArray(typedArray)
    }

    fun getTrashCount() = trashCountView.text.toString().toInt()

    //디폴트 설정
    private fun setTypeArray(typedArray: TypedArray) {
        //이미지의 아이콘 지정
        val trashIconId = typedArray.getResourceId(
            R.styleable.TrashButton_trashIcon,
            R.drawable.ic_vinyl
        )
        iconImageView.setImageResource(trashIconId)

        val trashTypeTextId = typedArray.getText(R.styleable.TrashButton_trashType)
        trashNameView.text = trashTypeTextId

        typedArray.recycle()
    }
}