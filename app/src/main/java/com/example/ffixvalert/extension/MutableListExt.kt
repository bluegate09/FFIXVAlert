package com.example.ffixvalert.extension

fun MutableList<String>.toMessageBody() =
    when (this.size) {
        1 -> this[0]
        else -> this.joinToString(", ")
            .replaceAfterLast(this[this.lastIndex-1]," and ${this.last()}")

    }