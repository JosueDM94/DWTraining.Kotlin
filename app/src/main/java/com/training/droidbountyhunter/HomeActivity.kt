package com.training.droidbountyhunter

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.training.droidbountyhunter.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var fragments: ArrayList<Fragment> = ArrayList()
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager,fragments)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = mSectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            val intent = Intent(this, AgregarActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_agregar) {
            val intent = Intent( this , AgregarActivity::class.java)
            startActivityForResult(intent, 0 )
        }
        return super .onOptionsItemSelected(item)
    }

    fun actualizarListas(index: Int ){
        view_pager?.adapter = mSectionsPagerAdapter
        view_pager.currentItem = index
    }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent ?) {
        super .onActivityResult(requestCode, resultCode, data)
        actualizarListas(resultCode)
    }
}