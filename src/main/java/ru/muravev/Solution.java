package ru.muravev;

import java.util.ArrayList;
import java.util.List;

import static ru.muravev.Solution.Lexeme.expr;
import static ru.muravev.Solution.Lexeme.lexAnalyze;


public class Solution {
    public static void main(String[] args) {
        String expressionText = "2 + 2 * 4";
        List<Lexeme> lexemes = lexAnalyze(expressionText);
        Lexeme.LexemeBuffer lexemeBuffer = new Lexeme.LexemeBuffer(lexemes);
        int a = expr(lexemeBuffer);
        System.out.println(a);
    }

    // нисходящий рекурсивный синтаксический анализатор
    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        NUMBER,
        EOF
    }

    // элемент лексемы
    public static class Lexeme {
        LexemeType type;
        String value;


        public Lexeme(LexemeType type, String value) {
            this.type = type;           
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{"
                    + "type=" + type
                    + ", value='" + value + '\'' 
                    +'}';
        }

        // функция лексического анализа
        public static List<Lexeme> lexAnalyze (String expText) {
            List<Lexeme> lexemes = new ArrayList<>();
            int pos = 0;
            while (pos < expText.length()){
                char c = expText.charAt(pos);
                switch (c){
                    case '(':
                        lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                        pos++;
                        continue;
                    case ')':
                        lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                        pos++;
                        continue;
                    case '+':
                        lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                        pos++;
                        continue;
                    case '-':
                        lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                        pos++;
                        continue;
                    case '*':
                        lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                        pos++;
                        continue;
                    case ':':
                        lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                        pos++;
                        continue;
                    default:
                        if (c <= '9' && c >= '0'){
                            StringBuilder sb = new StringBuilder();
                            do {
                                sb.append(c);
                                pos++;// если мы достигли конца строки
                                if (pos >= expText.length()){
                                    break;
                                }
                                c = expText.charAt(pos);
                            } while (c <= '9' && c >= '0');
                            lexemes.add(new Lexeme(LexemeType.NUMBER,sb.toString()));
                        } else {
                            if (c != ' ') {
                                throw new RuntimeException("Unexpected character: " + c);
                            }
                            pos++;
                        }
                }
            }
            lexemes.add(new Lexeme(LexemeType.EOF, ""));
            return lexemes;
        }

        // вспомогательный класс
        public static class LexemeBuffer {
            private int pos;
            public List<Lexeme> lexemes;
            public LexemeBuffer(List<Lexeme> lexemes) {
                this.lexemes = lexemes;}
                public Lexeme next() {
                return lexemes.get(pos++);}
                public void back() {
                pos--;
            }
                public int getPos(){
                return pos;
                }
        }

        // expr: plusminus * EOF;
        // plusMinus: multdiv (('+' | '-') multdiv))* ;
        // plusMinus: multdiv (('+' | '-') multdiv))* ;
        // factor : NUMBER | '(' expr ')' ;

        public static int expr (LexemeBuffer lexemes) {
            Lexeme lexeme = lexemes.next();
            if (lexeme.type == LexemeType.EOF){
                return 0;
            } else {
                lexemes.back();
                return plusMinus(lexemes);
            }
        }
    }

    public static int plusMinus (Lexeme.LexemeBuffer lexemes) {
        int value = multdiv(lexemes);
        while (true){
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static int multdiv (Lexeme.LexemeBuffer lexemes) {
        int value = factor(lexemes);
        while (true){
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    // factor - множитель выражения в скобках

    public static int factor (Lexeme.LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                return Integer.parseInt(lexeme.value);
            case LEFT_BRACKET:
                // функция обрабатывающая выражения, считает из массива лексемвсе, которые относятся к этому подвыражению
                int value = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET){
                    throw new RuntimeException("Unexpected token: " + lexeme.value + "at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value + "at position: " + lexemes.getPos());
        }
    }

    public int multipleInt (int a, int b){
        return a + b;
    }
}
