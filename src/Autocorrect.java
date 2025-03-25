import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author YOUR NAME HERE
 */

public class Autocorrect {

    private String[] words;
    private int threshold;

    public static void main(String[] args) {
        // Load in the dictionary and store it in an array
        String[] words = loadDictionary("large");
        // Create an array of HashSets which store token data for each word in dictionary
        HashSet<String>[] dictionaryTokens = new HashSet[words.length];
        // Initialize HashSets
        for (int i = 0; i < dictionaryTokens.length; i++) {
            dictionaryTokens[i] = new HashSet<>();
        }
        // Loop through dictionary storing tokens for each word
        for (int i = 0; i < words.length; i++) {
            // Loop through each word recording each group of 2 characters
            for (int j = 0; j < words[i].length() - 1; j++) {
                dictionaryTokens[i].add(words[i].substring(j, j + 2));
            }
        }
        // Construct and autocorrect class with the dictionary and setting the threshold to 2
        Autocorrect autocorrect = new Autocorrect(words, 2);
        // Scanner to read user inputs
        Scanner s = new Scanner(System.in);
        // Main UI loop
        while(true){
            // Prompt user & record response
            System.out.print("Enter a word: ");
            String typed = s.nextLine().toLowerCase();
            ArrayList<String> toReturn = new ArrayList<>();
            ArrayList<String> typedTokens = new ArrayList<>();
            ArrayList<String> filteredWords = new ArrayList<>();
            // Find all two-letter tokens in typed
            for (int i = 0; i < typed.length() - 1; i++) {
                typedTokens.add(typed.substring(i, i + 2));
            }
            // Search the dictionary for similar words by matching tokens
            for(int i = 0; i < words.length; i++) {
                // Ensure that the word isn't too large or small to ever be within the threshold
                if(Math.abs(words[i].length() - typed.length()) <= autocorrect.threshold) {
                    int matchingTokens = 0;
                    // Count all matching tokens between the typed word and each word in the dictionary
                    for (int j = 0; j < typedTokens.size(); j++) {
                        if (dictionaryTokens[i].contains(typedTokens.get(j))) {
                            matchingTokens++;
                        }
                    }
                    // Token matching threshold scales with the size of the word
                    if (matchingTokens >= typed.length() / 2) {
                        filteredWords.add(words[i]);
                    }
                }
            }

            // Now check from the list of filtered words for words that are within the threshold
            for (int i = 0; i < filteredWords.size(); i++) {
                if(autocorrect.ed(typed, filteredWords.get(i)) <= autocorrect.threshold){
                    toReturn.add(filteredWords.get(i));
                }
            }

            // Sort the returned list
            Collections.sort(toReturn, Comparator
                    .comparingInt((String word) -> autocorrect.ed(typed, word)) // Sort by edit distance
                    .thenComparingInt(word -> Math.abs(typed.length() - word.length())) // Then sort by length difference
                    .thenComparing(word -> word)); // If same distance, sort alphabetically

            // If the first word matches typed, then the word is spelled correctly
            if(!toReturn.getFirst().equals(typed)){
                // Otherwise give user options for corrected words
                System.out.println("Typo! Did you mean...");
                // Print options
                for (int i = 0; i < 4; i++) {
                    if(i >= toReturn.size()){
                        break;
                    }
                    System.out.println( (i + 1) + ". " + toReturn.get(i));
                }
                System.out.println("5. None of these");
                int userInput;
                // Prompt the user for an int between 1 and 5, inclusive
                do {
                    do {
                        System.out.print("Please make a selection (1-5): ");
                    } while (!s.hasNextInt());
                    userInput = s.nextInt();
                    s.nextLine();
                }while (userInput > 5 || userInput < 1);
                // Print out their chosen option
                if(userInput != 5){
                    System.out.println("Here's your corrected word: " + toReturn.get(userInput - 1));
                }
            }
            // otherwise notify the user their word is correct
            else{
                System.out.println("That word is spelled correctly!");
            }
            System.out.println("------------");
        }
    }

    /**
     * Constructs an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */

    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
    }

    public int ed(String s1, String s2){
        // Data structure to tabulate solutions
        int[][] tabulation = new int[s1.length() + 1][s2.length() + 1];
        // Adding a space at the beginning of each word provides a base case
        s1 = " " + s1;
        s2 = " " + s2;
        // Initialize first row and column
        for(int i = 0; i < s1.length(); i++) {
            tabulation[i][0] = i;
        }
        for(int i = 1; i < s2.length(); i++) {
            tabulation[0][i] = i;
        }
        // Loop through the table tabulating results
        for (int i = 1; i < s1.length(); i++) {
            for (int j = 1; j < s2.length(); j++) {
                int prev = Math.min(Math.min(tabulation[i][j-1], tabulation[i-1][j]), tabulation[i-1][j-1]);
                // If the characters are equal, don't increment the edit distance
                if(s1.charAt(i) == s2.charAt(j)){
                    tabulation[i][j] = tabulation[i - 1][j - 1];
                }
                // Otherwise increment the edit distance
                else{
                    tabulation[i][j] = prev + 1;
                }
            }
        }
        // Return the last entry
        return tabulation[s1.length() - 1][s2.length() - 1];
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        ArrayList<String> toReturn = new ArrayList<>();
        // Search the provided words for words within the edit distance threshold
        for (int i = 0; i < words.length; i++) {
            if(ed(typed, words[i]) <= threshold){
                toReturn.add(words[i]);
            }
        }
        // Sort the recorded words by edit distance the alphabetically
        Collections.sort(toReturn, Comparator
                .comparingInt((String word) -> ed(typed, word)) // Sort by edit distance
                .thenComparing(word -> word)); // If same distance, sort alphabetically
        return toReturn.toArray(new String[0]);
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}