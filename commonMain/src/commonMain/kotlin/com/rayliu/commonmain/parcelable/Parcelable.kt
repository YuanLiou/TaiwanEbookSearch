package com.rayliu.commonmain.parcelable

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(BINARY)
annotation class Parcelize

expect interface Parcelable
