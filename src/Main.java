/*
	This program was written by Charlie Evans and Luke Tangen for an assignment in Natural Language Processing CS [45] 242 due 10/21/2022.
	This program (Naive Bayes Classifier) is one of 2 that will be submitted along with our Naive Bayes Eval. The purpose
	of the project is to go through a text file filled with movie reviews and assign them either positive or negative.
	Our eval will produce the accuracy, precision, recall, number of true positive, false positive, true negative, false negative
	all on separate lines for the user. Our goal was that our accuracy was above 60%
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.Math.log;

public class Main {

    static double numReviews = 0; //Total number of reviews
    static double TYPES; //Total count of all the types in the data
    static int unigramFrequencyCutoff;

    //List of each review as a string
    static ArrayList<String> trainingReviewList = new ArrayList<>();
    //Name of each review so we can output at the end
    static ArrayList<String> reviewName = new ArrayList<>();

    //HashMaps that store the count of each word given the class
    static Map<String, Integer> posHashMap = new HashMap<>();
    static Map<String, Integer> negHashMap = new HashMap<>();

    //Keep track of how many pos/neg reviews there are
    static double numPosReview = 0;
    static double numNegReview = 0;

    //Saves the probability of a review being pos/neg
    static double posReviewProb = 0;
    static double negReviewProb = 0;

    public static void main(String[] args) throws IOException {
        unigramFrequencyCutoff = parseInt(args[0]); //Unigram frequency cutoff
        String trainingFile = args[1]; //Training file name
        String testFile = args[2]; //Test file name

        System.out.println("Using unigram frequency cutoff of value: " + unigramFrequencyCutoff + "\n");

        trainingReviewList = readText(trainingFile);
        numReviews = trainingReviewList.size();

        genUnigramsForDocument();

        posReviewProb = numPosReview / numReviews;
        negReviewProb = numNegReview / numReviews;

        testData(testFile);
    }

    //This file reads the text and saves each line into an ArrayList of Strings that it returns
    static ArrayList<String> readText(String file) {
        ArrayList<String> aList = new ArrayList<>();
        numReviews = 0;
        try {
            Scanner scanner = new Scanner(new File("C:\\Users\\cjske\\IdeaProjects\\PA 3\\" + file));
            while (scanner.hasNextLine()) {
                //We iterate numReviews to keep track of how many reviews there are
                numReviews++;
                aList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Return the list of reviews
        return aList;
    }

    //This is the function that trains the naive bayes classifier.
    //It reads through the training review list and if the review is positive is saves it
    //in the positive hashmap and iterates its count and vice versa for negative.
    static void genUnigramsForDocument() {
        //Iterate through the training review list for each review
        for (String s : trainingReviewList) {
            //Split each review by spaces to get each unigram
            String[] reviewWords = s.split(" ");
            //If the review is positive (the second unigram would be a 1)
            if (Integer.parseInt(reviewWords[1]) == 1) {
                //Iterate through each word in the positive review and add them to hashmap
                //We ignore the first 2 in array because they are file name and pos/neg indicator
                for (int j = 2; j < reviewWords.length; j++) {
                    Integer count = posHashMap.get(reviewWords[j]);
                    if (count == null)
                        posHashMap.put(reviewWords[j], 1);
                    else
                        posHashMap.put(reviewWords[j], count + 1);
                }
                //Keeping track of how many positive reviews there are
                numPosReview++;
            //If the review is negative
            } else {
                //Iterate through each word in the positive review and add them to hashmap
                //We ignore the first 2 in array because they are file name and pos/neg indicator
                for (int j = 2; j < reviewWords.length; j++) {
                    Integer count = negHashMap.get(reviewWords[j]);
                    if (count == null)
                        negHashMap.put(reviewWords[j], 1);
                    else
                        negHashMap.put(reviewWords[j], count + 1);
                }
                //Keeping track of how many negative reviews there are
                numNegReview++;
            }
        }
    }

    //This function calculates the total types in all the reviews
    //by combining the positive and negative hashmap and returning its size
    static void generateTypeCount() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.putAll(posHashMap);
        hashMap.putAll(negHashMap);
        TYPES = hashMap.size();
    }

    //This function tests the naive bayes classifier by implementing the naive bayes algorithm
    static void testData(String file) throws IOException {
        //This is a list of all the reviews in the test file
        ArrayList<String> reviewList = readText(file);
        //These lists are used to save the probability for each review being positive/negative
        //so it can be printed out
        ArrayList<Double> posProbList = new ArrayList<>();
        ArrayList<Double> negProbList = new ArrayList<>();

        String[] reviewWords; //Array this is used to save each word in the review
        double wordFreq, prob, posProb, negProb;
        //Iterate through each review
        for (int j = 0; j < reviewList.size(); j++) {
            String s = reviewList.get(j);
            posProb = 0;
            negProb = 0;
            //Split the review into unigrams
            reviewWords = s.split(" ");
            //Save the name of the review. Ex: "cv666_tok_13320.txt"
            reviewName.add(reviewWords[0]);
            //Iterate through each unigram for that specific review, skipping the
            //first two unigrams that are useless data
            for (int i = 2; i < reviewWords.length; i++) {
                String w = reviewWords[i];
                //If the word is not in the positive hashmap we smooth it +1
                if (posHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = posHashMap.get(w);
                //Used for unigram frequency cutoff
                if (wordFreq >= unigramFrequencyCutoff) {
                    //Actual algorithm itself, we take the log of it to prevent underflow
                    prob = log(wordFreq / (posHashMap.size() + TYPES));
                    //Add it to the previus sum
                    posProb = posProb + prob;
                }

                //If the word is not in the negative hashmap we smooth it +1
                if (negHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = negHashMap.get(w);
                //Used for unigram frequency cutoff
                if (wordFreq >= unigramFrequencyCutoff) {
                    //Actual algorithm itself, we take the log of it to prevent underflow
                    prob = log(wordFreq / (negHashMap.size() + TYPES));
                    //Add it to the previus sum
                    negProb = negProb + prob;
                }
            }
            //We have now finished iterating through that review
            //and we add the log of the probability that the review is in
            //that class
            posProb = posProb + log(posReviewProb);
            negProb = negProb + log(negReviewProb);
            //We then save that value and the final probabilities for that review and move on to the next review
            posProbList.add(posProb);
            negProbList.add(negProb);
        }
        //We have read through all the reviews and we send the probabilities to be outputted to text file
        genResults(posProbList, negProbList);
    }

    //This function outputs the probabilities for each review to the answers file
    static void genResults(ArrayList<Double> posProbList, ArrayList<Double> negProbList) throws IOException {
        FileWriter myWriter = new FileWriter("naive-bayes-answers.txt");
        try {
            //Print each line, starting at review 0, and then go to the next line
            for(int i = 0; i < reviewName.size(); i++) {
                //Print review name
                myWriter.write(reviewName.get(i) + " ");
                //Print the probability for it being positive
                myWriter.write(posProbList.get(i) + " ");
                //Print the probability for it being negative
                myWriter.write(negProbList.get(i) + " ");
                //Print whether it is positive or negative
                if (posProbList.get(i) > negProbList.get(i))
                    myWriter.write("1\n");
                else
                    myWriter.write("0\n");
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
