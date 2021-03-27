# Travello Technical Test - Eric Murano

This project implements the Supermarket Checkout coding challenge provided by
Travello.


## Time taken

Total time taken for coding and project setup was 5h 36m. Writing this README took 40 minutes.

This is the first time I've set up a gradle project from scratch, without Spring Boot from
Spring Initializr.

Time was tracked using a simple Markdown table with start and stop times.

| Date       | Start   | End     | Task                                                                                     | Time Spend |
|:---------- | ------- | ------- | ---------------------------------------------------------------------------------------- | ---------- |
| 2021-03-25 | 6:30am  | 7:08am  | Set up basic java application project, email Tom questions and create initial interfaces | 38m        |
| 2021-03-25 | 7:52pm  | 8:29pm  | Start implementing MultiPricedCheckout                                                   | 38m        |
| 2021-03-25 | 10:30pm | 11:30pm | Implement simple prices and start Special Price logic                                    | 60m        |
| 2021-03-26 | 6:20am  | 7:07am  | Implement multi-item prices                                                              | 47m        |
| 2021-03-26 | 5:03pm  | 5:45pm  | Handle missing pricing rules for the number of items e.g. no price for 1 item            | 42m        |
| 2021-03-26 | 8:13pm  | 10:04pm | Demo in main method, fix pricing rule bug, fix up gradle build file                      | 111m       |
| 2021-03-26 | 10:05pm | 10:45pm | Write README.md file                                                                     | 40m        |
| 2021-03-27 | 8:00PM  | 8:23pm  | Handle case of twice pricing rules with same quantity but different prices               | 23m        | 

## Project Configuration

The project is a barebones Java application using JUnit4 for unit tests and Mockito
for test doubles.

I have not used any linting tools or any pipeline automation tools.

## External Libraries

I did not need to any libraries for the implementation.

The test suite uses Junit 4 and Mockito.

## Build & Execution Instructions

The project can build a small application that shows the checkout code working.

The code is set to have source and target compatibility of 1.8. Be sure to be using a java runtime that is 1.8 or newer.

To build the project, you can either run it directly from a Gradle command:

```shell
$ ./gradlew run

> Task :run
== Supermarket Application ==
Total checkout: 485.35

BUILD SUCCESSFUL in 1s
2 actionable tasks: 1 executed, 1 up-to-date
```

You can also build the jar file then run that manually:

```shell
$ ./gradlew build

BUILD SUCCESSFUL in 1s
7 actionable tasks: 3 executed, 4 up-to-date

$ java -jar build/libs/supermarket-1.0.jar
== Supermarket Application ==
Total checkout: 485.35
```

The run the tests

```shell
$ ./gradlew cleanTest test --info

... ommitted for brevity ...

Successfully started process 'Gradle Test Executor 10'
Finished generating test XML results (0.007 secs) into: XXXXX/supermarket/build/test-results/test
Generating HTML test report...
Finished generating test html results (0.012 secs) into: XXXXX/supermarket/build/reports/tests/test
:test (Thread[Daemon worker Thread 15,5,main]) completed. Took 0.976 secs.

BUILD SUCCESSFUL in 2s
4 actionable tasks: 2 executed, 2 up-to-date

```

## Notes

I implemented the code in a way that would technically allow you to have more than one special price per SKU. This did not add to the dev time, but I feel like it made the pricing rules interface more elegant than giving a pricing rule a price and then a special price with a threshold quantity.

I initially thought I would need to break the `Checkout` / `MultiPricedCheckout` class into separate classes to keep the code in `MultiPricedCheckout` cohesive and follow SRP. I usually separate logic when I feel like I need to defer some decisions about some part of the code. That's usually a good indicator that adding that code to the class I'm working on would have introduced more complexity than I would like.

I tend to prefer returning interfaces and consuming interfaces instead of concrete classes as a way to shift responsibility to the appropriate class.

## Things that could be done to improve the code / application

I feel like this code could look better:

```java
.map(rule -> {
    long remainingCount = skuCount.count() - rule.quantity();
    if (remainingCount > 0) {
        return rule.price().add(
            calculateSkuSubTotal(
                new SkuCount(skuCount.sku(), remainingCount)
            )
        );
    } else {
        return rule.price();
    }
})
```

It's not wrong, but I don't immediately understand what is going on (and I wrote it!).

## Test Coverage

I wrote tests for the core code, not the `Application` and `MyPricingRule` classes. Test coverage for `ericmurano.multipriced` is 100% line coverage.