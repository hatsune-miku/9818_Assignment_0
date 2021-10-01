package a0;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class A0SExp {
    /**
     * Measures the length of a SExp in a non-recursive way.
     * Rules:
     * - Length of a Nil, which represents empty list (), is zero.
     * - Length of a Symbol is 1.
     *
     * @param x The SExp to be measured.
     * @return The length of the SExp x.
     */
    public static int length(SExp x) {
        final AtomicInteger ans = new AtomicInteger(0);
        walkSExpPreorder(x, (exp) -> {
            if (exp.isAtomic() && !exp.isNil()) {
                ans.addAndGet(1);
            }
            return true;
        });
        return ans.get();
    }

    /**
     * Measures the height of a SExp.
     * Rules:
     * - Height of a Symbol is 1.
     * - Height of a Nil is 0.
     * - Height of a cons x is <code>max(x.first(), x.rest()) + 1</code>
     *
     * @param x The SExp to be measured.
     * @return The height of the SExp x.
     */
    public static int height(SExp x) {
        if (x.isNil()) {
            return 0;
        }
        if (x.isAtomic()) {
            return 1;
        }
        return Math.max(
            height(x.first()),
            height(x.rest())
        ) + 1;
    }

    /*
    public static int heightFast(SExp x) {
        final Queue<SExp> q = new ArrayDeque<>();
        int ans = 0;

        // (nil, (S, (S, (nil, S) ) ) )
        q.add(x);
        while (!q.isEmpty()) {
            SExp exp = q.poll();

            if (exp.isNil()) {
                continue;
            }
            if (exp.isAtomic()) {
                ++ans;
            }
            else {
                q.add(exp.first());
                q.add(exp.rest());
            }
        }
        return ans;
    }
     */

    /**
     * Provided a SExp in ( (a0, b0), (a1, b1), ... ) key-value pairs,
     * where `a`s are keys and `b`s are values, given an `a` and return its
     * corresponding `b`.
     *
     * @param s The key.
     * @param list The k-v pairs represented by SExp.
     * @return The corresponding `b` of given `a`. Nil if key not found.
     * @throws IllegalArgumentException if the format is incorrect.
     */
    public static SExp lookup(SExp.Symbol s, SExp list) /* throws IllegalArgumentException */ {
        if (list.isNil() || list.isAtomic()) {
            throw new IllegalArgumentException("Malformed list.");
        }

        // Assume that the list is in
        // ((a0, b0), ((a1, b1), ((a2, b2), ...Nil))) form.
        while (!list.isNil()) {
            final SExp pair = list.first();

            if (!(pair instanceof SExp.Cons)) {
                throw new IllegalArgumentException("Malformed list.");
            }

            final SExp key = pair.first();
            final SExp value = pair.rest();

            if (!(key instanceof SExp.Symbol)) {
                throw new IllegalArgumentException("Invalid symbol.");
            }

            if (key.eq(s)) {
                return value;
            }

            list = list.rest();
        }

        return SExp.nil();
    }

    /**
     * Replaces all occurrences of `a`s with SExp `x`s in SExp y.
     *
     * @param a The symbol to be replaced.
     * @param x The replacement.
     * @param y The SExp where replacement takes place.
     * @return The new replaced SExp.
     * @ensure The original SExp `y` will remain unchanged.
     */
    public static SExp replace(SExp.Symbol a, SExp x, SExp y) {
        replaceSharedParamA = a;
        replaceSharedParamX = x;
        return replacePart(y);
    }

    // I can't write this non-recursively,
    // but by using shared memory this can run
    // a bit faster.
    private static SExp replaceSharedParamA;
    private static SExp replaceSharedParamX;

    private static SExp replacePart(SExp y) {
        if (y instanceof SExp.Cons) {
            return new SExp.Cons(
                replacePart(y.first()),
                replacePart(y.rest())
            );
        }
        // Ideas: Deep copy and replace.
        return y.eq(replaceSharedParamA)
            ? replaceSharedParamX
            : y;
    }

    /**
     * Examines whether the SExp x is equal to SExp y in value. Rules:
     *  - If both are Symbols: Checks the value equality.
     *  - If both are Nils: Then of course they are equal.
     *  - If both are Cons: Check if they represent the same binary tree.
     *  - If not the same type: Not equal.
     *
     * @param x The SExp x.
     * @param y The SExp y.
     * @return Equal: <code>true</code>, Not equal: <code>false</code>.
     */
    static public boolean equal(SExp x, SExp y) {
        if (x.isNil()) {
            return y.isNil();
        }
        if (x.isAtomic()) {
            return x.eq(y);
        }
        if (y.isNil() || y.isAtomic()) {
            return false;
        }
        return equal(x.first(), y.first())
            && equal(x.rest(), y.rest());
    }

    /**
     * Concatenate the List y into the tail of List x. Example:
     * Concat (1,2,3) and (4,5,6) will get (1,2,3,4,5,6).
     *
     * @param x The first elements of new list.
     * @param y The remaining elements of new list.
     * @return The cancatenated list.
     * @throws IllegalArgumentException if either x or y is not a list.
     */
    public static SExp concat(SExp x, SExp y) {
        // (1, (2, (3, nil)))
        // (4, (5, (6, nil)))
        AtomicReference<SExp> ret = new AtomicReference<>(SExp.nil());
        walkSExpPreorder(x, (e) -> {
            ret.set(SExp.cons(e, ret.get()));
            return true;
        });
        ret.set(ret.get().rest());
        walkSExpPreorder(y, (e) -> {
            ret.set(SExp.cons(e, ret.get()));
            return true;
        });
        ret.set(ret.get().rest());
        return reverse(ret.get());
    }

    /**
     * Reverse the List x. Example:
     * The Reverse of list (1,2,3,4,5,6) is (6,5,4,3,2,1).
     * This function will not recurse into sublists. Example:
     * The Reverse of list (1,2,(3,4),5) is (5,(3,4),2,1) instead of (5,(4,3),2,1).
     *
     * @param x The list to be reversed.
     * @return The reversed list.
     */
    public static SExp reverse(SExp x) {
        SExp ret = SExp.nil();

        if (x.isNil() || x.isAtomic()) {
            return x;
        }

        while (!x.isNil()) {
            ret = SExp.cons(x.first(), ret);
            x = x.rest();
        }

        return ret;
    }

    /**
     * Flatten a multi-dimension list into a 1d list and remove nils. Example:
     * The flatted form of (1,((nil, (nil, 2)),(3,(4,(5, nil))))) is (1,2,3,4,5).
     *
     * @param x The list to be flatted.
     * @return The flatted list.
     */
    public static SExp flatten(SExp x) {
        AtomicReference<SExp> ret = new AtomicReference<>(SExp.nil());
        walkSExpPreorder(x, (e) -> {
            // System.out.println(e.toString());
            if (!e.isNil()) {
                ret.set(SExp.cons(e, ret.get()));
            }
            return true;
        });
        return reverse(ret.get());
    }

    /**
     * Maps each element in list according to the given processor <code>f</code>.
     * Example: The map of (1,2,3) where f(x)=x+1, is (4,5,6).
     *
     * @param f The processor that accepts a SExp and return its replacement.
     * @param list The list to be mapped.
     * @return The mapped list consist of all elements processed by <code>f</code>.
     */
    public static SExp mapList(Function<SExp, SExp> f, SExp list) {
        if (list.isAtomic()) {
            return list;
        }
        return SExp.cons(f.apply(list.first()), mapList(f, list.rest()));
    }

    /**
     * A bulk <code>lookUp</code> mapping all keys to their
     * corresponding values in the k-v pair `list`.
     *
     * @param keys The keys to be queried.
     * @param list The key-value pairs in the same form of <code>lookUp</code>.
     * @return The list of values of given keys.
     */
    public static SExp lookUpMany(SExp keys, final SExp list) {
        return mapList(
            s -> lookup((SExp.Symbol) s, list),
            keys
        );
    }

    /**
     * Preorder Traverse a SExp.
     *
     * @param exp The SExp to be traversed.
     * @param callback The callback function is called every time when
     *                 a node is reached. Return false in the callback
     *                 to terminate the traverse immediately,
     *                 otherwise return true to continue.
     * @ensure Guarantees that node is either a Symbol or a Nil.
     */
    private static void walkSExpPreorder(SExp exp, Function<SExp, Boolean> callback) {
        final Stack<SExp> st = new Stack<SExp>();

        st.push(exp);
        while (!st.empty()) {
            final SExp e = st.pop();
            if (e.isNil()) {
                if (!callback.apply(e)) {
                    return;
                }
            }
            else if (e.isAtomic()) {
                if (!callback.apply(e)) {
                    return;
                }
            }
            else {
                // root.
                st.push(e.rest());
                st.push(e.first());
            }
        }
    }

    /** A faster implementation of <code>SExp.list</code> using
     * a non-recursive method.
     *
     * @param exps The items to be put in the list. Each of
     *             exps should be either String or SExp.
     *             Strings are automatically converted to
     *             Symbols.
     * @return
     */
    public static SExp listFast(Object ...exps) {
        // 1, 2, 3, 4
        // (1, (2, (3, 4)))
        SExp ret = SExp.nil();
        for (int i = exps.length - 1; i >= 0; --i) {
            Object exp = exps[i];
            ret = new SExp.Cons(
                exp instanceof String
                    ? SExp.symbol((String) exp)
                    : (SExp) exp,
                ret
            );
        }
        return ret;
    }

    /**
     * A faster implementation of <code>SExp.listNotation</code> that
     * uses a non-recursive method.
     * @param x
     * @return
     */
    public static String listNotationFast(SExp x) {
        // Special cases where x is simply a Nil or a symbol.
        if (x.isNil()) {
            return "()";
        }
        if (x.isAtomic()) {
            return x.toString();
        }

        StringBuilder builder = new StringBuilder("(");
        Stack<SExp> st = new Stack<>();

        st.push(x);
        while (!st.empty()) {
            SExp exp = st.pop();
            if (exp.isNil()) {
                // To prevent unexpected whitespaces.
                int len = builder.length();
                if (len > 0) {
                    switch (builder.charAt(len - 1)) {
                        case ')', ' ' -> builder.setLength(len - 1);
                    }
                }

                // Close bracket immediately for nil values.
                builder.append(") ");
            }
            else if (exp.isAtomic()) {
                // Append atomic exps.
                builder.append(exp).append(" ");
            }
            else {
                if (exp.first() instanceof SExp.Symbol
                    && exp.rest() instanceof SExp.Symbol) {
                    // Dotted pair?
                    builder.append(exp.first())
                        .append(" . ")
                        .append(exp.rest())
                        .append(")");
                }
                else {
                    // Push cons into stack.
                    st.push(exp.rest());
                    st.push(exp.first());
                }

                // Start a bracket for first or nil cons parts.
                if (!exp.first().isAtomic() || exp.first().isNil()) {
                    builder.append("(");
                }
            }
        } // while

        return builder.toString().trim();
    }

    /**
     * A shortcut to create symbols.
     * @param s The value of symbol.
     * @return The symbol with the value <code>s</code>.
     */
    public static SExp.Symbol s(String s) {
        return SExp.symbol(s);
    }
}
