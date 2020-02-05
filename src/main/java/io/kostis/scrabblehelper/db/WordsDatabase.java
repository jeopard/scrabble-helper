package io.kostis.scrabblehelper.db;

import java.util.List;

/**
 * Interface for Scrabble word databases. Implementations are responsible
 * for querying the DB and returning appropriate words. One implementation
 * is {@link FileWordsDatabase}, but others can include key-value stores,
 * SQL DBs, etc.
 */
public interface WordsDatabase {

    /**
     * Returns all the words in the DB that can be composed out of the
     * list of `letters` that is provided.
     *
     * @param letters the list of letters that the returned words can be
     *                composed of
     * @return a list of matching Scrabble words
     */
    List<String> getMatchingWords(List<Character> letters);

    /**
     * Returns the words in the DB that can be composed out of the
     * list of `letters` that is provided. Maximum number of words
     * to be returned is limited by `limit`
     *
     * @param letters the list of letters that the returned words can be
     *                composed of
     * @param limit Maximum number of words to be returned
     * @return a list of matching Scrabble words
     */
    List<String> getMatchingWords(List<Character> letters, int limit);
}
