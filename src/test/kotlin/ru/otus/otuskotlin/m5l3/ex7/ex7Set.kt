package ru.otus.otuskotlin.m5l3.ex7

import org.junit.jupiter.api.Test

internal class SetTest {
    @Test
    fun operations() {
        val numbers = setOf("one", "two", "three")

        println(numbers union setOf("four", "five"))
        println(setOf("four", "five") union numbers)

        println(numbers intersect setOf("two", "one"))
        println(numbers subtract setOf("three", "four"))
        println(numbers subtract setOf("four", "three")) // same output
    }

}