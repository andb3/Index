package com.andb.apps.todo.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.andb.apps.todo.R
import com.andb.apps.todo.Utilities
import com.andb.apps.todo.lists.TagList
import com.andb.apps.todo.objects.Tasks
import com.google.android.material.chip.Chip
import com.jaredrummler.cyanea.Cyanea
import kotlinx.android.synthetic.main.inbox_list_item.view.*

import kotlinx.android.synthetic.main.view_title_tags.view.*
import java.util.*

class ItemViewTitleTags : ConstraintLayout{
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        inflate(context, R.layout.view_title_tags, this)
    }
    fun setTitle (name: String){
        taskName2.text = name
    }
    fun setOverflow(){

    }

    fun setTags(task: Tasks) {

        val c1: Chip = chip1 as Chip
        val c2: Chip = chip2 as Chip
        val c3: Chip = chip3 as Chip


        if (task.isListTags) {
            Log.d("tags", "multipleTags")


            val tagsList = ArrayList(Arrays.asList<Chip>(c3, c2, c1))

            for (i in tagsList.indices) {

                if (i < task.allListTags.size) {
                    val tagtemp = TagList.getItem(task.getListTags(i))
                    val chiptemp = tagsList[i]

                    chiptemp.text = tagtemp.tagName
                    val drawable = chiptemp.chipIcon!!.mutate()
                    drawable.setColorFilter(tagtemp.tagColor, PorterDuff.Mode.SRC_ATOP)
                    chiptemp.chipIcon = drawable
                    chiptemp.chipBackgroundColor = ColorStateList.valueOf(Utilities.lighterDarker(Cyanea.instance.backgroundColor, 1.2f))
                    chiptemp.visibility = View.VISIBLE

                } else {
                    tagsList[i].visibility = View.INVISIBLE
                }

            }




        } else {
            c1.visibility = View.GONE
            c2.visibility = View.GONE
            c3.visibility = View.GONE
        }

    }
}