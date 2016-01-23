package org.jetbrains.spek.api

open class OnImpl : On {
    private val recordedActions = linkedListOf<TestItAction>()

    //    public fun iterateIt(it : (TestItAction) -> Unit) {
    //        removingIterator(recordedActions, it)
    //    }

    public fun listIt() = recordedActions

    public override fun it(description: String, itExpression: It.() -> Unit) {
        recordedActions.add(
                object : TestItAction {
                    public override fun description() = "it " + description

                    override fun run(action: () -> Unit) {
                        ItImpl().itExpression()
                    }
                })
    }
}

