package io.kostis.scrabblehelper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 * Configuration class. Loads the config from the YAML file passes when initializing the server.
 *
 * Only config at the moment is the words database filename.
 */
public class ScrabbleHelperConfiguration extends Configuration {

    private String wordsDatabaseFilename = "word/db/filename";

    @JsonProperty
    public String getWordsDatabaseFilename() {
        return wordsDatabaseFilename;
    }

    @JsonProperty
    public void setWordsDatabaseFilename(String wordsDatabaseFilename) {
        this.wordsDatabaseFilename = wordsDatabaseFilename;
    }
}
