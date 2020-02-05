package io.kostis.scrabblehelper.resources;

import io.dropwizard.jersey.params.IntParam;
import io.kostis.scrabblehelper.api.Word;
import io.kostis.scrabblehelper.db.WordsDatabase;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Resource class for the {@code /words} path. The only route that is currently
 * implemented is {@code GET /words?letters=a&letters=t&limit=3}.
 */
@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WordsResource {

    private final WordsDatabase wordsDatabase;

    /**
     * Constructor method.
     *
     * @param wordsDatabase the {@link WordsDatabase} this class will use for querying words
     */
    public WordsResource(WordsDatabase wordsDatabase) {
        this.wordsDatabase = wordsDatabase;
    }

    /**
     * The {@code GET /words} route. Accepts the letters that the Scrabble player possesses and
     * returns the words that can be formed using these letters.
     *
     * Example: {@code GET /words?letters=a&letters=t&letters=o&&limit=3}
     *
     * @param letters the list of letters that the Scrabble player possesses. The array in
     *                the REST call should be passed as {@code letters=a&letters=g} and not
     *                as {@code letters=a,g}
     * @param limit the max number of words to be returned
     * @return a list of words that can be formed from the the letters
     */
    @GET
    public List<Word> listWords(@QueryParam("letters") @NotNull List<Character> letters,
                                @QueryParam("limit") IntParam limit) {
        // List elements sometimes appear as null due to some framework limitations
        sanitizeLetters(letters);
        // check that at least one character is passed
        validateLetters(letters);

        List<String> wordStrings;
        if (limit == null) {
            wordStrings = wordsDatabase.getMatchingWords(letters);
        } else {
            wordStrings = wordsDatabase.getMatchingWords(letters, limit.get());
        }

        // transform Strings to Words
        return wordStrings.stream()
                .map(Word::new)
                .collect(Collectors.toList());
    }

    // if some element of the array query param is empty, it is passed as null to the Resource
    // e.g. `letters=a&letters=&letters=c` would appear as ['a', null, 'c'] in the Resource
    private void sanitizeLetters(List<Character> letters) {
        letters.removeIf(Objects::isNull);
    }

    // This method is implemented to overcome framework limitations over the validation
    // of an "array of characters" query param. Unfortunately, if the param is absent in the request,
    // it appears here as an empty list (not null)
    private void validateLetters(List<Character> letters) {
        if (!letters.isEmpty()) {
            return;
        }

        throw new WebApplicationException(
                Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                        .entity("letters list cannot be empty")
                        .build()
        );
    }
}
