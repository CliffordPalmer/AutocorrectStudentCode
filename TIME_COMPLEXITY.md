Initializing data structures:

  String[] words:
    - Loading the dictionary takes time proportional to the size of the dictionary, so O(n). 
  	HashSet<String>[] dictionaryTokens:
  		- Need to loop through the array and initialize each HashSet, taking O(n) time
  	ArrayLists:
  		- O(n), as each ArrayList is initialized empty
  	int[][] tabulation:
    - 2D array, takes O(m*n) time, as it needs to loop through and initialize each index

Paring down the large dictionary into candidates:

  I begin by finding and storing the tokens of each word in the entire dictionary, anticipating that the user will use this program multiple times, so that I only have to do this process once. Finding the tokens of a word takes O(n) time, where n is the length of the word, as I just need to loop through the word once, incrementing one letter at a time to find all two-letter tokens. I do this process for the entire dictionary, for a total time of O(n*l), where l is the length of the dictionary. I store these tokens in an array of HashSets, which lets me do very quick look-ups later on.

  Once the user enters a word, I find the tokens in the typed word, which only takes O(n) time.

  I then search the HashSets for each word in the dictionary for matching tokens, and record the number of matching tokens for each word. Each HashSet lookup takes only O(1) time. This process is repeated n times for each dictionary word (l words total), as the number of tokens in the typed word is proportional to its length. So the time complexity for this step is O(n*l).

  So overall the process of paring down the larger dictionary takes O(n*l) time.

Tabulation for edit distance:

  Without going into detail about how the tabulation process works, and only focusing on the time complexity, the time it takes to tabulate the edit distance between two words is closely related to the data structure used for this tabulation. The data structure is ultimately a 2D array with rows and columns proportional to the lengths of the two words, n and m. The tabulation process loops through this entire array, building answers off of previous answers, ultimately filling in the entire table. So it takes O(n*m) time.

Overall:

  Ultimately, this tabulation process is repeated for each word in the filtered down list of candidates. If we say this list has a length of x, then the total time complexity for the entire process is O(m*n*x)
