# ScrabbleHelper

Introduction
---
The purpose of this exercise is to build a simple RESTful webservice in Java using the Dropwizard framework, 
which aims to help people playing Scrabble (https://en.wikipedia.org/wiki/Scrabble) by finding existing words.
The web service provides a single endpoint that takes a list of letters as query parameters and returns existing words to the end user.

Example
```
GET /words?letters=a&letters=b&letters=e&letters=y&limit=5

200 OK
[{"text":"ab"},{"text":"be"},{"text":"by"},{"text":"aye"},{"text":"bay"}]
```


Requirements
---

This project was developed with:

- Apache Maven 3.5.4
- Java 1.8


How to start the ScrabbleHelper application
---

1. Run `mvn clean install` to build the application
1. Start the application with `java -jar target/scrabble-helper-1.0-SNAPSHOT.jar server config.yml`
1. To check that the application is running enter url `http://localhost:8080`. You'll get a 404 back :)


Sample calls with cURL
---
```
curl -v "http://localhost:8080/words?letters=a&letters=b"

# more letters
curl -v "http://localhost:8080/words?letters=g&letters=b&letters=r&letters=t&letters=e&letters=y"

# limit: 1
curl -v "http://localhost:8080/words?letters=g&letters=b&letters=r&letters=t&letters=e&letters=y&limit=1"

# limit: 0
curl -v "http://localhost:8080/words?letters=g&letters=b&letters=r&letters=t&letters=e&letters=y&limit=0"
```


Health Check
---
To see the application's health enter url `http://localhost:8081/healthcheck`


Project structure
---
This project has the standard structure of a Dropwizard project. Some unnecessary Dropwizard features (such as healthchecks) were removed.

- `.gitignore`: files that git should ignore
- `README.md`: this README file
- `config.yml`: configuration for our service. Currently, it only contains the filename of the DB and some logging config
- `pom.xml`: XML config for Maven
- `src/main/`: the code and resources of the project
    - `java/io/kostis/scrabblehelper/`: the Java code
        - `api/`: directory for Representation classes. Used by Jackson to serialize to/deserialize from JSON
            - `Word.java`: the `Word` Representation class. Useful for serializing the Scrabble words into JSON
        - `db/`: database-related code
            - `FileWordsDatabase.java`: implementation of a `WordsDatabase` (see below). It loads the Scrabble words from a file
            - `WordsDatabase.java`: interface that defines how a database of Scrabble words should behave. Useful when we need to switch between different implementations (e.g. file, Redis, SQL)
        - `resources/`: Jersey resources that are plugged into our server and define our API
            - `WordsResource.java`: `Resource` class for the `/words` path
    - `ScrabbleHelperApplication.java`: the `Application` class responsible for initializing the resources and binding them to the Jersey server
    - `ScrabbleHelperConfiguration.java`: the `Configuration` class
    - `resources`: static resources
        - `banner.txt`: banner that gets displayed in the console when booting the server (Dropwizard likes banners)
        - `sanitized_dictionary.txt`: a plaintext file containing the dictionary with the words (separated by newline chars) that are permitted in our Scrabble version


Dictionary
---
The dictionary used for the source of the Scrabble words was from a version of the Oxford dictionary taken from [here](https://sites.google.com/a/vhhscougars.org/johnsearch/searchindex/oxford-english-dictionary). A Ruby script developed by the author was applied to remove non-Scrabble words, e.g. prefixes, abbreviations, words with unicode letters, symbols. The dictionary could be further improved, but as this was not the point of this exercise, little time was spent on sanitizing it. 


Querying algorithm
---
The algorithm used for querying the list of words extracted from the dictionary is a naive and very simple algorithm. In pseudocode:

```
method getMatchingWords(givenLetters, limit) {
  results = []
  
  for word in dictionary
    if results.size() == limit
      break
      
    wordCopy = word.copy()
    
    for letter in givenLetters
      wordCopy.removeSingleChar(letter)
      
    if wordCopy.isEmpty()
      results.add(word)
      
  return results
}
```

If `n` is the size of the dictionary and `m` is the count of letters that are passed, the complexity of this algorithm is O(n*m), which is quite high. However, given that the dictionary contains ~25k words, and the letters that a Scrabble player possesses are usually less than 10 (as far as I remember :)), the server will process all requests really fast without any implications on the CPU. 

Another more efficient algorithm that could be implemented would be sorting of the letters in the given letter list and for each word in the dictionary. A tree stucture would be used to store the dictionary and only the right paths would be followed instead of the whole dictionary. This would require a couple more Java classes to store the tree and nodes, and some more memory space while running the app. The performance gains from this would be minimal, but still, an interesting exercise :) 
