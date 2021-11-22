/**
    Copyright 2020 ZynkSoftware SRL

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
    associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute,
    sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or
    substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.zynksoftware.documentscanner.common.utils

import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import kotlin.math.abs

internal class ImageDetectionProperties(
    private val previewWidth: Double, private val previewHeight: Double,
    private val topLeftPoint: Point, private val bottomLeftPoint: Point,
    private val bottomRightPoint: Point, private val topRightPoint: Point,
    private val resultWidth: Int, private val resultHeight: Int
) {

    companion object {
        private const val SMALLEST_ANGLE_COS = 0.172 //80 degrees
    }

    fun isNotValidImage(approx: MatOfPoint2f): Boolean {
        return isEdgeTouching || isAngleNotCorrect(approx) || isDetectedAreaBelowLimits()
    }

    private fun isAngleNotCorrect(approx: MatOfPoint2f): Boolean {
        return getMaxCosine(approx) || isLeftEdgeDistorted || isRightEdgeDistorted
    }

    private val isRightEdgeDistorted: Boolean
        get() = abs(topRightPoint.y - bottomRightPoint.y) > 100

    private val isLeftEdgeDistorted: Boolean
        get() = abs(topLeftPoint.y - bottomLeftPoint.y) > 100

    private fun getMaxCosine(approx: MatOfPoint2f): Boolean {
        var maxCosine = 0.0
        val approxPoints = approx.toArray()
        maxCosine = MathUtils.getMaxCosine(maxCosine, approxPoints)
        return maxCosine >= SMALLEST_ANGLE_COS
    }

    private val isEdgeTouching: Boolean
        get() = isTopEdgeTouching || isBottomEdgeTouching || isLeftEdgeTouching || isRightEdgeTouching

    private val isBottomEdgeTouching: Boolean
//        get() = bottomLeftPoint.x >= previewHeight - (previewHeight * RESTRICT)  || bottomRightPoint.x >= previewHeight - (previewHeight * RESTRICT)
        get() = bottomLeftPoint.x >= previewHeight - 10 || bottomRightPoint.x >= previewHeight - 10

    private val isTopEdgeTouching: Boolean
//        get() = topLeftPoint.x <= (previewHeight * RESTRICT) || topRightPoint.x <= (previewHeight * RESTRICT)
        get() = topLeftPoint.x <= 10 || topRightPoint.x <= 10

    private val isRightEdgeTouching: Boolean
//        get() = topRightPoint.y >= previewWidth - (previewHeight * RESTRICT) || bottomRightPoint.y >= previewWidth - (previewHeight * RESTRICT)
        get() = topRightPoint.y >= previewWidth - 10 || bottomRightPoint.y >= previewWidth - 10

    private val isLeftEdgeTouching: Boolean
//        get() = topLeftPoint.y <= (previewHeight * RESTRICT) || bottomLeftPoint.y <= (previewHeight * RESTRICT)
        get() = topLeftPoint.y <= 10 || bottomLeftPoint.y <= 10

    private fun isDetectedAreaBelowLimits(): Boolean {
        //        val scanAreaHorizontal = previewWidth / previewHeight >= 1
//        val documentHorizontal = resultWidth.toDouble() / resultHeight.toDouble() >= 0.9
//        val highestPointOverPreview = resultHeight.toDouble() >= 0.70 * previewHeight
//        val horizontal = scanAreaHorizontal && documentHorizontal && highestPointOverPreview
//        Log.d("ImageDetectionProperties","scanAreaHorizontal = $vertical")

//        val scanAreaVertical = previewHeight / previewWidth >= 1
//        val documentVertical = resultHeight.toDouble() / resultWidth.toDouble() >= 0.9
//        val widthestPointOverPreview = resultWidth.toDouble() >= 0.70 * previewWidth
//        val vertical = scanAreaVertical && documentVertical && widthestPointOverPreview
//        Log.d("ImageDetectionProperties","scanAreaVertical = $scanAreaVertical, documentVertical = $documentVertical, widthestPointOverPreview = $widthestPointOverPreview")

//        Log.d("ImageDetectionProperties","horizontal = $horizontal, vertical = $vertical")
        return !(previewWidth / previewHeight >= 1 &&
                resultWidth.toDouble() / resultHeight.toDouble() >= 0.9 &&
                resultHeight.toDouble() >= 0.70 * previewHeight ||
                previewHeight / previewWidth >= 1 &&
                resultHeight.toDouble() / resultWidth.toDouble() >= 0.9 &&
                resultWidth.toDouble() >= 0.70 * previewWidth)
    }
}