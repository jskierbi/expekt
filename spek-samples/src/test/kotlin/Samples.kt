package org.jetbrains.spek.samples

import org.jetbrains.spek.api.Spek

class SampleCalculator {
    fun sum(x: Int, y: Int) = x + y
    fun subtract(x: Int, y: Int) = x - y
}

class SampleIncUtil {
    fun incValueBy(value: Int, inc: Int) = value + inc
}

class ManualTest : Spek() {
    init {

        beforeEach { println("before each") }
        afterEach { println("after each") }

        given("1 (###ALE GO GO GO!!!)") {
            println("given1 exec")
            beforeOn { println("    beforeOn") }
            afterOn { println("    afterOn") }
            on("on1") {
                println("    ->on1 exec")
                it ("on1.it1") { println("      ->on1.it1 exec") }
                it ("on1.it2") { println("      ->on1.it2 exec") }
            }
            on("on2") {
                println("    ->on2 exec")
                it ("on2.it1") { println("      ->on2.it1 exec") }
                it ("on2.it2") { println("      ->on2.it2 exec") }
            }
        }
    }
}