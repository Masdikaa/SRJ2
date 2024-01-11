package com.masdika.srj2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.masdika.srj2.databinding.ActivityMainBinding
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                Log.d("Bottom_Bar", "Selected index: $newIndex, title: ${newTab.title}")
                when (newTab.id) {
                    R.id.tab_profile -> {
                        binding.mapView.visibility = View.VISIBLE

                        // remove Fragment History
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragment = fragmentManager.findFragmentById(R.id.frame_layout)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(it).commit()
                            Log.d("Remove Fragment", "Fragment : $fragment")
                        }
                    }

                    R.id.tab_history -> {
                        // Disable mapview
                        binding.mapView.visibility = View.GONE

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, HistoryFragment()).commit()
                    }

                }
            }
        })

        binding.openDialog.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val btnClose = view.findViewById<ImageView>(R.id.close_button)

            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }
    }

}