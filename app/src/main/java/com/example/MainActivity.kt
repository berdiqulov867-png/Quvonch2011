package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.db.AppDatabase
import com.example.data.db.DatabaseSeeder
import com.example.data.repository.SchoolRepository
import com.example.ui.BilimMarkaziApp
import com.example.ui.BilimViewModel
import com.example.ui.BilimViewModelFactory
import com.example.ui.theme.BilimMarkaziTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: BilimViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = SchoolRepository(db)

        lifecycleScope.launch(Dispatchers.IO) {
            DatabaseSeeder.seedIfNeeded(db)
        }

        val factory = BilimViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BilimViewModel::class.java]

        setContent {
            BilimMarkaziTheme {
                BilimMarkaziApp(viewModel = viewModel)
            }
        }
    }
}
