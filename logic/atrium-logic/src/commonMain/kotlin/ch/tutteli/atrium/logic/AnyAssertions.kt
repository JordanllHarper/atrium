package ch.tutteli.atrium.logic

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.creating.AssertionContainer
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic.creating.transformers.SubjectChangerBuilder
import kotlin.reflect.KClass

/**
 * Collection of assertion functions and builders which are applicable to any type (sometimes `Any?` sometimes `Any`).
 */
interface AnyAssertions {
    fun <T> toBe(container: AssertionContainer<T>, expected: T): Assertion
    fun <T> notToBe(container: AssertionContainer<T>, expected: T): Assertion
    fun <T> isSameAs(container: AssertionContainer<T>, expected: T): Assertion
    fun <T> isNotSameAs(container: AssertionContainer<T>, expected: T): Assertion

    fun <T : Any> toBeNullIfNullGivenElse(
        container: AssertionContainer<T?>,
        assertionCreatorOrNull: (Expect<T>.() -> Unit)?
    ): Assertion

    fun <T : Any> notToBeNullButOfType(
        container: AssertionContainer<T?>,
        subType: KClass<T>
    ): SubjectChangerBuilder.ExecutionStep<T?, T>

    @Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
    fun <T, TSub> isA(
        container: AssertionContainer<T>,
        subType: KClass<TSub>
    ): SubjectChangerBuilder.ExecutionStep<T, TSub> where TSub : Any, TSub : T

    fun <T> isNotIn(container: AssertionContainer<T>, expected: Iterable<T>): Assertion

    fun <T> because(
        container: AssertionContainer<T>,
        reason: String,
        assertionCreator: (Expect<T>.() -> Unit)
    ): Assertion

    fun <T> notToBeAnInstanceOf(container: AssertionContainer<T>, notExpectedTypes: List<KClass<*>>): Assertion
}
