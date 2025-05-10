package org.leavesmc.plugin.mixin.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class ComparableCondition<T extends Comparable<T>> {
    private final Predicate<T> pred;

    private ComparableCondition(Predicate<T> pred) {
        this.pred = pred;
    }

    public boolean matches(T target) {
        return pred.test(target);
    }

    public static <T extends Comparable<T>> ComparableCondition<T> parse(String input, Function<String, T> parser) {
        Parser<T> p = new Parser<>(input, parser);
        ComparableCondition<T> result = p.parse();
        if (p.pos != p.expr.length()) {
            throw new IllegalArgumentException("Uncompleted condition" + p.expr.substring(p.pos) + "'");
        }
        return result;
    }

    private static class Parser<T extends Comparable<T>> {
        private final String expr;
        private int pos = 0;
        private final Function<String, T> parser;

        @Contract(pure = true)
        Parser(@NotNull String expr, Function<String, T> parser) {
            this.expr = expr.replaceAll("\\s+", "");
            this.parser = parser;
        }

        ComparableCondition<T> parse() {
            return parseOr();
        }

        private ComparableCondition<T> parseOr() {
            ComparableCondition<T> left = parseAnd();
            while (match("||")) {
                ComparableCondition<T> right = parseAnd();
                left = new ComparableCondition<>(left.pred.or(right.pred));
            }
            return left;
        }

        private ComparableCondition<T> parseAnd() {
            ComparableCondition<T> left = parseNot();
            while (match("&&")) {
                ComparableCondition<T> right = parseNot();
                left = new ComparableCondition<>(left.pred.and(right.pred));
            }
            return left;
        }

        private ComparableCondition<T> parseNot() {
            if (match("!")) {
                ComparableCondition<T> c = parseNot();
                return new ComparableCondition<>(c.pred.negate());
            }
            return parseAtom();
        }

        private ComparableCondition<T> parseAtom() {
            if (match("(")) {
                ComparableCondition<T> c = parse();
                if (!match(")")) throw new IllegalArgumentException("Missing `)`");
                return c;
            }
            String token = parseToken();

            if (token.startsWith(">="))
                return new ComparableCondition<>(v -> v.compareTo(parser.apply(token.substring(2))) >= 0);
            if (token.startsWith("<="))
                return new ComparableCondition<>(v -> v.compareTo(parser.apply(token.substring(2))) <= 0);
            if (token.startsWith(">"))
                return new ComparableCondition<>(v -> v.compareTo(parser.apply(token.substring(1))) > 0);
            if (token.startsWith("<"))
                return new ComparableCondition<>(v -> v.compareTo(parser.apply(token.substring(1))) < 0);
            if (token.matches("[^=><!(),]+")) {
                T value = parser.apply(token);
                return new ComparableCondition<>(v -> v.compareTo(value) == 0);
            }

            throw new IllegalArgumentException("Unsupported Condition: " + token);
        }

        private @NotNull String parseToken() {
            int start = pos;
            while (pos < expr.length() &&
                peekNot('&') &&
                peekNot('|') &&
                peekNot('!') &&
                peekNot('(') &&
                peekNot(')')
            ) {
                pos++;
            }
            return expr.substring(start, pos);
        }

        private boolean peekNot(char c) {
            return expr.charAt(pos) != c;
        }

        private boolean match(String expect) {
            if (expr.startsWith(expect, pos)) {
                pos += expect.length();
                return true;
            }
            return false;
        }
    }
}