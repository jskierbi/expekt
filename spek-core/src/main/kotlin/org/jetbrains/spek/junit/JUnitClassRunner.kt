package org.jetbrains.spek.junit

import org.jetbrains.spek.api.PendingException
import org.jetbrains.spek.api.SkippedException
import org.jetbrains.spek.api.Spek
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import java.io.Serializable
import java.util.*

data class JUnitUniqueId(val id: Int) : Serializable {
    companion object {
        var id = 0
        fun next() = JUnitUniqueId(id++)
    }
}

public fun junitAction(description: Description, notifier: RunNotifier, action: () -> Unit) {
    if (description.isTest) notifier.fireTestStarted(description)
    try {
        action()
    } catch(e: SkippedException) {
        notifier.fireTestIgnored(description)
    } catch(e: PendingException) {
        notifier.fireTestIgnored(description)
    } catch(e: Throwable) {
        notifier.fireTestFailure(Failure(description, e))
    } finally {
        if (description.isTest) notifier.fireTestFinished(description)
    }
}

data class SpekResult(val successful: Boolean = false, val exception: Throwable? = null)

public fun runSpek(testIdHashCode: Int, results: HashMap<Int, SpekResult>, action: () -> Unit) {
    try {
        action()
        results.put(testIdHashCode, SpekResult(successful = true))
    } catch(e: Throwable) {
        results.put(testIdHashCode, SpekResult(exception = e))
    }
}

public fun evaluateResults(desc: Description?, notifier: RunNotifier?, results: HashMap<Int, SpekResult>) {
    desc?.children?.forEach { child -> evaluateResults(child, notifier, results) }
    desc?.apply {
        val testId = hashCode()
        val result = results[testId]
        notifier?.apply {
            fireTestStarted(desc)
            when (result?.exception) {
                is SkippedException -> notifier.fireTestIgnored(desc)
                is PendingException -> notifier.fireTestIgnored(desc)
                is Throwable -> notifier.fireTestFailure(Failure(desc, result?.exception))
            }
            fireTestFinished(desc)
        }
    }
}

public class JUnitClassRunner<T>(val specificationClass: Class<T>) : ParentRunner<Unit>(specificationClass) {
    private val suiteDescription = Description.createSuiteDescription(specificationClass)

    val _spekRunResults: HashMap<Int, SpekResult> = HashMap()

    val _description by lazy(LazyThreadSafetyMode.NONE) {
        val suiteDesc = Description.createSuiteDescription(specificationClass)
        val spek = specificationClass.newInstance() as Spek
        spek.listGiven().forEach { givenSpek ->
            val givenId = JUnitUniqueId.next()
            val givenDesc = Description.createSuiteDescription(givenSpek.description(), givenId)
            suiteDesc.addChild(givenDesc)
            runSpek(givenId.hashCode(), _spekRunResults) {
                givenSpek.listOn().forEach { onSpek ->
                    val onId = JUnitUniqueId.next()
                    val onDesc = Description.createSuiteDescription(onSpek.description(), onId)
                    givenDesc.addChild(onDesc)
                    runSpek(onId.hashCode(), _spekRunResults) {
                        givenSpek.run {
                            onSpek.run {
                                onSpek.listIt().forEach { itSpek ->
                                    val itId = JUnitUniqueId.next()
                                    val itDesc = Description.createSuiteDescription(itSpek.description(), itId)
                                    onDesc.addChild(itDesc)
                                    runSpek(itId.hashCode(), _spekRunResults) { itSpek.run {} }
                                }
                            }
                        }
                    }
                }
            }
        }
        suiteDesc
    }

    override fun getDescription(): Description = _description

    override fun run(notifier: RunNotifier?) {
        evaluateResults(_description, notifier, _spekRunResults)
    }

    override fun getChildren(): MutableList<Unit> = ArrayList()

    protected override fun describeChild(child: Unit?): Description? = null

    protected override fun runChild(child: Unit?, notifier: RunNotifier?) {
    }
}

fun evaluateChildren(desc: Description?, notifier: RunNotifier?) {

}
