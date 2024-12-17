package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Parser {
    private Scanner scan;
    private Token currentToken;
    private StringBuilder xmlOutput;

    public Parser(byte[] input) {
        scan = new Scanner(input);
        currentToken = scan.nextToken();
        xmlOutput = new StringBuilder();
    }

    public void parse() {
        parseClass();
    }

    void parseClass() {
        xmlOutput.append("<class>\n");
        match(TokenType.CLASS);
        xmlOutput.append(tokenToXML());
        match(TokenType.IDENT); // nomeDaClasse
        xmlOutput.append(tokenToXML());
        match(TokenType.LBRACE); // {
        xmlOutput.append(tokenToXML());

        while (currentToken.type == TokenType.STATIC || currentToken.type == TokenType.FIELD) {
            parseClassVarDec();
        }

        while (currentToken.type == TokenType.CONSTRUCTOR || currentToken.type == TokenType.FUNCTION || currentToken.type == TokenType.METHOD) {
            parseSubroutine();
        }

        match(TokenType.RBRACE); // }
        xmlOutput.append(tokenToXML());
        xmlOutput.append("</class>\n");
    }

    void parseClassVarDec() {
        xmlOutput.append("<classVarDec>\n");
        match(currentToken.type); // static ou field
        xmlOutput.append(tokenToXML());
        parseType();
        match(TokenType.IDENT); // nomeDaVariavel
        xmlOutput.append(tokenToXML());

        while (currentToken.type == TokenType.COMMA) {
            match(TokenType.COMMA);
            xmlOutput.append(tokenToXML());
            match(TokenType.IDENT);
            xmlOutput.append(tokenToXML());
        }

        match(TokenType.SEMICOLON); // ;
        xmlOutput.append(tokenToXML());
        xmlOutput.append("</classVarDec>\n");
    }

    void parseSubroutine() {
        xmlOutput.append("<subroutineDec>\n");
        match(currentToken.type); // constructor, function, method
        xmlOutput.append(tokenToXML());
        if (currentToken.type == TokenType.VOID) {
            match(TokenType.VOID);
        } else {
            parseType();
        }
        xmlOutput.append(tokenToXML());
        match(TokenType.IDENT); // nomeDaSubRotina
        xmlOutput.append(tokenToXML());
        match(TokenType.LPAREN); // (
        xmlOutput.append(tokenToXML());
        parseParameterList();
        match(TokenType.RPAREN); // )
        xmlOutput.append(tokenToXML());
        parseSubroutineBody();
        xmlOutput.append("</subroutineDec>\n");
    }

    void parseParameterList() {
        xmlOutput.append("<parameterList>\n");
        if (currentToken.type == TokenType.INT || currentToken.type == TokenType.CHAR || currentToken.type == TokenType.BOOLEAN || currentToken.type == TokenType.IDENT) {
            parseType();
            match(TokenType.IDENT);
            xmlOutput.append(tokenToXML());
            while (currentToken.type == TokenType.COMMA) {
                match(TokenType.COMMA);
                xmlOutput.append(tokenToXML());
                parseType();
                match(TokenType.IDENT);
                xmlOutput.append(tokenToXML());
            }
        }
        xmlOutput.append("</parameterList>\n");
    }

    void parseSubroutineBody() {
        xmlOutput.append("<subroutineBody>\n");
        match(TokenType.LBRACE); // {
        xmlOutput.append(tokenToXML());
        while (currentToken.type == TokenType.VAR) {
            parseVarDec();
        }
        parseStatements();
        match(TokenType.RBRACE); // }
        xmlOutput.append(tokenToXML());
        xmlOutput.append("</subroutineBody>\n");
    }

    void parseVarDec() {
        xmlOutput.append("<varDec>\n");
        match(TokenType.VAR);
        xmlOutput.append(tokenToXML());
        parseType();
        match(TokenType.IDENT);
        xmlOutput.append(tokenToXML());
        while (currentToken.type == TokenType.COMMA) {
            match(TokenType.COMMA);
            xmlOutput.append(tokenToXML());
            match(TokenType.IDENT);
            xmlOutput.append(tokenToXML());
        }
        match(TokenType.SEMICOLON);
        xmlOutput.append(tokenToXML());
        xmlOutput.append("</varDec>\n");
    }

    void parseStatements() {
        xmlOutput.append("<statements>\n");
        while (isStatement()) {
            if (currentToken.type == TokenType.LET) {
                parseLet();
            } else if (currentToken.type == TokenType.IF) {
                parseIf();
            } else if (currentToken.type == TokenType.WHILE) {
                parseWhile();
            } else if (currentToken.type == TokenType.DO) {
                parseDo();
            } else if (currentToken.type == TokenType.RETURN) {
                parseReturn();
            }
        }
        xmlOutput.append("</statements>\n");
    }

    private void parseLet() {
        xmlOutput.append("<letStatement>\n");

        match(TokenType.LET); // Consome "let"
        xmlOutput.append(tokenToXML());

        match(TokenType.IDENT); // Consome nomeDaVariavel
        xmlOutput.append(tokenToXML());

        if (currentToken.type == TokenType.LBRACKET) { // Se houver um indice, temos um array
            match(TokenType.LBRACKET); // Consome "["
            xmlOutput.append(tokenToXML());
            parseExpression(); // Analise da expressao do indice
            match(TokenType.RBRACKET); // Consome "]"
            xmlOutput.append(tokenToXML());
        }

        match(TokenType.EQ); // Consome "="
        xmlOutput.append(tokenToXML());

        parseExpression(); // Análise da expressao a direita do "=", que é o valor a ser atribuido.

        match(TokenType.SEMICOLON); // Consome ";"
        xmlOutput.append(tokenToXML());

        xmlOutput.append("</letStatement>\n");
    }

    private void parseIf() {
        xmlOutput.append("<ifStatement>\n");

        match(TokenType.IF); // Consome "if"
        xmlOutput.append(tokenToXML());

        match(TokenType.LPAREN); // Consome "("
        xmlOutput.append(tokenToXML());

        parseExpression(); // Análise da expressão da condição

        match(TokenType.RPAREN); // Consome ")"
        xmlOutput.append(tokenToXML());

        match(TokenType.LBRACE); // Consome "{"
        xmlOutput.append(tokenToXML());

        parseStatements(); // Analise das declaracoes dentro do bloco "if"

        match(TokenType.RBRACE); // Consome "}"
        xmlOutput.append(tokenToXML());

        if (currentToken.type == TokenType.ELSE) {
            match(TokenType.ELSE); // Consome "else"
            xmlOutput.append(tokenToXML());

            match(TokenType.LBRACE); // Consome "{"
            xmlOutput.append(tokenToXML());

            parseStatements(); // Análise das declaracoes dentro do bloco "else"

            match(TokenType.RBRACE); // Consome "}"
            xmlOutput.append(tokenToXML());
        }

        xmlOutput.append("</ifStatement>\n");
    }

    private void parseExpression() {
    }

    private void parseWhile() {
        xmlOutput.append("<whileStatement>\n");

        match(TokenType.WHILE); // Consome "while"
        xmlOutput.append(tokenToXML());

        match(TokenType.LPAREN); // Consome "("
        xmlOutput.append(tokenToXML());

        parseReturn(); // Análise da expressao (condicao)

        match(TokenType.RPAREN); // Consome ")"
        xmlOutput.append(tokenToXML());

        parseStatements(); // Análise do corpo do while

        xmlOutput.append("</whileStatement>\n");
    }

    private void parseDo() {
        xmlOutput.append("<doStatement>\n");

        match(TokenType.DO); // Consome "do"
        xmlOutput.append(tokenToXML());

        parseSubroutine(); // Analise da chamada de sub-rotina

        match(TokenType.SEMICOLON); // Consome ";"
        xmlOutput.append(tokenToXML());

        xmlOutput.append("</doStatement>\n");
    }

    private void parseReturn() {
        xmlOutput.append("<returnStatement>\n");

        match(TokenType.RETURN); // Consome "return"
        xmlOutput.append(tokenToXML());

        if (currentToken.type != TokenType.SEMICOLON) {
            parseReturn(); // Análise da expressão de retorno, caso haja uma
        }

        match(TokenType.SEMICOLON); // Consome ";"
        xmlOutput.append(tokenToXML());

        xmlOutput.append("</returnStatement>\n");
    }

    private boolean isStatement() {
        return currentToken.type == TokenType.LET || currentToken.type == TokenType.IF || currentToken.type == TokenType.WHILE || currentToken.type == TokenType.DO || currentToken.type == TokenType.RETURN;
    }

    private void parseType() {
        if (currentToken.type == TokenType.INT || currentToken.type == TokenType.CHAR || currentToken.type == TokenType.BOOLEAN) {
            match(currentToken.type);
        } else {
            match(TokenType.IDENT); // nomeDaClasse
        }
    }

    private void match(TokenType t) {
        if (currentToken.type == t) {
            xmlOutput.append(tokenToXML());
            nextToken();
        } else {
            throw new Error("erro de sintaxe: esperado " + t + " mas encontrado " + currentToken.type);
        }
    }

    private String tokenToXML() {
        return currentToken.toString() + "\n";
    }

    private void nextToken() {
        currentToken = scan.nextToken();
    }

    public void writeXML(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(xmlOutput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
