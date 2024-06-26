package com.rayliu.commonmain.data.mapper.basic

interface NullableOutputListMapper<I, O> : Mapper<List<I>, List<O>?>

class NullableOutputListMapperImpl<I, O>(
    private val mapper: Mapper<I, O>
) : NullableOutputListMapper<I, O> {
    override fun map(input: List<I>): List<O>? =
        if (input.isEmpty()) {
            null
        } else {
            input.map { mapper.map(it) }
        }
}
