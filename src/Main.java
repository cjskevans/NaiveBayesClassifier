import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {

    static int numReviews = 0; //Total number of reviews
    //List of each review as a string
    static ArrayList<String> trainingReviewList = new ArrayList<>();
    //Name of each review so we can output at the end
    static ArrayList<String> reviewName = new ArrayList<>();
    static Map<String, Integer> posHashMap = new HashMap<>();
    static Map<String, Integer> negHashMap = new HashMap<>();

    //Keep track of how many pos/neg reviews there are
    static int numPosReview = 0;
    static int numNegReview = 0;

    public static void main(String[] args) throws IOException {

        int n = parseInt(args[0]); //Unigram frequency cutoff
        String trainingFile = args[1]; //Training file name
        String testFile = args[2]; //Test file name

        numReviews = readText(trainingFile);
        float posReviewProb = (float) numPosReview / numReviews;
        float negReviewProb = (float) numNegReview / numReviews;

        genUnigramsForDocument();
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
        for (int i = 0; i < trainingReviewList.size(); i++) {
            String[] reviewWords = trainingReviewList.get(i).split(" ");
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
            }
            //Keeping track of how many negative reviews there are
            numNegReview++;
        }
    }

    static float calcProbabilities() {
        float prob = 0;

        return prob;
    }
}
