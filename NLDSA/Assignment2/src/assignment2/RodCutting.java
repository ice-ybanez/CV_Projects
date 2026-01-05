package assignment2;

import java.util.LinkedList;

public class RodCutting {
    private final int[] prices;

    public RodCutting(int[] prices)
    {
        this.prices = prices;
    }

    public LinkedList<Integer> best_cuts()
    {
        // iterative dynamic programming approach - BOTTOM-UP
        // no backtracking with or without memoization

        // say we're given a rod of length n... and we want to cut it into pieces to max total selling price
        int n = prices.length;
        int[] maxPrice = new int[n + 1];  // maxPrice[i] = the max price for rod of length i
        int[] bestCut = new int[n + 1];     // bestCut[i] = best cut for length i

        // fill in the two arrays maxPrice[i] and bestCut[i]
        // go through all rod lengths(i) from 1 to n
        // for each length(i), we calc;
        // the best price and best first cut
        for(int i = 1; i <= n; i++){
            // start with min placeholder for best price
            int maxValue = -1;      // BEST PRICE SO FAR for rod length (i)

            // for loop to try all possible first cuts(j) from 1 to rod length(i)
            // quick example; if i = 4, try cutting;
            // 1 meter first then solve 3 remaining
            // 2 meters first then solve 2 remaining, etc .etc
            for (int j = 1; j <= i; j++){

                // prices[j - 1] is the price of first cut length(j)
                // maxPrice[i - j] is best price for remaining rod length [i - j]
                if (maxValue < prices[j - 1] + maxPrice[i - j]){
                    // if total is better than what's found so far, update

                    maxValue = prices[j - 1] + maxPrice[i - j];
                    bestCut[i] = j;     // BEST FIRST CUT

                    // saving new max price for rod length(i) and best first cut(j)
                }
            }
            // finally store best price found for rod length(i)
            maxPrice[i] = maxValue;
        }

        // reconstruct the solution
        LinkedList<Integer> result = new LinkedList<>();    // store best cuts as result in new empty linked list
        // start with full rod length (n)
        int length = n;         // rod of length n

        // as long as there's still some length to rod, keep cutting
        // continue to loop until nothing left
        while(length > 0){
            // look up best cut to make on remaining size of length (bestCut[length])
            // say if bestCut[10] = 2... means best cut on a 10m length rod = 2m
            // add this cut to result list
            result.add(bestCut[length]);

            // reduce remaining length of rod by length of cut made
            length -= bestCut[length];
        }

        return result;
    }

    public static void main(String[] args) {
        int[] prices = {  1, 5, 8, 9, 12, 14, 17, 19, 20, 21 };
        LinkedList<Integer> cuts = new RodCutting(prices).best_cuts();
        System.out.println("The best cuts for a rod of length " + prices.length + "m are");
        int total_price=0;
        for (Integer cut : cuts)
        {
            System.out.println(" - " + cut + "m selling at €"+prices[cut-1]);
            total_price += prices[cut-1];
        }
        System.out.println("The overall price is €"+total_price+".");
    }
}
