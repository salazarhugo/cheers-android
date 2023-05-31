package com.salazar.cheers.core.util

fun <E> MutableSet<E>.addOrRemove(element: E) {
    if (!add(element)) {
        remove(element)
    }
}