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
    public void testParseOnlyComment() throws Exception {
        parse(
                "; comment"
        );
    }

    @Test
    public void testParseSimple() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addPosting("expenses", "10");
        handler.addPosting("assets", null);
        handler.finish();

        parse(
                "2011-2-26 first transaction",
                "  expenses  10",
                "  assets"
        );
    }

    @Test
    public void testParseAccountWithSpace() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addPosting("my expenses", "10");
        handler.addPosting("super mega assets", null);
        handler.finish();

        parse(
                "2011-2-26 first transaction",
                "  my expenses  10",
                "  super mega assets"
        );
    }

    @Test
    public void testParseVariousAmounts() throws Exception {
        handler.start("2011-2-26", "various amounts mixed");
        handler.addPosting("assets:brokerage", "50 AAPL @ $10.0");
        handler.addPosting("liabilities", "10 EUR");
        handler.addPosting("billable:client one", "-0.00277h @ $35.00");
        handler.finish();

        parse(
                "2011-2-26 various amounts mixed",
                "  assets:brokerage  50 AAPL @ $10.0",
                "  liabilities  10 EUR",
                "  billable:client one  -0.00277h @ $35.00"
        );
    }

    @Test
    public void testParsePostingComment() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addPosting("expenses", "10");
        handler.addPosting("assets", null);
        handler.finish();

        parse(
                "2011-2-26 first transaction",
                "  expenses  10 ; some note",
                "  assets"
        );
    }

    @Test
    public void testParseSimpleWithExtraSpaces() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addPosting("expenses", "10");
        handler.addPosting("assets", null);
        handler.finish();

        parse(
                "2011-2-26  first transaction ",
                "  expenses   10 ",
                "  assets   "
        );
    }

    @Test
    public void testParseSimpleWithTabs() throws Exception {
        handler.start("2011-2-26", "first\ttransaction");
        handler.addPosting("expenses", "10");
        handler.addPosting("other expenses", "10");
        handler.addPosting("assets", null);
        handler.finish();

        parse(
                "2011-2-26\tfirst\ttransaction\t",
                "\texpenses\t10 ",
                "\t other expenses \t10 ",
                " \tassets\t"
        );
    }

    @Test(expected = Parser.ParserException.class)
    public void testThrowWhenPostingIsOutsideTransaction() throws Exception {
        parse(
                "  expenses  10"
        );
    }

    @Test(expected = Parser.ParserException.class)
    public void testThrowWhenEmptyLineBreaksTransaction() throws Exception {
        handler.start("2011-2-26", "first transaction");
        handler.addPosting("expenses", "10");
        handler.finish();
        parse(
                "2011-2-26 first transaction",
                "  expenses  10",
                "",
                "  assets"
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
