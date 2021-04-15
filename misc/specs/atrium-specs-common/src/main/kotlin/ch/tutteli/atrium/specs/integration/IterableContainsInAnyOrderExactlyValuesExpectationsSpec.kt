package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.translations.DescriptionIterableAssertion.EXACTLY
import org.spekframework.spek2.style.specification.Suite

abstract class IterableContainsInAnyOrderExactlyValuesExpectationsSpec(
    containsExactlyPair: Pair<(String, String) -> String, Fun3<Iterable<Double>, Int, Double, Array<out Double>>>,
    containsNotPair: Pair<String, (Int) -> String>,
    describePrefix: String = "[Atrium] "
) : IterableContainsSpecBase({

    val containsExactly = containsExactlyPair.second

    include(object : SubjectLessSpec<Iterable<Double>>(
        describePrefix,
        containsExactly.forSubjectLess(2, 2.3, arrayOf())
    ) {})

    fun describeFun(vararg pairs: SpecPair<*>, body: Suite.() -> Unit) =
        describeFunTemplate(describePrefix, pairs.map { it.name }.toTypedArray(), body = body)

    fun Expect<Iterable<Double>>.containsExactlyFun(atLeast: Int, a: Double, vararg aX: Double) =
        containsExactly.invoke(this, atLeast, a, aX.toTypedArray())

    val (containsNot, errorMsgContainsNot) = containsNotPair

    val exactly = EXACTLY.getDefault()

    describeFun(containsExactly) {
        context("throws an $illegalArgumentException") {
            it("for exactly -1 -- only positive numbers") {
                expect {
                    expect(oneToSeven()).containsExactlyFun(-1, 0.0)
                }.toThrow<IllegalArgumentException> { messageContains("positive number", -1) }
            }
            it("for exactly 0 -- points to $containsNot") {
                expect {
                    expect(oneToSeven()).containsExactlyFun(0, 0.0)
                }.toThrow<IllegalArgumentException> { message { toEqual(errorMsgContainsNot(0)) } }
            }
        }

        context("iterable ${oneToSeven().toList()}") {

            context("happy case with $containsExactly once") {
                it("${containsExactlyPair.first("1.0", "once")} does not throw") {
                    expect(oneToSeven()).containsExactlyFun(1, 1.0)
                }
                it("${containsExactlyPair.first("1.0 and 2.0 and 3.0", "once")} does not throw") {
                    expect(oneToSeven()).containsExactlyFun(1, 1.0, 2.0, 3.0)
                }
                it("${containsExactlyPair.first("3.0 and 1.0 and 2.0", "once")} does not throw") {
                    expect(oneToSeven()).containsExactlyFun(1, 3.0, 1.0, 2.0)
                }
            }

            context("failing cases; search string at different positions with $containsExactly once") {
                it("${containsExactlyPair.first("4.0", "once")} throws AssertionError") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(1, 4.0)
                    }.toThrow<AssertionError> { messageContains("$exactly: 1", "$anElementWhichIs: 4.0") }
                }

                it("${containsExactlyPair.first("1.0, 2.3", "once")} throws AssertionError mentioning only 2.3") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(1, 1.0, 2.3)
                    }.toThrow<AssertionError> {
                        message {
                            toContain("$exactly: 1", "$anElementWhichIs: 2.3")
                            notToContain("$anElementWhichIs: 1.0")
                        }
                    }
                }

                it("${containsExactlyPair.first("2.3, 1.0", "once")} throws AssertionError mentioning only 2.3") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(1, 2.3, 1.0)
                    }.toThrow<AssertionError> {
                        message {
                            toContain("$exactly: 1", "$anElementWhichIs: 2.3")
                            notToContain("$anElementWhichIs: 1.0")
                        }
                    }
                }

                it("${containsExactlyPair.first("1.0 and 2.3 and 3.1", "once")} throws AssertionError") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(1, 1.0, 2.3, 3.1)
                    }.toThrow<AssertionError> {
                        message {
                            toContain(exactly, 2.3, 3.1)
                            toContain.exactly(2).values(
                                "$numberOfOccurrences: 0",
                                "$exactly: 1"
                            )
                            toContain.exactly(1).values(
                                "$rootBulletPoint$containsInAnyOrder: $separator",
                                "$anElementWhichIs: 2.3",
                                "$anElementWhichIs: 3.1"
                            )
                        }
                    }
                }
            }

            context("multiple occurrences of the search string") {
                it("${containsExactlyPair.first("5.0", "once")} throws AssertionError") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(1, 5.0)
                    }.toThrow<AssertionError> { messageContains(EXACTLY.getDefault()) }
                }
                it("${containsExactlyPair.first("5.0", "twice")} does not throw") {
                    expect(oneToSeven()).containsExactlyFun(2, 5.0)
                }

                it(
                    "${containsExactlyPair.first(
                        "5.0",
                        "3 times"
                    )} throws AssertionError and message contains both, how many times we expected (3) and how many times it actually contained 5.0 (2)"
                ) {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(3, 5.0)
                    }.toThrow<AssertionError> {
                        message {
                            toContain(
                                "$rootBulletPoint$containsInAnyOrder: $separator",
                                "$anElementWhichIs: 5.0",
                                "$numberOfOccurrences: 2$separator"
                            )
                            toEndWith("$exactly: 3")
                        }
                    }
                }

                it("${containsExactlyPair.first("5.0 and 4.0", "twice")} throws AssertionError") {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(2, 5.0, 4.0)
                    }.toThrow<AssertionError> {
                        message {
                            toContain(
                                "$rootBulletPoint$containsInAnyOrder: $separator",
                                "$anElementWhichIs: 4.0",
                                "$numberOfOccurrences: 3$separator"
                            )
                            toEndWith("$exactly: 2")
                            notToContain("$anElementWhichIs: 5.0")
                        }
                    }
                }
                it("${containsExactlyPair.first("4.0", "3 times")} does not throw") {
                    expect(oneToSeven()).containsExactlyFun(3, 4.0)
                }
                it(
                    "${containsExactlyPair.first(
                        "5.0 and 4.0",
                        "3 times"
                    )} throws AssertionError and message contains both, how many times we expected (3) and how many times it actually contained 5.0 (2)"
                ) {
                    expect {
                        expect(oneToSeven()).containsExactlyFun(3, 5.0, 4.0)
                    }.toThrow<AssertionError> {
                        message {
                            toContain(
                                "$rootBulletPoint$containsInAnyOrder: $separator",
                                "$anElementWhichIs: 5.0",
                                "$numberOfOccurrences: 2$separator"
                            )
                            toEndWith("$exactly: 3")
                            notToContain("$anElementWhichIs: 4.0")
                        }
                    }
                }
            }
        }
    }
})
