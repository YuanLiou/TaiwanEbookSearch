package com.rayliu.commonmain.data.mapper.basic

interface ListMapper<I, O> : Mapper<List<I>, List<O>>

class ListMapperImpl<I, O>(
    private val mapper: Mapper<I, O>
) : ListMapper<I, O> {
    override fun map(input: List<I>): List<O> = input.map { mapper.map(it) }
}
