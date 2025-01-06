package br.ufma.ecp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testSimpleClass() {
        String input = """
            class Main {
                function void main() {
                    do Output.printString("Hello, world");
                    return;
                }
            }
            """;

        Parser parser = new Parser(input.getBytes());
        parser.parse();
        String output = parser.VMOutput();

        assertTrue(output.contains("function Main.main 0"));
        assertTrue(output.contains("call Output.printString 1"));
        assertTrue(output.contains("return"));
    }

    @Test
    public void testLetStatement() {
        String input = """
            class Main {
                function void main() {
                    var int x;
                    let x = 5;
                    return;
                }
            }
            """;

        Parser parser = new Parser(input.getBytes());
        parser.parse();
        String output = parser.VMOutput();

        assertTrue(output.contains("push constant 5"));
        assertTrue(output.contains("pop local 0"));
    }
}
