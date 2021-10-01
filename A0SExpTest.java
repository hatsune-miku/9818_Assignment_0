package a0;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static a0.SExp.*;
import static a0.A0SExp.*;

class A0SExpTest {
    @Test
    void testLength() {
        // listFast is my non-recursive impl of `SExp.list`,
        // which is also designed to auto convert
        // Strings to Symbols.
        assertEquals(4, length(listFast(
            "a", "b", symbol("c"), "d"
        ))); // #=> (a (b (c (d Nil))))

        // The method `s` is a shortcut to SExp.symbol()
        assertEquals(5, length(listFast(
            s("1"), listFast(s("2"), cons(s("x"), s("y")), s("3"))
        )));

        assertEquals(0, length(listFast()));
        assertEquals(3, length(listFast(
            "a", listFast("b", nil(), s("c"))
        )));
        assertEquals(0, length(nil()));
    }

    @Test
    void testHeight() {
        assertEquals(
            2,
            height(
                cons(
                    s("b"),
                    s("c")
                )
            )
        );

        assertEquals(
            7,
            height(
                cons(
                    cons(
                        s("xx"),
                        s("yy")
                    ),
                    cons(
                        s("e"),
                        cons(
                            s("s"),
                            cons(
                                s("a"),
                                cons(
                                    cons(
                                        s("x"),
                                        s("y")
                                    ),
                                    cons(
                                        s("xx"),
                                        s("yy")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );
    }

    @Test
    void testLookup() {
        SExp map = listFast(
            cons(s("CoolGuy"), s("xxx")),
            cons(s("RealCoolGuy"), s("yyy")),
            cons(s("AbsoluteRealCoolGuy"), s("Theodore"))
        );

        assertEquals(
            s("xxx"),
            lookup(s("CoolGuy"), map)
        );

        assertEquals(
            s("yyy"),
            lookup(s("RealCoolGuy"), map)
        );

        assertEquals(
            s("Theodore"),
            lookup(s("AbsoluteRealCoolGuy"), map)
        );

        assertEquals(
            nil(),
            lookup(s("404"), map)
        );

        try {
            // This will throw an IllegalArgumentException,
            // as the map is malformed.
            lookup(s("s"), symbol("MALFORMED"));
            fail();
        }
        catch (IllegalArgumentException e) {
            // Ignored.
        }

        try {
            // This will also produce an IllegalArgumentException,
            // as the map contains a non-symbol key.
            lookup(s("s"), listFast(
                cons(nil(), symbol("xxx"))
            ));
            fail();
        }
        catch (IllegalArgumentException e) {
            // Ignored.
        }

        try {
            // This will also produce an IllegalArgumentException,
            // as the map has inconsistent structure.
            lookup(s("s"), listFast(
                cons(symbol("xxx"), symbol("V1111")),
                cons(symbol("yyy"), symbol("V2222")),
                symbol("EVIL"),
                cons(symbol("zzz"), symbol("V3333"))
            ));
            fail();
        }
        catch (IllegalArgumentException e) {
            // Ignored.
        }
    }

    @Test
    void testReplace() {
        final SExp exp = listFast(
            listFast(
                s("k"),
                nil()
            ),
            listFast(
                nil(),
                listFast(
                    s("k"),
                    s("z")
                ),
                cons(
                    s("xxx"),
                    s("k")
                )
            )
        );

        // listNotationFast is my non-recursive impl
        // of `SExp.listNotation`.
        assertEquals(
            "((j ()) (() (j z) (xxx . j))",
            listNotationFast(
                replace(s("k"), s("j"), exp)
            )
        );
    }

    @Test
    void testEqual() {
        assertTrue(equal(nil(), nil()));
        assertTrue(equal(s("a"), s("a")));
        assertTrue(equal(
            listFast("a", listFast(nil(), "b", nil()), "c"),
            listFast("a", listFast(nil(), "b", nil()), "c")
        ));
        assertFalse(equal(
            listFast("a", listFast(nil(), "x", nil()), "c"),
            listFast("a", listFast(nil(), "b", nil()), "c")
        ));
        assertFalse(equal(
            listFast("a", listFast(nil(), "x", nil()), "c"),
            nil()
        ));
        assertFalse(equal(
            symbol("x"),
            nil()
        ));
    }

    @Test
    void testReverse() {
        assertTrue(
            equal(
                listFast("c", "b", "a"),
                reverse(
                        listFast("a", "b", "c")
                )
            )
        );
    }

    // O(2m+2n)
    @Test
    void testConcat() {
        SExp x = listFast("a", "b", "c");
        SExp y = listFast("d", "e", "f");
        assertTrue(
            equal(
                listFast("a", "b", "c", "d", "e", "f"),
                concat(x, y)
            )
        );
    }

    @Test
    void testFlatten() {
        SExp x = listFast(
            "x", "y", listFast(
                "z", "w"
            ), "a", "b", listFast(
                "c", listFast(
                    "d", "e"
                )
            ), "f"
        );
        assertTrue(
            equal(
                listFast("x", "y", "z", "w", "a",
                    "b", "c", "d", "e", "f"),
                flatten(x)
            )
        );
    }

    @Test
    void testLookUpMany() {
        SExp map = listFast(
            cons(s("ShinyBoy"), s("xxx")),
            cons(s("RealShinyBoy"), s("yyy")),
            cons(s("AbsoluteRealShinyBoy"), s("Z"))
        );

        SExp keys = listFast(
            "RealShinyBoy",
            "ShinyBoy",
            "NotExistKey",
            "AbsoluteRealShinyBoy"
        );

        SExp valuesExpected = listFast(
            "yyy",
            "xxx",
            nil(),
            "Z"
        );

        assertTrue(
            equal(
                valuesExpected,
                lookUpMany(keys, map)
            )
        );
    }

    @Test
    void testLispNotationFast() {
        SExp a = listFast( s( "abcd" ), s( "def" )  ) ;
        SExp b = cons( s( "Not" ), cons( s("exactly"), cons( s("a"), s("list") ) ) );
        SExp c = cons( s("dotted"), s("pair" ) ) ;
        SExp d = listFast( listFast( s("lists"), s("nested") ),
            listFast( s("in"), s("lists") ) ) ;
        SExp s = s( "Hello" ) ;
        assertEquals( "(abcd def)", listNotationFast(a) ) ;
        assertEquals( "(Not exactly a . list)", listNotationFast(b) ) ;
        assertEquals( "(dotted . pair)", listNotationFast(c) ) ;
        assertEquals( "((lists nested) (in lists))", listNotationFast(d) ) ;
        assertEquals( "Hello", listNotationFast(s) ) ;
        assertEquals( "()", listNotationFast( nil() ) ) ;
    }

    @Test
    void testListFast() {
        // The list to be built.
        final SExp[] args = {
            s("1"),
            s("2"),
            listFast(
                s(""),
                list(
                    nil(),
                    s("xxx1"),
                    nil(),
                    s("xxx2"),
                    s("xxx3")
                ),
                nil()
            ),
            nil(),
            s("2")
        };

        // vararg in java is equivalent to an array.
        final SExp listNormal = list(args);
        final SExp list = listFast(args);

        assertEquals(
            listNotationFast(list),
            listNotationFast(listNormal)
        );
    }
}
