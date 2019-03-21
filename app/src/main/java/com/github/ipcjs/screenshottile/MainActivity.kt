package com.github.ipcjs.screenshottile

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.collection.LruCache
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.github.ipcjs.screenshottile.dialog.ContainerActivity
import com.github.ipcjs.screenshottile.dialog.SettingFragment


/**
 * Shows a how-to video.
 */
class MainActivity : Activity() {

    private val images = arrayOf(
        R.drawable.screenshot_01,
        R.drawable.screenshot_02,
        R.drawable.screenshot_03,
        R.drawable.screenshot_04,
        R.drawable.screenshot_05,
        R.drawable.screenshot_06,
        R.drawable.screenshot_07,
        R.drawable.screenshot_08,
        R.drawable.screenshot_09,
        R.drawable.screenshot_10,
        R.drawable.screenshot_11,
        R.drawable.screenshot_12,
        R.drawable.screenshot_13,
        R.drawable.screenshot_14,
        R.drawable.screenshot_15,
        R.drawable.screenshot_16
    )

    private lateinit var bitmapCache: BitmapCache

    private lateinit var viewPager: ViewPager
    private lateinit var textViewStep: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 4
        bitmapCache = BitmapCache(cacheSize)

        textViewStep = findViewById(R.id.textViewStep)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = TutorialPagerAdapter()
        viewPager.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem + 1 % images.size, true)
        }
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                textViewStep.text = (position + 1).toString()
            }
        })

        findViewById<Button>(R.id.buttonSettings)?.setOnClickListener {
            ContainerActivity.start(this, SettingFragment::class.java)
        }
    }

    private inner class ClickableImageView(context: Context?) : ImageView(context) {
        init {
            setOnClickListener {
                viewPager.setCurrentItem((viewPager.currentItem + 1) % images.size, true)
            }
        }
    }

    private inner class TutorialPagerAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return ClickableImageView(this@MainActivity).apply {
                setImageBitmap(bitmapCache.get(images[position]))
                (container as? ViewPager)?.addView(this, 0)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            (container as? ViewPager)?.removeView(obj as? View)
        }
    }

    private inner class BitmapCache(cacheSize: Int) : LruCache<Int, Bitmap>(cacheSize) {
        override fun sizeOf(resId: Int, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }

        override fun create(resId: Int): Bitmap? {
            return BitmapFactory.decodeResource(resources, resId)
        }
    }
}
