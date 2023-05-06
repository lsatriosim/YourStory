package com.example.yourstory.ui.Home

import UserPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityHomeBinding
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.network.remote.responses.Story
import com.example.yourstory.ui.ViewModelFactory
import com.example.yourstory.ui.detail.DetailStoryActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.example.yourstory.ui.adapter.LoadingStateAdapter
import com.example.yourstory.ui.adapter.StoryListAdapter
import com.example.yourstory.ui.login.LoginActivity
import com.example.yourstory.ui.maps.MapsActivity
import com.example.yourstory.ui.upload.UploadActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var preferences : UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStore = SingletonDatastore.getInstance(this)
        preferences = UserPreferences(dataStore)

        homeViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore), dataStore, this))[HomeViewModel::class.java]

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        getData()

        homeViewModel.isLoading.observe(this){
            showLoading(it)
        }

        val loginName = runBlocking {
            dataStore.data.first()[stringPreferencesKey("name")]
        }

        binding.loginname.text = loginName

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(this@HomeActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener{

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(getString(R.string.confirm_logout_title))
            alertDialogBuilder.setMessage(getString(R.string.confirm_logout_message))
            alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        preferences.clearUser()
                    }
                }
                val intent = Intent(this@HomeActivity,LoginActivity::class.java)
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.btnMap.setOnClickListener{
            startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Close the app when the back button is pressed from the home activity
        finishAffinity()
    }

    private fun onItemClick(photoView: ImageView, nameView: TextView, data: Story?) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this@HomeActivity,
            Pair.create(photoView, "photo"),
            Pair.create(nameView, "name")
        )

        Intent(this@HomeActivity, DetailStoryActivity::class.java).also{
            it.putExtra(DetailStoryActivity.EXTRA_PHOTO, data?.photoUrl)
            it.putExtra(DetailStoryActivity.EXTRA_NAME, data?.name)
            it.putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, data?.description)
            startActivity(it, options.toBundle())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getData(){
        val adapter = StoryListAdapter()
        adapter.setOnItemClickCallback(object:StoryListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Story?, itemView: View) {
                val photoView = itemView.findViewById<ImageView>(R.id.iv_item_photo)
                val nameView = itemView.findViewById<TextView>(R.id.tv_item_name)

                onItemClick(photoView,nameView,data)
            }
        })
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        homeViewModel.storyPaging.observe(this){pagingData->
            adapter.submitData(lifecycle,pagingData)
        }
    }

}
