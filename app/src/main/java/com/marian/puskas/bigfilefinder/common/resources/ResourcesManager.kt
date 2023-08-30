package com.marian.puskas.bigfilefinder.common.resources

import androidx.annotation.StringRes

interface ResourcesManager {
    fun getString(@StringRes stringResource: Int): String
    fun getString(@StringRes stringRes: Int, vararg arguments: Any): String
}