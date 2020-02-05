package io.kostis.scrabblehelper.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Representation class for Scrabble words. Helps with JSON serializing and Jackson.
 *
 * Could potentially be extended with other attributes, such as the score for each word.
 */
public class Word {

    @NotEmpty
    private String text;

    @JsonCreator
    public Word(String text) {
        this.text = text;
    }

    @JsonProperty
    public String getText() {
        return text;
    }
}
