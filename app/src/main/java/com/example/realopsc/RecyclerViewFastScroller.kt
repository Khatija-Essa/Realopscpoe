@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.qtalk.recyclerviewfastscroller

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.Keep
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realopsc.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class RecyclerViewFastScroller @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG: String = "RVFastScroller"
        private const val ERROR_MESSAGE_NO_RECYCLER_VIEW =
            "The RecyclerView required for initialization with FastScroller cannot be null"
    }

    enum class FastScrollDirection(val value: Int) {
        HORIZONTAL(1), VERTICAL(0);

        companion object {
            fun getFastScrollDirectionByValue(value: Int = Defaults.fastScrollDirection.value): FastScrollDirection {
                for (fsDirection in values()) {
                    if (fsDirection.value == value) return fsDirection
                }
                return Defaults.fastScrollDirection
            }
        }
    }

    private enum class PopupPosition(val value: Int) {
        BEFORE_TRACK(0), AFTER_TRACK(1);

        companion object {
            fun getPopupPositionByValue(value: Int = Defaults.popupPosition.value): PopupPosition {
                for (popupPosition: PopupPosition in values()) {
                    if (popupPosition.value == value)
                        return popupPosition
                }
                return Defaults.popupPosition
            }
        }
    }

    private object Defaults {
        val popupDrawableInt: Int = R.drawable.custom_bg_primary
        val handleDrawableInt: Int = R.drawable.custom_bg_primary
        val handleSize: Int = R.dimen.default_handle_size
        val textStyle: Int = R.style.FastScrollerTextAppearance
        val popupPosition: PopupPosition = PopupPosition.BEFORE_TRACK
        val fastScrollDirection: FastScrollDirection = FastScrollDirection.VERTICAL
        const val isFixedSizeHandle: Boolean = false
        const val isFastScrollEnabled: Boolean = true
        const val animationDuration: Long = 100
        const val popupVisibilityDuration = 200L
        const val hasEmptyItemDecorator: Boolean = true
        const val handleVisibilityDuration: Int = 0
        const val trackMargin: Int = 0
    }

    var trackDrawable: Drawable?
        set(value) {
            trackView.background = value
        }
        get() = trackView.background

    var popupDrawable: Drawable?
        set(value) {
            popupTextView.background = value
        }
        get() = popupTextView.background

    var handleDrawable: Drawable?
        set(value) {
            handleImageView.setImageDrawable(requireNotNull(value) { "No drawable found for the given ID" })
        }
        get() = handleImageView.drawable

    @StyleRes
    var textStyle: Int = Defaults.textStyle
        set(value) {
            TextViewCompat.setTextAppearance(popupTextView, value)
        }

    private var isFixedSizeHandle: Boolean = Defaults.isFixedSizeHandle

    var isFastScrollEnabled: Boolean = Defaults.isFastScrollEnabled

    lateinit var popupTextView: TextView

    var trackMarginStart: Int = 0
        set(value) {
            field = value
            setTrackMargin()
        }

    var trackMarginEnd: Int = 0
        set(value) {
            field = value
            setTrackMargin()
        }

    var fastScrollDirection: FastScrollDirection = Defaults.fastScrollDirection
        set(value) {
            field = value
            alignTrackAndHandle()
        }

    var handleWidth: Int = LayoutParams.WRAP_CONTENT
        set(value) {
            field = value
            refreshHandleImageViewSize()
        }
    var handleHeight: Int = LayoutParams.WRAP_CONTENT
        set(value) {
            field = value
            refreshHandleImageViewSize()
        }

    var handleVisibilityDuration: Int = -1

    private var popupPosition: PopupPosition = Defaults.popupPosition
    private var hasEmptyItemDecorator: Boolean = Defaults.hasEmptyItemDecorator
    private lateinit var handleImageView: AppCompatImageView
    private lateinit var trackView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private var isEngaged: Boolean = false
    private var handleStateListener: HandleStateListener? = null
    private var previousTotalVisibleItem: Int = 0
    private var hideHandleJob: Job? = null

    private val trackLength: Float
        get() =
            when (fastScrollDirection) {
                FastScrollDirection.VERTICAL ->
                    trackView.height
                FastScrollDirection.HORIZONTAL ->
                    trackView.width
            }.toFloat()

    private val handleLength: Float
        get() =
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL ->
                    handleImageView.width
                FastScrollDirection.VERTICAL ->
                    handleImageView.height
            }.toFloat()

    private val popupLength: Float
        get() =
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL ->
                    popupTextView.width
                FastScrollDirection.VERTICAL ->
                    popupTextView.height
            }.toFloat()

    private val attribs: TypedArray? = if (attrs != null) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.RecyclerViewFastScroller, 0, 0)
    } else {
        null
    }

    private fun alignPopupLayout() {
        val lpPopupLayout =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
                when (popupPosition) {
                    PopupPosition.BEFORE_TRACK -> {
                        when (fastScrollDirection) {
                            FastScrollDirection.HORIZONTAL ->
                                it.addRule(ABOVE, trackView.id)
                            FastScrollDirection.VERTICAL -> {
                                if (Build.VERSION.SDK_INT > 16)
                                    it.addRule(START_OF, trackView.id)
                                else
                                    it.addRule(LEFT_OF, trackView.id)
                            }
                        }
                    }
                    PopupPosition.AFTER_TRACK -> {
                        when (fastScrollDirection) {
                            FastScrollDirection.HORIZONTAL ->
                                it.addRule(BELOW, trackView.id)
                            FastScrollDirection.VERTICAL -> {
                                if (Build.VERSION.SDK_INT > 16)
                                    it.addRule(END_OF, trackView.id)
                                else
                                    it.addRule(RIGHT_OF, trackView.id)
                            }
                        }
                    }
                }
            }
        popupTextView.layoutParams = lpPopupLayout
    }

    private fun alignTrackAndHandle() {
        val padding = resources.getDimensionPixelOffset(R.dimen.default_handle_padding)
        when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> {
                handleImageView.setPadding(0, padding, 0, padding)
                popupTextView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).also { it.addRule(ALIGN_BOTTOM, R.id.trackView) }
                trackView.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).also { it.addRule(ALIGN_PARENT_BOTTOM) }
            }
            FastScrollDirection.VERTICAL -> {
                handleImageView.setPadding(padding, 0, padding, 0)
                popupTextView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).also {
                    if (Build.VERSION.SDK_INT > 16)
                        it.addRule(ALIGN_END, R.id.trackView)
                    else
                        it.addRule(ALIGN_RIGHT, R.id.trackView)
                }
                trackView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
                ).also {
                    if (Build.VERSION.SDK_INT > 16)
                        it.addRule(ALIGN_PARENT_END)
                    else
                        it.addRule(ALIGN_PARENT_RIGHT)
                }
            }
        }
        post {
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL -> {
                    handleImageView.y = 0F
                    popupTextView.y = trackView.y - popupTextView.height
                }
                FastScrollDirection.VERTICAL -> {
                    handleImageView.x = 0F
                    popupTextView.x = trackView.x - popupTextView.width
                }
            }

            onScrollListener.onScrolled(recyclerView, 0, 0)
        }
    }

    private fun setTrackMargin() {
        with(trackView.layoutParams as MarginLayoutParams) {
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL ->
                    if (Build.VERSION.SDK_INT > 16) {
                        marginStart = trackMarginStart
                        marginEnd = trackMarginEnd
                    } else
                        setMargins(trackMarginStart, 0, trackMarginEnd, 0)
                FastScrollDirection.VERTICAL ->
                    setMargins(0, trackMarginStart, 0, trackMarginEnd)
            }
        }
    }

    private fun refreshHandleImageViewSize(newComputedSize: Int = -1) {
        // todo@shahsurajk add fork for horizontal layout
        if (newComputedSize == -1) {
            handleImageView.layoutParams = LinearLayout.LayoutParams(handleWidth, handleHeight)
        } else {
        }
    }

    private fun addThumbAndTrack() {
        View.inflate(context, R.layout.fastscroller_track_thumb, this)
        handleImageView = findViewById(R.id.thumbIV)
        trackView = findViewById(R.id.trackView)
    }

    private fun addPopupLayout() {
        View.inflate(context, R.layout.fastscroller_popup, this)
        popupTextView = findViewById(R.id.fastScrollPopupTV)
    }


    private fun enableNestedScrolling() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isNestedScrollingEnabled = true
        }
    }

    private fun moveHandle(offset: Float) {
        post {
            handleImageView.scaleX = 1F
            handleImageView.scaleY = 1F
        }

        if (handleVisibilityDuration > 0) {
            hideHandleJob?.cancel()

            hideHandleJob = CoroutineScope(Dispatchers.Main).launch {
                delay(handleVisibilityDuration.toLong())
            }
        }

        moveViewToRelativePositionWithBounds(handleImageView, offset)
        moveViewToRelativePositionWithBounds(popupTextView, offset - popupLength)
    }

    private fun moveViewToRelativePositionWithBounds(view: View, finalOffset: Float) {
        when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL ->
                view.x = min(max(finalOffset, 0F), (trackLength - view.width.toFloat()))
            FastScrollDirection.VERTICAL ->
                view.y = min(max(finalOffset, 0F), (trackLength - view.height.toFloat()))
        }
    }


    private fun loadDimenFromResource(@DimenRes dimenSize: Int): Int =
        context.resources.getDimensionPixelSize(dimenSize)

    private fun loadDrawableFromAttributes(@StyleableRes styleId: Int) =
        attribs?.getDrawable(styleId)

    private fun LinearLayoutManager.getTotalCompletelyVisibleItemCount(): Int {

        val firstVisibleItemPosition =
            this.findFirstCompletelyVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
                ?: this.findFirstVisibleItemPosition()

        val lastVisibleItemPosition =
            this.findLastCompletelyVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
                ?: this.findLastVisibleItemPosition()

        return if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) {
            RecyclerView.NO_POSITION
        } else {
            lastVisibleItemPosition - firstVisibleItemPosition
        }
    }

    private fun RecyclerView.safeScrollToPosition(position: Int) {
        with(this.layoutManager) {
            when (this) {
                is LinearLayoutManager -> scrollToPositionWithOffset(position, 0)
                is RecyclerView.LayoutManager -> scrollToPosition(position)
            }
        }
    }

    private fun RecyclerView.computePositionForOffsetAndScroll(relativeRawPos: Float): Int {
        val layoutManager: RecyclerView.LayoutManager? = this.layoutManager
        val recyclerViewItemCount = this.adapter?.itemCount ?: 0

        val newOffset = relativeRawPos / (trackLength - handleLength)
        return when (layoutManager) {
            is LinearLayoutManager -> {
                val totalVisibleItems = layoutManager.getTotalCompletelyVisibleItemCount()

                if (totalVisibleItems == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION

                previousTotalVisibleItem = max(previousTotalVisibleItem, totalVisibleItems)

                val position =
                    if (layoutManager.reverseLayout)
                        min(
                            recyclerViewItemCount,
                            max(
                                0,
                                recyclerViewItemCount - (newOffset * (recyclerViewItemCount - totalVisibleItems)).roundToInt()
                            )
                        )
                    else
                        min(
                            recyclerViewItemCount,
                            max(
                                0,
                                (newOffset * (recyclerViewItemCount - totalVisibleItems)).roundToInt()
                            )
                        )

                val toScrollPosition =
                    min((this.adapter?.itemCount ?: 0) - (previousTotalVisibleItem + 1), position)
                safeScrollToPosition(toScrollPosition)
                position
            }
            else -> {
                val position = (newOffset * recyclerViewItemCount).roundToInt()
                safeScrollToPosition(position)
                position
            }
        }
    }

    private fun updateTextInPopup(position: Int) {
        if (position !in 0 until (recyclerView.adapter?.itemCount ?: 1)) {
            return
        }

        when (val adapter = recyclerView.adapter) {
            null -> {
                throw IllegalAccessException("No adapter found, if you have an adapter then try placing if before calling the attachFastScrollerToRecyclerView() method")
            }
            is OnPopupTextUpdate -> popupTextView.text = adapter.onChange(position).toString()
            is OnPopupViewUpdate -> {
                adapter.onUpdate(position, popupTextView)
            }
            else -> {
                popupTextView.visibility = View.GONE
            }
        }
    }

    private val emptySpaceItemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount ?: 0 - 1) {
                    val currentVisiblePos: Int =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (currentVisiblePos != RecyclerView.NO_POSITION) {
                        outRect.bottom =
                            (parent.findViewHolderForAdapterPosition(currentVisiblePos)?.itemView?.height
                                ?: 0)
                    }
                }
            }
        }
    }

    private fun setEmptySpaceItemDecorator() {
        recyclerView.addItemDecoration(emptySpaceItemDecoration)
    }

    private val adapterDataObserver = lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                previousTotalVisibleItem = 0
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                previousTotalVisibleItem = 0
            }
        }
    }

    private fun registerDataObserver() {
        recyclerView.adapter?.registerAdapterDataObserver(adapterDataObserver.value)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isEngaged && isFastScrollEnabled) return

            val (range, extent, offset) =
                when ((recyclerView.layoutManager as LinearLayoutManager).orientation) {
                    RecyclerView.HORIZONTAL ->
                        Triple(
                            recyclerView.computeHorizontalScrollRange(),
                            recyclerView.computeHorizontalScrollExtent(),
                            recyclerView.computeHorizontalScrollOffset()
                        )
                    RecyclerView.VERTICAL ->
                        Triple(
                            recyclerView.computeVerticalScrollRange(),
                            recyclerView.computeVerticalScrollExtent(),
                            recyclerView.computeVerticalScrollOffset()
                        )
                    else -> error("The orientation of the LinearLayoutManager should be horizontal or vertical")
                }

            val error = extent.toFloat() * offset / range
            val finalOffset: Float = (trackLength - handleLength) * ((error + offset) / range)

            moveHandle(finalOffset)
        }
    }

    private fun initImpl() {
        if (hasEmptyItemDecorator) {
            setEmptySpaceItemDecorator()
        }
        registerDataObserver()
        recyclerView.addOnScrollListener(onScrollListener)
    }

    fun setHandleStateListener(handleStateListener: HandleStateListener) {
        this.handleStateListener = handleStateListener
    }

    @Keep
    fun attachFastScrollerToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        initImpl()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun detachFastScrollerFromRecyclerView() {

        if (adapterDataObserver.isInitialized()) {
            recyclerView.adapter?.unregisterAdapterDataObserver(adapterDataObserver.value)
        }
        handleImageView.setOnTouchListener(null)
        popupTextView.setOnTouchListener(null)
        recyclerView.removeOnScrollListener(onScrollListener)
        if (hasEmptyItemDecorator) {
            recyclerView.removeItemDecoration(emptySpaceItemDecoration)
        }
    }

    interface OnPopupViewUpdate {
        fun onUpdate(position: Int, popupTextView: TextView)
    }

    interface OnPopupTextUpdate {
        fun onChange(position: Int): CharSequence
    }

    interface HandleStateListener {

        fun onEngaged() {}


        fun onDragged(offset: Float, position: Int) {}

        fun onReleased() {}
    }
}