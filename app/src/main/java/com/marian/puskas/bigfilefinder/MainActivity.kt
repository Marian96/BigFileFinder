package com.marian.puskas.bigfilefinder

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bigfilefinder.ui.Icons
import com.marian.puskas.bigfilefinder.common.Constants
import com.marian.puskas.bigfilefinder.common.notifications.NotificationProvider
import com.marian.puskas.bigfilefinder.common.notifications.model.NotificationMessage
import com.marian.puskas.bigfilefinder.ui.screens.Screen
import com.marian.puskas.bigfilefinder.ui.screens.directories.DirectoriesScreen
import com.marian.puskas.bigfilefinder.ui.screens.main.MainScreen
import com.marian.puskas.bigfilefinder.ui.screens.searchresults.SearchResultsScreen
import com.marian.puskas.bigfilefinder.ui.theme.BigFileFinderTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationProvider: NotificationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BigFileFinderTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val numberOfFiles = rememberSaveable { mutableStateOf("") }
                    val context = LocalContext.current
                    var notificationPermissionDialogShown by remember {
                        mutableStateOf(false)
                    }
                    var hasNotificationPermission by remember {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mutableStateOf(
                                ContextCompat.checkSelfPermission(
                                    context,
                                    POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            )
                        } else mutableStateOf(true)
                    }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            hasNotificationPermission = isGranted
                            notificationPermissionDialogShown = true
                        }
                    )
                    NavHost(
                        navController = navController,
                        startDestination = Screen.MainScreen.route
                    ) {
                        composable(route = Screen.MainScreen.route) {
                            MainScreen(
                                numberOfFiles = numberOfFiles,
                                onChooseDirectoryClick = {
                                    if (checkReadStoragePermission()) {
                                        navController.navigate(Screen.DirectoriesScreen.route)
                                    } else {
                                        requestReadPermission()
                                    }
                                },
                                searchFiles = {
                                    val searchScreenRoute = Screen.SearchResultsScreen.route + "/${numberOfFiles.value}"
                                    if (hasNotificationPermission || notificationPermissionDialogShown) {
                                        navController.navigate(searchScreenRoute)
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            launcher.launch(POST_NOTIFICATIONS)
                                            if (notificationPermissionDialogShown) {
                                                navController.navigate(searchScreenRoute)
                                            }
                                        } else {
                                            navController.navigate(searchScreenRoute)
                                        }
                                    }
                                }
                            )
                        }
                        composable(route = Screen.DirectoriesScreen.route) {
                            DirectoriesScreen {
                                navController.navigate(Screen.MainScreen.route)
                            }
                        }
                        composable(route = Screen.SearchResultsScreen.route + "/{number_of_files}") {
                            SearchResultsScreen(onBackButtonClick = {
                                navController.navigate(Screen.MainScreen.route)
                            })
                        }
                    }
                }
            }
        }

        notificationProvider.observeNotificationToSend()
            .onEach { showNotification(it) }
            .launchIn(lifecycleScope)
    }

    private fun showNotification(notificationMessage: NotificationMessage) {
        try {
            val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(notificationMessage.notificationIcon)
                .setContentTitle(notificationMessage.title)
                .setContentText(notificationMessage.description)
                .build()
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun requestReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:${applicationContext.packageName}")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(READ_EXTERNAL_STORAGE), 30)
        }
    }

    private fun checkReadStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED
        }
    }
}