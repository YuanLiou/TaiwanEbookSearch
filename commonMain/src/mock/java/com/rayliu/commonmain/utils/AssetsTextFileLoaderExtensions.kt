package com.rayliu.commonmain.utils

import android.content.res.AssetManager

fun AssetManager.loadJsonFromFile(fileName: String): String {
    return open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}