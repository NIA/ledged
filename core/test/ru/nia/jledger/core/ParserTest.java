package ru.nia.jledger.core;

import org.junit.*;

import java.io.*;

import static org.easymock.EasyMock.*;

public class ParserTest {
    TransactionHandler handler;

    @Before
    public void setUp() {
        handler = createStrictMock(TransactionHandler.class);
    }

    private void parse(String... strings) throws Exception {
        replay(handler);

        String text = "";
        for(String s : strings) {
            text += s + "\n";
        }
        Parser parser = new Parser(new BufferedReader(new StringReader(text)), handler);
        parser.parse();

        verify(handler);
    }

    @Test
    public void testParseEmptySource() throws Exception {
        parse();
    }

    @Test
    public void testParseSimple() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addField("expenses", "10");
        handler.addField("assets", null);
        handler.finish();

        parse(
                "2011-2-26 first transaction",
                "  expenses  10",
                "  assets"
        );
    }

    @Test
    public void testParseSimpleWithExtraSpaces() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addField("expenses", "10");
        handler.addField("assets", null);
        handler.finish();

        parse(
                "2011-2-26  first transaction ",
                "  expenses   10 ",
                "  assets   "
        );
    }

    @Test(expected = Parser.ParserException.class)
    public void testThrowWhenFieldIsOutsideTransaction() throws Exception {
        parse(
                "  expenses  10"
        );
    }

    @Test
    @Ignore
    public void testParseYearSet() throws Exception {
        parse(
                "Y2011"
        );
    }
}
