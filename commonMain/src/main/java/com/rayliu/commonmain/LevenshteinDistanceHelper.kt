package com.rayliu.commonmain

import me.xdrop.fuzzywuzzy.FuzzySearch

class LevenshteinDistanceHelperImpl : LevenshteinDistanceHelper {
    override fun check(
        original: String,
        target: String
    ): Int = FuzzySearch.tokenSortRatio(original, target)
}

interface LevenshteinDistanceHelper {
    fun check(
        original: String,
        target: String
    ): Int
}
