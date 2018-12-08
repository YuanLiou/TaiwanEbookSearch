package liou.rayyuan.ebooksearchtaiwan.utils

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentArgumentsDelegate<T: Any> : ReadWriteProperty<Fragment, T> {

    var value: T? = null

    @Suppress("UNCHECKED_CAST")    // Uncheck argument.get casting
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val argument = thisRef.arguments ?: throw IllegalStateException("Cannot read property ${property.name} if no argument have been set.")
            value = argument.get(property.name) as T
        }

        return value ?: throw IllegalStateException("Property ${property.name} could not be read.")
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val argument = thisRef.arguments ?: Bundle()
        val key = property.name

        argument.putAll(bundleOf(key to value))
    }

}