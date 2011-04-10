package ru.nia.ledged.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
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
                        throw new ParserException(Problem.WHITESPACE_OUTSIDE_TRANSACTION, line, lineNumber);
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
    private int lineNumber;

    public Parser(BufferedReader input, TransactionHandler transactionHandler) {
        this.input = input;
        this.transactionHandler = transactionHandler;
    }

    public void parse() throws IOException, ParserException {
        inTransaction = false;
        lineNumber = 0;

        for (;;) {
            final String line = input.readLine();
            ++lineNumber;
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
                throw new ParserException(Problem.UNSUPPORTED_BEGINNING, line, lineNumber);
            }
        }

        if (inTransaction) {
            transactionHandler.finish();
        }
    }

    private String[] matchGroups(String line, String regex) throws ParserException {
        Matcher matcher = Pattern.compile(regex).matcher(line);
        if (!matcher.find()) {
            throw new ParserException(Problem.ILLEGAL_FORMAT, line, lineNumber);
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

    public enum Problem {
        WHITESPACE_OUTSIDE_TRANSACTION,
        UNSUPPORTED_BEGINNING,
        ILLEGAL_FORMAT
    }

    public class ParserException extends Exception {
        private final Problem problem;
        private final String line;
        private final int lineNubmer;

        ParserException(Problem problem, String line, int lineNubmer) {
            super(problem.toString() + " in line " + Integer.toString(lineNubmer) + " \"" + line + "\"");
            this.problem = problem;
            this.line = line;
            this.lineNubmer = lineNubmer;
        }

        public Problem getProblem() {
            return problem;
        }

        public String getLine() {
            return line;
        }

        public int getLineNubmer() {
            return lineNubmer;
        }
    }
}
