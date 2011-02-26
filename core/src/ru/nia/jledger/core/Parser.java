package ru.nia.jledger.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Parser {
    /* -- fields -- */

    private BufferedReader input;
    private TransactionHandler transactionHandler;

    /* -- constants -- */

    private static final char COMMENT_CHAR = ';';
    private static final char YEAR_CHAR = 'Y';

    private static final String TRANSACTION_DELIMITER = " ";
    private static final String FIELD_DELIMITER = "  ";

    public Parser(BufferedReader input, TransactionHandler transactionHandler) {
        this.input = input;
        this.transactionHandler = transactionHandler;
    }

    public boolean isSkipped(String line) {
        return line.isEmpty() || line.charAt(0) == COMMENT_CHAR
               /*hack*/
               || line.charAt(0) == YEAR_CHAR;
    }

    public boolean isTransaction(String line) {
        return line.matches("[0-9].*");
    }

    public String[] splitIntoTwoParts(String line, String delimiter) {
        if(line.contains(delimiter)) {
            int pos = line.indexOf(delimiter);
            return new String[]{ line.substring(0, pos), line.substring(pos + delimiter.length()) };
        } else {
            return new String[]{ line, null };
        }
    }

    public void parse() throws IOException, ParseException {
        boolean inTransaction = false;

        String line;
        while((line = input.readLine()) != null) {
            line = line.trim();

            if(isSkipped(line)) {
                continue;
            }

            if(isTransaction(line)) {
                if(inTransaction) {
                    transactionHandler.finish();
                }
                inTransaction = true;

                String[] parts = splitIntoTwoParts(line, TRANSACTION_DELIMITER);
                transactionHandler.start(parts[0], parts[1]);
            } else {
                /* otherwise - field line */
                if( ! inTransaction ) {
                    throw new ParseException("transaction field outside transaction", 0);
                }

                String[] parts = splitIntoTwoParts(line, FIELD_DELIMITER);
                transactionHandler.addField(parts[0], parts[1]);
            }
        }

        if(inTransaction) {
            transactionHandler.finish();
        }
    }
}
