package com.example.brainbridge.CourseDetail

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

class CustomBulletSpan(
    private val bulletRadius: Int,
    private val gapWidth: Int,
    private val bulletColor: Int
) : LeadingMarginSpan {

    override fun getLeadingMargin(first: Boolean): Int {
        return gapWidth + bulletRadius * 2
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout?
    ) {
        val oldColor = paint.color
        val oldStyle = paint.style

        // Рисуем точку
        paint.color = bulletColor
        paint.style = Paint.Style.FILL
        val yPosition = (top + bottom) / 2f
        canvas.drawCircle(x + dir * bulletRadius.toFloat(), yPosition, bulletRadius.toFloat(), paint)

        paint.color = oldColor
        paint.style = oldStyle
    }
}