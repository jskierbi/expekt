package org.jetbrains.spek.api

public interface SpekTestableComponent {
    public fun description(): String
    public fun run(action: () -> Unit)
}

public interface TestSpekAction : SpekTestableComponent {
    //    public fun iterateGiven(it:(TestGivenAction) -> Unit)
    public fun listGiven(): List<TestGivenAction>
}

public interface TestGivenAction : SpekTestableComponent {
    //    public fun iterateOn(it: (TestOnAction) -> Unit)
    public fun listOn(): List<TestOnAction>
}

public interface TestOnAction : SpekTestableComponent {
    //    public fun iterateIt(it : (TestItAction) -> Unit)
    public fun listIt(): List<TestItAction>
}

public interface TestItAction : SpekTestableComponent {
    //    public fun description(): String
    //    public fun run()
}

