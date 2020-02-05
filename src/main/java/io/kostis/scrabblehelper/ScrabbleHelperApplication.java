package io.kostis.scrabblehelper;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.kostis.scrabblehelper.db.FileWordsDatabase;
import io.kostis.scrabblehelper.db.WordsDatabase;
import io.kostis.scrabblehelper.resources.WordsResource;

import java.io.FileNotFoundException;

/**
 * The Application class responsible for initializing the resources and binding them to the
 * Jersey server.
 */
public class ScrabbleHelperApplication extends Application<ScrabbleHelperConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ScrabbleHelperApplication().run(args);
    }

    @Override
    public String getName() {
        return "ScrabbleHelper";
    }

    // no need to initialize anything here
    @Override
    public void initialize(final Bootstrap<ScrabbleHelperConfiguration> bootstrap) { }

    // initialize the WordsResource and bind it to Jersey
    @Override
    public void run(final ScrabbleHelperConfiguration configuration,
                    final Environment environment) {
        WordsResource wordsResource = constructWordsResource(configuration);

        environment.jersey().register(wordsResource);
    }

    // initialize the WordsDatabase and pass it to the WordsResource constructor
    private WordsResource constructWordsResource(final ScrabbleHelperConfiguration configuration) {
        WordsDatabase wordsDatabase;

        String wordsDatabaseFilename = configuration.getWordsDatabaseFilename();

        // if filename is incorrect, make the booting of the server fail
        try {
            wordsDatabase = new FileWordsDatabase(wordsDatabaseFilename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Words DB filename is incorrect: " + wordsDatabaseFilename);
        }

        return new WordsResource(wordsDatabase);
    }

}
