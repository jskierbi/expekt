package org.jetbrains.spek.samples

import org.jetbrains.spek.api.Spek
import kotlin.test.fail

class SampleCalculator {
    fun sum(x: Int, y: Int) = x + y
    fun subtract(x: Int, y: Int) = x - y
}

class SampleIncUtil {
    fun incValueBy(value: Int, inc: Int) = value + inc
}

class ManualTest : Spek() {
    init {

        beforeEach { println("    *before each") }
        afterEach { println("    *after each") }

        given("1") {
            println("  ->given1 expr start")
            beforeOn { println("    **beforeOn") }
            afterOn { println("    **afterOn") }
            on("1") {
                println("    ->on1 expr start")
                it ("1") {
                    println("      ->on1.it1 expr")
                    fail("failed it")
                }
                it ("2") { println("      ->on1.it2 expr") }
                println("    ->on1 expr end")
            }
            on("2") {
                println("    ->on2 expr start")
                it ("1") { println("      ->on2.it1 expr") }
                it ("2") { println("      ->on2.it2 expr") }
                println("    ->on2 expr end")
            }
            on("3") {
                println("    ->on3 expr start")
                it ("1") { println("      ->on3.it1 expr") }
                println("    ->on3 expr end")
            }
            println("  ->given1 expr end")
        }

        given("2") {
            println("  ->given 2 expr start")
            on("1") {
                println("    ->on1 expr start")
                it("1") { println("      ->on1.it1 expr") }
                it("2") { println("      ->on1.it2 expr") }
                println("    ->on1 expr end")
            }
            on("2") {
                println("    ->on2 expr start")
                it("1") { println("      ->on2.it1 expr") }
                it("2") { println("      ->on2.it2.expr") }
                println("    ->on2 expr end")
            }
            println("  ->given 2 expr end")
        }
    }
}