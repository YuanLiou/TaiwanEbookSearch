package liou.rayyuan.ebooksearchtaiwan.model.data.mapper.basic

interface Mapper<I, O> {
    fun map(input: I): O
}