package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.database.AppDatabase
import com.example.data.repository.ArtRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: ArtRepository
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SQLite Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ai_image_creator_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Initialize Art Repository
        repository = ArtRepository(
            walletDao = database.userWalletDao(),
            creationDao = database.creationDao(),
            commentDao = database.commentDao(),
            collectionDao = database.collectionDao()
        )

        // Initialize MainViewModel with repository injection
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(repository)
        )[MainViewModel::class.java]

        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
