package ru.nia.jledger.core;

import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import static org.easymock.EasyMock.*;

public class ParserTest {
    TransactionHandler handler;

    @Before
    public void setUp() {
        handler = createStrictMock(TransactionHandler.class);
    }

    private void parse(String... strings) throws IOException, ParseException {
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
    public void testItWorks() throws IOException, ParseException {
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
}
