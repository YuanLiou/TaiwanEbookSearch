package com.rayliu.commonmain.data.mapper.basic

interface Mapper<I, O> {
    fun map(input: I): O
}