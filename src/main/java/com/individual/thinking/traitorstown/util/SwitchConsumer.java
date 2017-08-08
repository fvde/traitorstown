package com.individual.thinking.traitorstown.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SwitchConsumer<T> {
    Predicate<T> conditionalConsumer;
    private SwitchConsumer(Predicate<T> pred) {
        conditionalConsumer = pred;
    }

    public static <C> SwitchConsumer<C> inCase(Predicate<C> pred, Consumer<C> cons) {
        return new SwitchConsumer<>(testAndConsume(pred, cons));
    }

    public SwitchConsumer<T> elseIf(Predicate<T> pred, Consumer<T> cons) {
        return new SwitchConsumer<>(conditionalConsumer.or(testAndConsume(pred,cons)));
    }

    public Consumer<T> elseDefault(Consumer<T> cons) {
        return testAndConsume(conditionalConsumer.negate(),cons)::test;   // ::test converts Predicate to Consumer
    }

    private static <T> Predicate<T> testAndConsume(Predicate<T> pred, Consumer<T> cons) {
        return t -> {
            boolean result = pred.test(t);
            if (result) cons.accept(t);
            return result;
        };
    }
}
