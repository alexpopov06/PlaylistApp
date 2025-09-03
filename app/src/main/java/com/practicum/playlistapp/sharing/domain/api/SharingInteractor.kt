package com.practicum.playlistapp.sharing.domain.api

import android.content.Intent

interface SharingInteractor {
    fun writeSupport():Intent
    fun shareLink():Intent
    fun agreement():Intent

}