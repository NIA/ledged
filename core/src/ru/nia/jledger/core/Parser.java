package ru.nia.jledger.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public class ParserException extends Exception {
        ParserException(String msg, String line) {
            super(msg + " in line \"" + line + "\"");
        }
    }

    private interface LineProcessor {
        boolean canProcess(char firstChar);
        void process(String line) throws ParserException;
    }

    private LineProcessor[] lineProcessors = new LineProcessor[] {
            // comment line
            new LineProcessor() {
                public boolean canProcess(char firstChar) {
                    return firstChar == ';';
                }
                public void process(String line) throws ParserException {
                }
            },

            // year
            new LineProcessor() {
                public boolean canProcess(char firstChar) {
                    return firstChar == 'Y';
                }
                public void process(String line) throws ParserException {
                    finishTransaction();
                    transactionHandler.setYear(line.substring(1));
                }
            },

            // transaction description
            new LineProcessor() {
                public boolean canProcess(char firstChar) {
                    return Character.isDigit(firstChar);
                }
                public void process(String line) throws ParserException {
                    finishTransaction();

                    String[] groups = matchGroups(line, "^([^\\s]+)\\s+(.+)?$");
                    String date = groups[1];
                    String description = (groups[2] != null) ? groups[2].trim() : null;
                    transactionHandler.start(date, description);
                    inTransaction = true;
                }
            },

            // posting
            new LineProcessor() {
                public boolean canProcess(char firstChar) {
                    return Character.isWhitespace(firstChar);
                }
                public void process(String line) throws ParserException {
                    if (!inTransaction) {
                        throw new ParserException("line begins with whitespace outside transaction", line);
                    }

                    String[] groups = matchGroups(line, "^\\s+((?:[^\\s]| (?!\\s))*)(?:(?:\\s{2,}|\t\\s*)([^;]+)?)?\\s*(?:;.*)?$");
                    String account = groups[1];
                    String amount = (groups[2] != null) ? groups[2].trim() : null;
                    transactionHandler.addPosting(account, amount);
                }
            }
    };

    private BufferedReader input;
    private TransactionHandler transactionHandler;
    private boolean inTransaction;

    public Parser(BufferedReader input, TransactionHandler transactionHandler) {
        this.input = input;
        this.transactionHandler = transactionHandler;
    }

    public void parse() throws IOException, ParserException {
        inTransaction = false;

        for (;;) {
            final String line = input.readLine();
            if (line == null) {
                break;
            }
            if (line.trim().length() == 0) {
                finishTransaction();
                continue;
            }

            boolean processed = false;
            char firstChar = line.charAt(0);
            for (LineProcessor processor : lineProcessors) {
                if (processor.canProcess(firstChar)) {
                    processor.process(line);
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                throw new ParserException("unsupported format", line);
            }
        }

        if (inTransaction) {
            transactionHandler.finish();
        }
    }

    private String[] matchGroups(String line, String regex) throws ParserException {
        Matcher matcher = Pattern.compile(regex).matcher(line);
        if (!matcher.find()) {
            throw new ParserException("illegal format", line);
        }
        int groupCount = matcher.groupCount() + 1; // + 1 because 0'th group is entire string and is needed too
        String[] parts = new String[groupCount];
        for (int i = 0; i < groupCount; ++i) {
            parts[i] = matcher.group(i);
        }
        return parts;
    }

    private void finishTransaction() {
        if (inTransaction) {
            transactionHandler.finish();
        }
        inTransaction = false;
    }
}
