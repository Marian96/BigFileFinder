package com.marian.puskas.bigfilefinder.common.resources

import android.content.Context
import android.content.res.Resources

class ResourcesManagerImpl (
    private val applicationResources: Resources,
    private val applicationContext: Context
) : ResourcesManager {
    override fun getString(stringResource: Int): String = applicationResources.getString(stringResource)
    override fun getString(stringRes: Int, vararg arguments: Any): String = applicationContext.getString(stringRes, *arguments)
}