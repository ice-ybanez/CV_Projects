package assignment2;

public class LongestCommonSubsequence {
    private final String X;
    private final String Y;

    public LongestCommonSubsequence(String X, String Y) {
        this.X = X;
        this.Y = Y;
    }

    public String compare() {
        int m = X.length();     // (m) is length of string (X)
        int n = Y.length();     // (n) is length of string (Y)

        // 2D table of size (m+1)*(n+1) where table[i][j] stores length of LCS of X[0...i-1] and Y[0..j-1]
        // using (m+1) and (n+1) to include empty string /0 cases in first row and column
        int[][] table = new int[m + 1][n + 1];

        // fill table
        // loop over each character of X and Y
        for (int i = 1; i <= m; i++) {      // i = current position in string X
            for (int j = 1; j <= n; j++) {      // j = current position in string Y
                // if chars match X[i-1] == Y[j-1]
                if (X.charAt(i - 1) == Y.charAt(j - 1)) {   // table indices start 1 but string starts at 0
                    // example if i = 1, X.charAt(0) = 'A'

                    // extend found LCS by 1 via
                    table[i][j] = table[i - 1][j - 1] + 1;
                }
                // if chars don't match, take longer LCS from skipping char from
                // X which is table[i - 1][j] or from
                // Y which is table[i][j - 1]
                else {
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
                }
            }
        }

        // reconstruct LCS from table
        StringBuilder LCS = new StringBuilder();
        int i = m, j = n;       // because starting at bottom right
        while (i > 0 && j > 0) {        // while i and j are bigger than 0 - empty string
            if (X.charAt(i - 1) == Y.charAt(j - 1)) {   // if characters match,
                LCS.append(X.charAt(i - 1));    // it's part of the LCS... add to result
                i--;    // moving - up
                j--;    // diagonally - left
            }
            // if not, we move in direction of greater value
            else if (table[i - 1][j] >= table[i][j - 1]) {  // did >= instead of just > and it changed the value of Z
                // >= moves up first whereas > moves left first
                i--; // move up in the table
            }
            else {
                j--; // move left in the table
            }
        }
        // string is constructed in reverse order, so reverse it
        return LCS.reverse().toString();
    }

    public static void main(String[] args) {
        String X = "ABCBDAB";
        String Y = "BDCABA";
        String Z = new LongestCommonSubsequence(X, Y).compare();
        System.out.println("The longest common subsequence of '" + X + "' and '" + Y + "' is '" + Z + "'.");
    }
}
