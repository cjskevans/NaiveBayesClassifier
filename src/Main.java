import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {

    static double numReviews = 0; //Total number of reviews
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

        //Read reviews

        double prob1,prob2, prob3, prob4;
        String badReview = "bad horrible terrible boring";
        String goodReview = "great fantastic good amazing";
        prob1 = calcProbabilities(posHashMap, goodReview, true);
        prob2 = calcProbabilities(negHashMap, badReview, false);
        //flipped
        prob3 = calcProbabilities(posHashMap, badReview, true);
        prob4 = calcProbabilities(negHashMap, goodReview, false);

        System.out.println("Prob good with good review: " + prob1);
        System.out.println("Prob bad with bad review: " + prob2);
        System.out.println("Prob good with bad review: " + prob3);
        System.out.println("Prob bad with good review: " + prob4);

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
            //Save the name of the review. Ex: "cv666_tok_13320.txt"
            reviewName.add(reviewWords[0]);
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

    static double calcProbabilities(Map<String, Integer> hashMap, String review, boolean isPos) {
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
            wordFreq++;
            prob = wordFreq / (hashMap.size() + generateTypeCount());
            totalProb = totalProb * prob;
        }
        return classProb * totalProb;
    }

    static double generateTypeCount() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.putAll(posHashMap);
        hashMap.putAll(negHashMap);
        return hashMap.size();
    }

    static void testData(String file) {
        ArrayList<String> reviewList = new ArrayList<>();
        String[] reviewWords;
        double wordFreq;
        double prob;
        double posProb = 0;
        double negProb = 0;
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

        for (String s : reviewList) {
            reviewWords = s.split(" ");
            for (String w : reviewWords) {
                if (posHashMap.get(w) == null && negHashMap.get(w) == null) {break;}
                if (posHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = posHashMap.get(w);
                prob = wordFreq / posHashMap.size() + generateTypeCount();
                posProb = posProb * prob;
                if (negHashMap.get(w) == null)
                    wordFreq = 1;
                else
                    wordFreq = negHashMap.get(w);
                prob = wordFreq / negHashMap.size() + generateTypeCount();
                negProb = negProb * prob;
            }
            System.out.println ("PosProb: " + posProb);
            System.out.println ("NegProb: " + negProb);

            if (posProb > negProb) {
                System.out.println("Positive");
            }
            else {
                System.out.println("Negative");
            }
        }
    }
}
