package com.rayliu.commonmain.domain.search

class BlankSearchQueryException : IllegalArgumentException("Search query is blank")

class BlankSearchIdException : IllegalArgumentException("Search id is blank")

class SearchSnapshotNotFoundException : NoSuchElementException("Search snapshot was not found")

class NetworkUnavailableException : IllegalStateException("Network is unavailable")
