package com.salazar.cheers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salazar.cheers.ui.chat.ChatViewModel
import com.salazar.cheers.ui.comment.CommentsViewModel
import com.salazar.cheers.ui.detail.PostDetailViewModel
import com.salazar.cheers.ui.otherprofile.OtherProfileViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun postDetailViewModelFactory(): PostDetailViewModel.PostDetailViewModelFactory
        fun otherProfileViewModelFactory(): OtherProfileViewModel.OtherProfileViewModelFactory
        fun chatViewModelFactory(): ChatViewModel.ChatViewModelFactory
        fun commentsViewModelFactory(): CommentsViewModel.CommentsViewModelFactory
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CheersApp()
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    @Composable
    fun ChangeProfileBottomSheet(bottomSheetState: ModalBottomSheetState) {
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.background)
                )
                {
                    Column {
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                        Text("Add photo")
                        Text("Take photo")
                    }
                }
            },
            sheetState = bottomSheetState
        ) {
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (mMessageReceiver),
            IntentFilter("NewMessage")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent
        ) {
            mainViewModel.onNewMessage()
        }
    }
}