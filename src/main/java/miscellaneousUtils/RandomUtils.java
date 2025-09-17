package miscellaneousUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class holds random functions
 * @author Yael Rozenfeld
 * @since 28.12.2022
 */
@SuppressWarnings("unused")
public class RandomUtils {

    private static Random random;

    /**
     * Get random Integer with exclude values
     * @param rnd - instance of Random
     * @param start - int to start random range
     * @param end - int to end random range
     * @param exclude - List<Integer> contains values to exclude
     * @return random number
     * @author Yael Rozenfeld
     * @since 28.12.2022
     */
    public static int getRandomIntWithExclusion(Random rnd, int start, int end, List<Integer> exclude ) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.size());
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    /**
     * Get a random number
     * @param upperbound the upper bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static int getRandomNumber(int upperbound)
    {
        random = new Random();
        return random.nextInt(upperbound);
    }

    /**
     * Get a random number
     * @param upperbound the upper bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 05.11.2024
     */
    public static double getRandomNumber(double upperbound)
    {
        random = new Random();
        return random.nextDouble(upperbound);
    }

    /**
     * Get a random number
     * @param upperbound the upper bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 05.11.2024
     */
    public static float getRandomNumber(float upperbound)
    {
        random = new Random();
        return random.nextFloat(upperbound);
    }

    /**
     * create a random number by the given length
     * For example: if length is 2 the returned random number will be between 10 and 99
     * @param length length of number
     * @return a random number by the given length
     * @author ghawi.rami
     * @since 19.07.2023
     */
    public static int getRandomNumberByLength(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Get a random number between 2 given int numbers
     * @param upperBound the upper bound for the random number
     * @param lowerBound the lower bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static int getRandomNumber(int lowerBound, int upperBound)
    {
        return (int) (Math.random() * (upperBound - lowerBound)) + lowerBound;
    }

    /**
     * Get a random number between 2 given double numbers
     * @param upperBound the upper bound for the random number
     * @param lowerBound the lower bound for the random number
     * @return a random number as double
     * @author genosar.dafna
     * @since 05.11.2024
     */
    public static double getRandomNumber(double lowerBound, double upperBound) {
        random = new Random();
        return lowerBound + random.nextDouble() * (upperBound - lowerBound);
    }

    /**
     * Get a random number between 2 given float numbers
     * @param upperBound the upper bound for the random number
     * @param lowerBound the lower bound for the random number
     * @return a random number as float
     * @author genosar.dafna
     * @since 05.11.2024
     */
    public static float getRandomNumber(float lowerBound, float upperBound) {
        random = new Random();
        return lowerBound + random.nextFloat() * (upperBound - lowerBound);
    }

    /**
     * create a random string composed of letters
     * @param length length of wanted string
     * @param alphabet alphabet to generate from
     * @return return generated string
     * @author ghawi.rami
     * @since 19.07.2023
     */
    public static String getRandomAlphabetString(int length, String alphabet) {
        random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /** Get X random indexes from 0 to length
     *
     * @param length - length of the possible indexes (for example - valuesList.size() )
     * @param x - number of random indexes to select
     * @return list of the x random indexes
     * @author umflat.lior and ChatGPT
     * @since 1.1.2024
     */
    public static List<Integer> selectRandomIndexes(int length, int x) {
        if (x > length) {
            throw new IllegalArgumentException("Number of indexes to select (x) cannot be greater than the length.");
        }

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            indexes.add(i);
        }

        Collections.shuffle(indexes);

        return indexes.subList(0, x);
    }
}
