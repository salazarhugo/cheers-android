package com.salazar.cheers.ui.otherprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.CheersTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreOtherProfileBottomSheet : DialogFragment() {

    //    private val args: MoreOtherProfileBottomSheetArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModels()
//    private val viewModel: AddPostDialogViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DialogFullScreen)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.Theme_Cheers_Slide)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CheersTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "AddDialogFragment"
    }
}

