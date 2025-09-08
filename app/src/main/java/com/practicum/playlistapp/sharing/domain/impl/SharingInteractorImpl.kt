package com.practicum.playlistapp.sharing.domain.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistapp.R
import com.practicum.playlistapp.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(private val context: Context) : SharingInteractor {

    override fun writeSupport(): Intent {
        val write = Intent(Intent.ACTION_SENDTO)
        write.data = Uri.parse("mailto:")
        write.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.myEmail)))
        write.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.MessageToDev))
        write.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.ThanksToDev))
        return write
    }

    override fun shareLink(): Intent {
        val share = Intent(Intent.ACTION_SENDTO)
        share.data = Uri.parse("smsto:")
        share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.LinkAndroid))
        return share
    }

    override fun agreement(): Intent {
        val browse = Intent(Intent.ACTION_VIEW)
        browse.data = Uri.parse(context.getString(R.string.AndoidOffer))
        return browse
    }
}