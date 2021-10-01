package a0;

public class Partitions {

    /** Count the number of ways to partition a set of size n into k nonempty subsets.
     *
     * @param n The size of the set
     * @param k The size of the partitions
     * @require 0 <= k && k <= n
     * @ensure result equals the number of ways to partition a set of size n
     */
    public static int countPartitions(int n, int k) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid n value.");
        }
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("Invalid k value.");
        }
        if (n == 0) {
            return 0;
        }
        if (k == 0) {
            return 1;
        }
        if (k == n) {
            return 1;
        }

        return countPartitions(n - 1, k - 1)
                + countPartitions(n - 1, k);
    }

    public static void main(String[] args) {
        try {
            System.out.println("" + countPartitions(4, 4));
            System.out.println("" + countPartitions(4, 3));
            System.out.println("" + countPartitions(4, 2));
            System.out.println("" + countPartitions(4, 1));
            System.out.println("" + countPartitions(30, 15));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
