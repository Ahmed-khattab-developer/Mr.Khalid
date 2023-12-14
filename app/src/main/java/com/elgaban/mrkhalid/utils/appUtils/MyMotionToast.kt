package com.elgaban.mrkhalid.utils.appUtils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.elgaban.mrkhalid.R.*
import com.elgaban.mrkhalid.databinding.MotionToastBinding

class MyMotionToast {

    companion object {

        const val LONG_DURATION = 10000L // 10 seconds
        const val TOAST_SUCCESS = "SUCCESS"
        const val TOAST_ERROR = "FAILED"
        const val TOAST_NO_INTERNET = "NO INTERNET"
        const val GRAVITY_BOTTOM = 80

        private var successToastColor: Int = color.successColor
        private var errorToastColor: Int = color.errorColor
        private var warningToastColor: Int = color.warningColor

        // all dark toast CTA
        fun darkToast(
            context: Activity, message: String, title: String? = null,
            style: String, position: Int, duration: Long, font: Typeface?
        ) {

            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(layout.motion_toast, null)
            val binding = MotionToastBinding.bind(view)

            when (style) {
                // Function for Toast Success
                TOAST_SUCCESS -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(context, drawable.ic_check_green)
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, successToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(binding.colorToastView, context)
                    binding.colorToastText.setTextColor(
                        ContextCompat.getColor(context, successToastColor)
                    )
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) TOAST_SUCCESS else title

                    setDescriptionDetails(font, message, binding.colorToastDescription)

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = binding.colorToastView
                    toast.duration = Toast.LENGTH_SHORT
                    toast.show()
                }
                // CTA for Toast Error
                TOAST_ERROR -> {

                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(context, drawable.ic_error_)
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, errorToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(binding.colorToastView, context)

                    // Setting up the color for title & Message text

                    binding.colorToastText.setTextColor(
                        ContextCompat.getColor(context, errorToastColor)
                    )
                    binding.colorToastText.text = if (title.isNullOrBlank()) TOAST_ERROR else title

                    setDescriptionDetails(font, message, binding.colorToastDescription)

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = binding.colorToastView
                    toast.duration = Toast.LENGTH_SHORT
                    toast.show()
                }
                // CTA for Toast No Internet
                TOAST_NO_INTERNET -> {
                    binding.colorToastImage.setImageResource(drawable.ic_no_internet)

                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, warningToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(binding.colorToastView, context)

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(
                        ContextCompat.getColor(context, warningToastColor)
                    )
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) TOAST_NO_INTERNET else title

                    setDescriptionDetails(font, message, binding.colorToastDescription)

                    // init toast
                    val toast = Toast(context.applicationContext)

                    //   Setting up the duration
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = binding.colorToastView
                    toast.duration = Toast.LENGTH_SHORT
                    toast.show()
                }
            }
        }


        private fun startTimer(duration: Long, toast: Toast) {
            val timer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // do nothing
                }

                override fun onFinish() {
                    toast.cancel()
                }
            }
            timer.start()
        }

        private fun setDescriptionDetails(font: Typeface?, message: String, layout: TextView) {
            layout.setTextColor(Color.WHITE)
            layout.text = message
            font?.let { layout.typeface = font }
        }

        private fun setGravity(position: Int, toast: Toast) {
            if (position == GRAVITY_BOTTOM) {
                toast.setGravity(position, 0, 100)
            } else {
                toast.setGravity(position, 0, 0)
            }
        }

        private fun setBackgroundAndFilter(layout: View, context: Context) {
            val drawable = ContextCompat.getDrawable(context, drawable.toast_round_background)
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, color.darkBgColor), PorterDuff.Mode.MULTIPLY
            )
            layout.background = drawable
        }
    }

}