package assignment2;

public class DivideAndConquer {

    public static int fibonacci(int n) {
        // TASK 1.A.a
        // calculates and returns the nth Fibonacci number

        if (n == 0)
            return 0;
        if (n == 1)
            return 1;

        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static int search(int[] A, int v)
    {
        // TASK 1.A.b
        /* binary search - takes input sorted array A and value v
         and returns index within array which value is stored or -1 otherwise */

        int low = 0;        // low pointer to show start of array
        int high = A.length - 1;    // high pointer to show end of array

        while (low <= high) {       // while low is less than or equal to high

            int mid = low + (high - low) / 2;       // middle index is computed

            if (A[mid] == v) {          // middle element = v, return mid
                return mid;
            }
            else if (A[mid] < v) {      // if middle element is less than v, search to the right half.
                low = mid + 1;
            }
            else {                      // otherwise, search to the left half.
                high = mid - 1;
            }
        }
        return -1;
    }

    public static void hanoi(int n, char A, char B, char C)
    {
        // TASK 1.A.c
        // towers of hanoi - move discs from rod a to rod c using intermediate rod B
        // a larger disc cannot be put atop a smaller disc

        if (n == 1) {
            System.out.print(A + " -> " + C + ", ");
        }

        else {
            hanoi(n - 1, A, C, B);
            hanoi(1, A, B, C);
            hanoi(n - 1, B, A, C);
        }

    }

    public static void main(String[] args) {
        for (int i=0; i<10; i++) {
            System.out.println(fibonacci(i));
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println(search(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, i));
        }
        System.out.println();
        hanoi(4, 'A', 'B', 'C');
    }
}
