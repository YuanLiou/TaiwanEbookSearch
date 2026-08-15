package com.rayliu.commonmain.domain.search

class BlankSearchQueryException : IllegalArgumentException("Search query is blank")

class NetworkUnavailableException : IllegalStateException("Network is unavailable")
