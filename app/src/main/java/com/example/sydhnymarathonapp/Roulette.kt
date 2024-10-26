package com.example.sydhnymarathonapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Roulette @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val ROULETTE_MAX_SIZE = 8
        const val ROULETTE_MIN_SIZE = 2
    }

    var rouletteSize = 0
        set(value) {
            field = value
            invalidate()
        }

    var emptyMessage = ""
        set(value) {
            field = value
            invalidate()
        }

    var isRotate = false

    private val rectF = RectF()

    private var rouletteDataList = listOf<String>()

    private var rouletteBorderLineColor = Color.BLACK
    private var rouletteBorderLineWidth = 0f

    private var shapeColors = arrayOf<String>()

    private var rouletteTextColor = Color.BLACK
    private var rouletteTextSize = 0f

    // Center point attr
    private var centerPointColor = Color.BLACK
    private var centerPointRadius = 0f
    private var centerPointVisibility = VISIBLE

    // Top marker attr
    private var topMarkerColor = Color.BLACK
    private var topMarkerVisibility = INVISIBLE

    // Paint
    private val strokePaint = Paint()
    private val fillPaint = Paint()
    private val centerPointPaint = Paint()
    private val textPaint = Paint()
    private val topMarkerPaint = Paint()

    private var centerX = 0f
    private var centerY = 0f

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RouletteView,
            defStyleAttr,
            0
        )

        rouletteBorderLineColor = typedArray.getColor(
            R.styleable.RouletteView_backgroundLineColor,
            Color.BLACK
        )

        rouletteBorderLineWidth = typedArray.getDimension(
            R.styleable.RouletteView_backgroundLineWidth,
            Constant.DEFAULT_CIRCLE_BORDER_LINE_WIDTH
        )

        rouletteTextColor = typedArray.getColor(
            R.styleable.RouletteView_rouletteTextColor,
            Color.BLACK
        )

        rouletteSize = typedArray.getInt(
            R.styleable.RouletteView_rouletteSize,
            Constant.DEFAULT_ROULETTE_SIZE
        )

        rouletteTextSize = typedArray.getDimension(
            R.styleable.RouletteView_rouletteTextSize,
            Constant.DEFAULT_TEXT_SIZE
        )

        emptyMessage = typedArray.getString(
            R.styleable.RouletteView_emptyMessage
        ) ?: Constant.DEFAULT_EMPTY_MESSAGE

        centerPointRadius = typedArray.getDimension(
            R.styleable.RouletteView_centerPointRadius,
            Constant.DEFAULT_CENTER_POINTER_RADIUS
        )

        centerPointColor = typedArray.getColor(
            R.styleable.RouletteView_centerPointColor,
            Color.BLACK
        )

        centerPointVisibility = typedArray.getInt(
            R.styleable.RouletteView_centerPointVisibility,
            VISIBLE
        )

        topMarkerColor = typedArray.getColor(
            R.styleable.RouletteView_topMarkerColor,
            Color.BLACK
        )

        topMarkerVisibility = typedArray.getInt(
            R.styleable.RouletteView_topMarkerVisibility,
            INVISIBLE
        )

        typedArray.recycle()

        strokePaint.apply {
            color = rouletteBorderLineColor
            style = Paint.Style.STROKE
            strokeWidth = rouletteBorderLineWidth
            isAntiAlias = true
        }

        fillPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        centerPointPaint.apply {
            color = centerPointColor
            isAntiAlias = true
        }

        textPaint.apply {
            color = rouletteTextColor
            textSize = rouletteTextSize
            textAlign = Paint.Align.CENTER
        }

        topMarkerPaint.apply {
            color = topMarkerColor
            strokeWidth = 10f
            isAntiAlias = true
        }

        shapeColors = resources.getStringArray(R.array.shape_colors)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val shorterLength = min(right, height)

        val rectLeft = (width / 2f) - (shorterLength / 2f) + paddingLeft + Constant.DEFAULT_PADDING
        val rectRight = (width / 2f) + (shorterLength / 2f) - paddingRight - Constant.DEFAULT_PADDING
        val rectTop = (height / 2f) - (shorterLength / 2f) + paddingTop + Constant.DEFAULT_PADDING
        val rectBottom = (height / 2f) + (shorterLength / 2f) - paddingBottom - Constant. DEFAULT_PADDING

        rectF.set(rectLeft, rectTop, rectRight, rectBottom)

        centerX = (rectF.left + rectF.right) / 2f
        centerY = (rectF.top + rectF.bottom) / 2f

        drawRoulette(canvas, rectF)

        if (centerPointVisibility == VISIBLE) {
            canvas.drawCircle(centerX, centerY, centerPointRadius, centerPointPaint)
        }

        if (topMarkerVisibility == VISIBLE) {
            drawTopMarker(canvas, rectF)
        }
    }

    private fun drawRoulette(canvas: Canvas, rectF: RectF) {
        // draw roulette border line
        canvas.drawArc(rectF, 0f, 360f, false, strokePaint)

        if (rouletteSize in 2..8) {
            val sweepAngle = 360f / rouletteSize.toFloat()
            val radius = (rectF.right - rectF.left) / 2 * 0.5

            for (i in 0 until rouletteSize) {
                fillPaint.color = Color.parseColor(shapeColors[i])

                // draw roulette arc
                val startAngle = if (i == 0) 0f else sweepAngle * i
                canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint)

                // draw roulette text
                val medianAngle = (startAngle + sweepAngle / 2f) * Math.PI / 180f
                val x = (centerX + (radius * cos(medianAngle))).toFloat()
                val y = (centerY + (radius * sin(medianAngle))).toFloat() + Constant.DEFAULT_PADDING
                val text = if (i > rouletteDataList.size - 1)  emptyMessage else rouletteDataList[i]

                canvas.drawText(text, x, y, textPaint)
            }
        } else {
            throw IndexOutOfBoundsException("size out of roulette")
        }
    }

    /**
     * 룰렛 상단 마커
     * */
    private fun drawTopMarker(canvas: Canvas, rectF: RectF) {
        val path = Path()
        val y = rectF.top - 30f

        val point1 = PointF(centerX - Constant.DEFAULT_TOP_MARKER_LENGTH, y) // 왼쪽 포인트
        val point2 = PointF(centerX + Constant.DEFAULT_TOP_MARKER_LENGTH, y) // 오른쪽 포인트
        val point3 = PointF(centerX, rectF.top - 30f + Constant.DEFAULT_TOP_MARKER_LENGTH) // 아래쪽 포인트

        path.reset()

        path.moveTo(point1.x, point1.y)
        path.lineTo(point2.x, point2.y)
        path.lineTo(point3.x, point3.y)
        path.lineTo(point1.x, point1.y)

        path.close()

        canvas.drawPath(path, topMarkerPaint)
    }

    /**
     * 룰렛 회전 함수
     * @param toDegrees : 종료 각도(시작 각도 : 0)
     * @param duration : 회전 시간
     * @param rotateListener : 회전 애니메이션 시작, 종료 확인을 위한 리스너 (선택)
     * */
    fun rotateRoulette(toDegrees: Float, duration: Long, rotateListener: RotateListener?) {
        val animListener = object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {
                rotateListener?.onRotateStart()
                isRotate = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                rotateListener?.onRotateEnd(getRouletteRotateResult(toDegrees))
                isRotate = false
            }
        }

        val rotateAnim = RotateAnimation(
            0f, toDegrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = duration
        rotateAnim.fillAfter = true
        rotateAnim.setAnimationListener(animListener)

        startAnimation(rotateAnim)
    }

    /**
     * 룰레 회전 결과 리턴
     * @param toDegrees : 회전 각도
     * @return 회전 결과
     * */
    private fun getRouletteRotateResult(toDegrees: Float): String {
        val moveDegrees = toDegrees % 360
        val resultAngle = if (moveDegrees > 270) 360 - moveDegrees + 270 else 270 - moveDegrees
        for (i in 1..rouletteSize) {
            if (resultAngle < (360 / rouletteSize) * i) {
                if (i - 1 >= rouletteDataList.size) {
                    return emptyMessage
                }

                return rouletteDataList[i - 1]
            }
        }

        return ""
    }

    /**
     * getter & setter
     * */
    fun setRouletteDataList(rouletteDataList: List<String>) {
        this.rouletteDataList = rouletteDataList
        invalidate()
    }

    fun getRouletteDataList(): List<String> = rouletteDataList

    fun setRouletteTextSize(textSize: Float) {
        rouletteTextSize = textSize
        invalidate()
    }

    fun getRouletteTextSize(): Float = rouletteTextSize

    fun setRouletteTextColor(textColor: Int) {
        this.rouletteTextColor = textColor
        invalidate()
    }

    fun getRouletteTextColor(): Int = rouletteTextColor

    fun setRouletteBorderLineColor(borderLineColor: Int) {
        rouletteBorderLineColor = borderLineColor
        invalidate()
    }

    fun getRouletteBorderLineColor(): Int = rouletteBorderLineColor

    fun setRouletteBorderLineWidth(width: Float) {
        rouletteBorderLineWidth = width
        invalidate()
    }

    fun getRouletteBorderLineWidth(): Float = rouletteBorderLineWidth

    fun setCenterPointColor(centerPointColor: Int) {
        this.centerPointColor = centerPointColor
        invalidate()
    }

    fun getCenterPointColor(): Int = centerPointColor

    fun setCenterPointVisibility(visibility: Int) {
        centerPointVisibility = visibility
        invalidate()
    }

    fun getCenterPointVisibility(): Int = centerPointVisibility

    fun setTopMarkerColor(topMarkerColor: Int) {
        this.topMarkerColor = topMarkerColor
        invalidate()
    }

    fun getTopMarkerColor(): Int = topMarkerColor

    fun setTopMarkerVisibility(visibility: Int) {
        topMarkerVisibility = visibility
        invalidate()
    }

    fun getTopMarkerVisibility(): Int = topMarkerVisibility
}