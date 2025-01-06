package br.ufma.ecp.token;

public class Token {
    public final TokenType type;
    private final String value;
    public String lexeme;

    /**
     * Construtor para criar um token com um tipo e valor específicos.
     *
     * @param type  O tipo do token (palavra-chave, símbolo, identificador, etc.).
     * @param value O valor literal do token (como "class", "+", "x", etc.).
     */
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Retorna o tipo do token.
     *
     * @return O tipo do token (enum TokenType).
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Retorna o valor literal do token.
     *
     * @return O valor do token como uma string.
     */
    public String getValue() {
        return value;
    }

    /**
     * Representação em string do token para depuração.
     *
     * @return Uma string no formato "Token(type: VALUE)".
     */
    @Override
    public String toString() {
        return String.format("Token(%s: %s)", type, value);
    }

    /**
     * Método auxiliar para verificar se o token corresponde a um determinado tipo e valor.
     *
     * @param type  O tipo esperado do token.
     * @param value O valor esperado do token.
     * @return Verdadeiro se o token corresponde, falso caso contrário.
     */
    public boolean matches(TokenType type, String value) {
        return this.type == type && this.value.equals(value);
    }
}
