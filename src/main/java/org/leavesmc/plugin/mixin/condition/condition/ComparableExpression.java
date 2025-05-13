package org.leavesmc.plugin.mixin.condition.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class ComparableExpression<T extends Comparable<T>> {
    private final Predicate<T> pred;

    private ComparableExpression(Predicate<T> pred) {
        this.pred = pred;
    }

    public boolean matches(T target) {
        return pred.test(target);
    }

    public static <T extends Comparable<T>> ComparableExpression<T> parse(String input, Function<String, T> parser) {
        Parser<T> p = new Parser<>(input, parser);
        ComparableExpression<T> result = p.parse();
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

        ComparableExpression<T> parse() {
            return parseOr();
        }

        private ComparableExpression<T> parseOr() {
            ComparableExpression<T> left = parseAnd();
            while (match("||")) {
                ComparableExpression<T> right = parseAnd();
                left = new ComparableExpression<>(left.pred.or(right.pred));
            }
            return left;
        }

        private ComparableExpression<T> parseAnd() {
            ComparableExpression<T> left = parseNot();
            while (match("&&")) {
                ComparableExpression<T> right = parseNot();
                left = new ComparableExpression<>(left.pred.and(right.pred));
            }
            return left;
        }

        private ComparableExpression<T> parseNot() {
            if (match("!")) {
                ComparableExpression<T> c = parseNot();
                return new ComparableExpression<>(c.pred.negate());
            }
            return parseAtom();
        }

        private ComparableExpression<T> parseAtom() {
            if (match("(")) {
                ComparableExpression<T> c = parse();
                if (!match(")")) throw new IllegalArgumentException("Missing `)`");
                return c;
            }
            String token = parseToken();

            if (token.startsWith(">="))
                return new ComparableExpression<>(v -> v.compareTo(parser.apply(token.substring(2))) >= 0);
            if (token.startsWith("<="))
                return new ComparableExpression<>(v -> v.compareTo(parser.apply(token.substring(2))) <= 0);
            if (token.startsWith(">"))
                return new ComparableExpression<>(v -> v.compareTo(parser.apply(token.substring(1))) > 0);
            if (token.startsWith("<"))
                return new ComparableExpression<>(v -> v.compareTo(parser.apply(token.substring(1))) < 0);
            if (token.matches("[^=><!(),]+")) {
                T value = parser.apply(token);
                return new ComparableExpression<>(v -> v.compareTo(value) == 0);
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