package io.kostis.scrabblehelper.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of a WordsDatabase. It loads the words from a file
 * whose path is defined in the class' constructor.
 */
public class FileWordsDatabase implements WordsDatabase {

    // holds the words and their list of characters for easier querying
    private Map<String, List<Character>> wordLetterMap;

    /**
     * Constructor method.
     *
     * @param filename should be of a file containing the words that we want to be queried.
     *                 Words should be separated by newline characters
     * @throws FileNotFoundException if the file cannot be found
     */
    public FileWordsDatabase(String filename) throws FileNotFoundException {
        List<String> wordList = loadWords(filename);

        // build the wordLetterMap that holds the words and
        // their list of characters for easier querying
        wordLetterMap = new HashMap<>();

        for (String word: wordList) {
            char[] letterArray = word.toCharArray();

            List<Character> letterList = new ArrayList<>();
            for (char letter: letterArray) {
                letterList.add(letter);
            }

            wordLetterMap.put(word, letterList);
        }
    }

    // helper method to load the words from the file to a list
    private List<String> loadWords(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        List<String> result = new ArrayList<String>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // avoid empty lines
            if (!line.isEmpty()) {
                result.add(line);
            }
        }

        return result;
    }

    @Override
    public List<String> getMatchingWords(List<Character> givenLetters, int limit) {

        return wordLetterMap.entrySet()
                .stream()
                // we don't care about the order, which can result in performance gains
                .unordered()
                // filter the words that can be composed out of the givenLetters
                .filter(entry -> doesListIncludeList(givenLetters, entry.getValue()))
                // get the String words
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMatchingWords(List<Character> givenLetters) {

        return wordLetterMap.entrySet()
                .stream()
                // we don't care about the order, which can result in performance gains
                .unordered()
                // filter the words that can be composed out of the givenLetters
                .filter(entry -> doesListIncludeList(givenLetters, entry.getValue()))
                // get the String words
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Helper method that checks if `listB` is a sublist of `listA`. Unlike
     * `Collection#containsAll`, this method takes into account duplicates,
     * and returns true only if each element of `listB` matches a different
     * element of `listA`.
     */
    private static <T> boolean doesListIncludeList(List<T> listA, List<T> listB) {
        List<T> listBCopy = new ArrayList<>(listB);

        for (T elementA: listA) {
            listBCopy.remove(elementA);
        }

        return listBCopy.isEmpty();
    }
}
