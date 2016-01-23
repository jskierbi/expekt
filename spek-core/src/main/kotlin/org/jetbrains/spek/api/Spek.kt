package org.jetbrains.spek.api

import org.jetbrains.spek.junit.JUnitClassRunner
import org.junit.runner.RunWith

@RunWith(JUnitClassRunner::class)
public abstract class Spek : org.jetbrains.spek.api.Specification {

    private val recordedActions = linkedListOf<org.jetbrains.spek.api.TestGivenAction>()
    private val recordedBeforeActions = linkedListOf<() -> Unit>()
    private val recordedAfterActions = linkedListOf<() -> Unit>()

    override fun beforeEach(action: () -> Unit) {
        recordedBeforeActions.add(action)
    }

    override fun afterEach(action: () -> Unit) {
        recordedAfterActions.add(action)
    }

    public override fun given(description: String, givenExpression: org.jetbrains.spek.api.Given.() -> Unit) {
        recordedActions.add(
                object : org.jetbrains.spek.api.TestGivenAction {
                    val given: GivenImpl by lazy {
                        val impl = GivenImpl()
                        impl.givenExpression() // Delay given expresion execution, let it be executed by test runner
                        impl
                    }

                    public override fun description() = "given " + description

                    override fun listOn() = given.listOn()

                    override fun run(action: () -> Unit) {
                        recordedBeforeActions.forEach { it() }
                        action()
                        recordedAfterActions.forEach { it() }
                    }
                })

    }

//    public fun iterateGiven(it: (org.jetbrains.spek.api.TestGivenAction) -> Unit): Unit = org.jetbrains.spek.api.removingIterator(recordedActions, it)

    public fun allGiven(): List<org.jetbrains.spek.api.TestGivenAction> = recordedActions
}


public fun <T> Spek.givenData(data: Iterable<T>, givenExpression: org.jetbrains.spek.api.Given.(T) -> Unit) {
    for (entry in data) {
        given(entry.toString()) {
            givenExpression(entry)
        }
    }
}
