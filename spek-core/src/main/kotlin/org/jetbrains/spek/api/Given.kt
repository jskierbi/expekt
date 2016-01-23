package org.jetbrains.spek.api


open class GivenImpl : org.jetbrains.spek.api.Given {
    private val recordedActions = linkedListOf<org.jetbrains.spek.api.TestOnAction>()
    private val beforeActions = linkedListOf<() -> Unit>()
    private val afterActions = linkedListOf<() -> Unit>()

    //    public fun iterateOn(callback: (org.jetbrains.spek.api.TestOnAction) -> Unit) {
    //        org.jetbrains.spek.api.removingIterator(recordedActions) {
    //            // This doesn't actually work. Tests pass but tests are wrong
    //            beforeActions.forEach { it() }
    //            try {
    //                callback(it)
    //            } finally {
    //                afterActions.forEach { it() }
    //            }
    //        }
    //    }

    public fun listOn() = recordedActions

    public override fun beforeOn(it: () -> Unit) {
        beforeActions.add(it)
    }

    public override fun afterOn(it: () -> Unit) {
        afterActions.add(it)
    }

    public override fun on(description: String, onExpression: org.jetbrains.spek.api.On.() -> Unit) {
        recordedActions.add(
                object : org.jetbrains.spek.api.TestOnAction {
                    val on: OnImpl by lazy {
                        val impl = OnImpl()
                        impl.onExpression() // Delay on expression execution, let it be executed by test runner
                        impl
                    }

                    public override fun description() = "on " + description

                    override fun listIt() = on.listIt()

                    override fun run(action: () -> Unit) {
                        beforeActions.forEach { it() }
                        action()
                        afterActions.forEach { it() }
                    }
                })
    }
}

