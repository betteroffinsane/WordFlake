package plugin.WordFlake;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static plugin.WordFlake.WordFlake.getFilteredMessage;

class WordFlakeTest {

    public static final Map<WordFlake.SwearWordPattern, String> VALUES = Map.of(
            "fork", "flock",
            "cheat", "sheet",
            "count", "cutie",
            "dock", "ding",
            "ice", "ace",
            "=", "<"
    ).entrySet()
            .stream()
            .collect(Collectors.toMap(s -> WordFlake.compileRegexPattern(s.getKey()), Map.Entry::getValue));

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

    @Test
    void replaceMiddleOfCapitals() {
        String originalMessage = ".djrFORK.sjnw";
        String expected = ".djrflock.sjnw";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }

    @Test
    void replaceSpacedOut() {
        String originalMessage = "F O R K";
        String expected = "flock";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }

    @Test
    void replaceMixedCapitals() {
        String originalMessage = "fOrK";
        String expected = "flock";
        String actual = getFilteredMessage(originalMessage, VALUES);
        assertEquals(expected, actual);
    }
}