package plugin.WordFlake;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static plugin.WordFlake.WordFlake.getFilteredMessage;

class WordFlakeTest {

    public static final Map<String, String> VALUES = Map.of(
            "fork", "flock",
            "cheat", "sheet",
            "count", "cutie",
            "dock", "ding",
            "ice", "ace",
            "=", "<"
    );

    @Test
    void replaceSimpleSingleWord() {
        String originalMessage = "I am going to eat dinner with my fork tonight";
        assertEquals("I am going to eat dinner with my flock tonight", getFilteredMessage(originalMessage, VALUES));
    }

    @Test
    void replaceMultipleWordsInMessage() {
        String originalMessage = "fork fork fork fork";
        String expected = "flock flock flock flock";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }

    @Test
    void replaceMultipleWordsInMessageNoSpace() {
        String originalMessage = "forkforkforkfork";
        String expected = "flockflockflockflock";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }

    @Test
    void replaceSingleLetterCharacter() {
        String originalMessage = "wow=no";
        String expected = "wow<no";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }

    @Test
    void replaceSingleLetterMessage() {
        String originalMessage = "=";
        String expected = "<";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }
}