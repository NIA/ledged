package ru.nia.jledger.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Parser {
    class ParserException extends Exception {
        ParserException(String msg, String line) {
            super(msg + " in line \"" + line + "\"");
        }
    }

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

    public void parse() throws IOException, ParserException {
        boolean inTransaction = false;

        for (;;) {
            final String line = input.readLine();
            if (line == null) {
                break;
            }
            if (line.isEmpty()) {
                continue;
            }

            char switcher = line.charAt(0);
            if (Character.isDigit(switcher)) {
                // new transaction
                if (inTransaction) {
                    transactionHandler.finish();
                }
                inTransaction = true;

                String[] parts = splitIntoTwoParts(line, TRANSACTION_DELIMITER);
                transactionHandler.start(parts[0], parts[1]);
            } else if (Character.isSpaceChar(switcher)) {
                // field line
                if (!inTransaction) {
                    throw new ParserException("transaction field outside transaction", line);
                }

                String[] parts = splitIntoTwoParts(line.trim(), FIELD_DELIMITER);
                transactionHandler.addField(parts[0], parts[1]);
            } else {
                throw new ParserException("unsupported format", line);
            }
        }

        if (inTransaction) {
            transactionHandler.finish();
        }
    }

    private String[] splitIntoTwoParts(String line, String delimiter) {
        if (line.contains(delimiter)) {
            int pos = line.indexOf(delimiter);
            return new String[]{ line.substring(0, pos).trim(), line.substring(pos + delimiter.length()).trim() };
        } else {
            return new String[]{ line.trim(), null };
        }
    }
}
