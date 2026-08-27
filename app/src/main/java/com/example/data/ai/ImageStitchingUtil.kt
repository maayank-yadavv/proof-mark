package com.example.data.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

/**
 * Utility for stitching multi-angle package label photos into a seamless composite panoramic label image.
 * Solves multi-panel package declaration inspection (e.g., cylindrical bottles, boxes, folded wrappers).
 */
object ImageStitchingUtil {

    /**
     * Stitches a list of bitmaps horizontally into a continuous panoramic label image.
     * Optionally draws subtle seam lines and panel badges ("PANEL A", "PANEL B") for transparency.
     */
    fun stitchHorizontally(
        bitmaps: List<Bitmap>,
        addSeamDividers: Boolean = true
    ): Bitmap? {
        if (bitmaps.isEmpty()) return null
        if (bitmaps.size == 1) return bitmaps.first()

        // Normalize height to minimum or target standard height (e.g., 800px)
        val targetHeight = bitmaps.minOf { it.height }.coerceIn(400, 1200)

        val scaledBitmaps = bitmaps.map { bmp ->
            if (bmp.height == targetHeight) {
                bmp
            } else {
                val scale = targetHeight.toFloat() / bmp.height
                val targetWidth = (bmp.width * scale).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(bmp, targetWidth, targetHeight, true)
            }
        }

        val dividerWidth = if (addSeamDividers) 6 else 0
        val totalWidth = scaledBitmaps.sumOf { it.width } + (dividerWidth * (scaledBitmaps.size - 1))

        val resultBitmap = Bitmap.createBitmap(totalWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        canvas.drawColor(Color.parseColor("#121212")) // Dark background for gaps

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4285F4") // Metric primary blue seam indicator
            strokeWidth = dividerWidth.toFloat()
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (targetHeight * 0.035f).coerceIn(16f, 32f)
            isFakeBoldText = true
        }

        val textBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CC000000") // Semi-transparent black overlay
        }

        var currentX = 0f

        scaledBitmaps.forEachIndexed { index, bmp ->
            // Draw panel
            canvas.drawBitmap(bmp, currentX, 0f, paint)

            // Draw panel tag badge ("PANEL 1", "PANEL 2")
            val panelTag = "PANEL ${('A' + index)}"
            val textBounds = Rect()
            textPaint.getTextBounds(panelTag, 0, panelTag.length, textBounds)
            val padding = 12f
            val badgeWidth = textBounds.width() + (padding * 2)
            val badgeHeight = textBounds.height() + (padding * 2)

            canvas.drawRect(
                currentX + 16f,
                16f,
                currentX + 16f + badgeWidth,
                16f + badgeHeight,
                textBgPaint
            )
            canvas.drawText(
                panelTag,
                currentX + 16f + padding,
                16f + padding + textBounds.height(),
                textPaint
            )

            currentX += bmp.width

            // Draw divider seam line if not last
            if (addSeamDividers && index < scaledBitmaps.size - 1) {
                canvas.drawRect(
                    currentX,
                    0f,
                    currentX + dividerWidth,
                    targetHeight.toFloat(),
                    dividerPaint
                )
                currentX += dividerWidth
            }
        }

        return resultBitmap
    }

    /**
     * Stitches a list of bitmaps vertically into a stacked composite label image.
     */
    fun stitchVertically(
        bitmaps: List<Bitmap>,
        addSeamDividers: Boolean = true
    ): Bitmap? {
        if (bitmaps.isEmpty()) return null
        if (bitmaps.size == 1) return bitmaps.first()

        val targetWidth = bitmaps.minOf { it.width }.coerceIn(400, 1200)

        val scaledBitmaps = bitmaps.map { bmp ->
            if (bmp.width == targetWidth) {
                bmp
            } else {
                val scale = targetWidth.toFloat() / bmp.width
                val targetHeight = (bmp.height * scale).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(bmp, targetWidth, targetHeight, true)
            }
        }

        val dividerHeight = if (addSeamDividers) 6 else 0
        val totalHeight = scaledBitmaps.sumOf { it.height } + (dividerHeight * (scaledBitmaps.size - 1))

        val resultBitmap = Bitmap.createBitmap(targetWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        canvas.drawColor(Color.parseColor("#121212"))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4285F4")
        }

        var currentY = 0f

        scaledBitmaps.forEachIndexed { index, bmp ->
            canvas.drawBitmap(bmp, 0f, currentY, paint)
            currentY += bmp.height

            if (addSeamDividers && index < scaledBitmaps.size - 1) {
                canvas.drawRect(
                    0f,
                    currentY,
                    targetWidth.toFloat(),
                    currentY + dividerHeight,
                    dividerPaint
                )
                currentY += dividerHeight
            }
        }

        return resultBitmap
    }
}
