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
    static double TYPES;
    //List of each review as a string
    static ArrayList<String> trainingReviewList = new ArrayList<>();
    //Name of each review so we can output at the end
    static ArrayList<String> reviewName = new ArrayList<>();
    static Map<String, Integer> posHashMap = new HashMap<>();
    static Map<String, Integer> negHashMap = new HashMap<>();

    //Keep track of how many pos/neg reviews there are
    static double numPosReview = 0;
    static double numNegReview = 0;
    //Saves the probability of a review being pos/neg
    static double posReviewProb = 0;
    static double negReviewProb = 0;

    public static void main(String[] args) throws IOException {
        int n = parseInt(args[0]); //Unigram frequency cutoff
        String trainingFile = args[1]; //Training file name
        String testFile = args[2]; //Test file name

        numReviews = readText(trainingFile);

        genUnigramsForDocument();

        posReviewProb = numPosReview / numReviews;
        negReviewProb = numNegReview / numReviews;

        double prob1, prob2;
        String badReview = "bad horrible terrible boring";
        String goodReview = "great fantastic good amazing";
        prob1 = trainBayes(posHashMap, goodReview, true);
        prob2 = trainBayes(negHashMap, badReview, false);

        testData(testFile);

    }

    static int readText(String file) {
        try {
            Scanner scanner = new Scanner(new File("C:\\Users\\cjske\\IdeaProjects\\PA 3\\" + file));
            while (scanner.hasNextLine()) {
                numReviews++;
                trainingReviewList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return trainingReviewList.size();
    }

    static void genUnigramsForDocument() {
        for (String s : trainingReviewList) {
            String[] reviewWords = s.split(" ");
            if (Integer.parseInt(reviewWords[1]) == 1) {
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
            } else {
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

    static double trainBayes(Map<String, Integer> hashMap, String review, boolean isPos) {
        double classProb;
        double totalProb = 1;
        double prob = 0;
        double wordFreq = 0;
        if (isPos)
            classProb = posReviewProb;
        else
            classProb = negReviewProb;

        String[] reviewWords = review.split(" ");

        for (String w : reviewWords) {
            wordFreq = hashMap.get(w);
            prob = wordFreq + 1 / (hashMap.size() + TYPES);
            totalProb = totalProb * prob;
        }
        return classProb * totalProb;
    }

    static void generateTypeCount() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.putAll(posHashMap);
        hashMap.putAll(negHashMap);
        TYPES = hashMap.size();
    }

    static void testData(String file) throws IOException {
        ArrayList<String> reviewList = new ArrayList<>();
        ArrayList<Double> posProbList = new ArrayList<>();
        ArrayList<Double> negProbList = new ArrayList<>();

        String[] reviewWords;
        double wordFreq, prob, posProb, negProb;

        try {
            Scanner scanner = new Scanner(new File("C:\\Users\\cjske\\IdeaProjects\\PA 3\\" + file));
            while (scanner.hasNextLine()) {
                numReviews++;
                reviewList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < reviewList.size(); j++) {
            String s = reviewList.get(j);
            posProb = 0;
            negProb = 0;
            reviewWords = s.split(" ");
            //Save the name of the review. Ex: "cv666_tok_13320.txt"
            reviewName.add(reviewWords[0]);

            for (int i = 2; i < reviewWords.length; i++) {
                String w = reviewWords[i];
                if (posHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = posHashMap.get(w);
                prob = log(wordFreq / (posHashMap.size() + TYPES));
                posProb = posProb + prob;

                if (negHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = negHashMap.get(w);
                prob = log(wordFreq / (negHashMap.size() + TYPES));
                negProb = negProb + prob;
            }

            posProb = posProb + log(posReviewProb);
            negProb = negProb + log(negReviewProb);
            posProbList.add(posProb);
            negProbList.add(negProb);

            if (posProb > negProb)
                System.out.println("Positive");
            else
                System.out.println("Negative");

        }
        genResults(posProbList, negProbList);
    }

    //This function outputs the probabilities for each review to the answers file
    static void genResults(ArrayList<Double> posProbList, ArrayList<Double> negProbList) throws IOException {
        FileWriter myWriter = new FileWriter("naive-bayes-answers.txt");
        try {
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
