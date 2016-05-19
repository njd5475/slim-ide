package com.njd5475.github.slim.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class Loli {
    private static Loli instance = new Loli();

    private Loli() {
    }

    private Object read(File file) {
        try {
            FileReader freader = new FileReader(file);
            Token last = null, next = null;
            int c;
            List<Token> tokens = new LinkedList<Token>();
            while ((c = freader.read()) != -1) {
                if (last == null) {
                    next = last = new Token((char) c);
                } else {
                    next = last.next((char) c);
                }

                if (next != last) {
                    tokens.add(last);
                }
                last = next;
            }
            freader.close();
            System.out.println(tokens);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    enum T_TYPE {
        WHITESPACE, SEPARATOR, OPEN_BRACE, CLOSE_BRACE, BOUNDARY, IDENTIFIER, NEWLINE
    };

    protected class Token {
        private T_TYPE        type;
        private StringBuilder token = new StringBuilder();

        public Token(char c) {
            type = type(c);
            token.append(c);
        }

        public T_TYPE type() {
            return type;
        }

        public Token next(char c) {
            if (type(c).equals(this.type)) {
                token.append(c);
                return this;
            }

            return new Token(c);
        }

        public T_TYPE type(char c) {
            if (c == ' ' || c == '\t') {
                return T_TYPE.WHITESPACE;
            } else if (c == '\n') {
                return T_TYPE.NEWLINE;
            } else if (c == ':') {
                return T_TYPE.SEPARATOR;
            } else if (c == '[') {
                return T_TYPE.OPEN_BRACE;
            } else if (c == ']') {
                return T_TYPE.CLOSE_BRACE;
            } else if (c == '|') {
                return T_TYPE.BOUNDARY;
            } else {
                return T_TYPE.IDENTIFIER;
            }
        }

        public String toString() {
            return String.format("%s", type);
        }
    }

    public static void main(String... args) {
        Loli.instance.read(new File("./themes/color.loli"));
    }
}
