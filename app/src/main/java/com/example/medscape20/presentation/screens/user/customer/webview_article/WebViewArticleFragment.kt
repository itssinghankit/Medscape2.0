package com.example.medscape20.presentation.screens.user.customer.webview_article

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentWebviewArticleBinding


class WebViewArticleFragment : Fragment() {

    private var _binding: FragmentWebviewArticleBinding? = null
    private val binding get() = _binding!!
    private val args: WebViewArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWebviewArticleBinding.inflate(layoutInflater, container, false)
        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility=View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility=View.GONE
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //to prevent it from opening chrome app
        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString()) // Load the URL within the WebView
                return true // Prevent opening in external browser
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressCircular.visibility = View.VISIBLE // Show progress bar
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                binding.progressCircular.visibility = View.GONE // Hide progress bar
            }
        }

        binding.webview.apply {
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            loadUrl(args.url)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}