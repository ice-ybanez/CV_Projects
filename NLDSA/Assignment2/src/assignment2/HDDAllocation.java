package assignment2;

public class HDDAllocation {
    private final int[] hdds;
    private final int[] files;

    public HDDAllocation(int[] hdds, int[] files)
    {
        this.hdds = hdds;
        this.files = files;
    }

    public int[] generate_allocation() {

        int[] allocation = new int[files.length];   // which HDD each file is ALLOCATED to - files.length
        int[] spaceUsed = new int[hdds.length];     // how much SPACE IS USED UP on each HDD - hdds.length

        if(backtrackingAlgo(0, allocation, spaceUsed)){     // if valid allocation for all files is found,
            return allocation;      // return which HDD each file is allocated to
        }
        else {      // otherwise, throw exception.
            throw new RuntimeException("No valid allocation found.");
        }
    }

    // takes as parameters the file index, allocation of file and the space used up on hdd
    public boolean backtrackingAlgo(int fileIDX, int[] allocation, int[] spaceUsed){
        if (fileIDX == files.length){       // base case... if allocated all files, end recursion.
            return true;    // so that files are allocated properly.
        }

        // iterate through hdds to see where files will be allocated
        for(int hdd = 0; hdd < hdds.length; hdd++){
            // checking if file fits
            // if hdd's used space + size of current file we want to allocate is less than or equal to the size of hdd
            if(spaceUsed[hdd] + files[fileIDX] <= hdds[hdd]){
                // allocate current file(at file index fileIDX) to hdd
                allocation[fileIDX] = hdd;

                // update the space used up on hdd after allocation of current file
                spaceUsed[hdd] += files[fileIDX];

                // move to next file by recursively calling the backtracking algorithm method
                if(backtrackingAlgo(fileIDX + 1, allocation, spaceUsed)){
                    return true;    // the path was found and to continue
                }

                // backtrack at this point by "undoing" last assignment of current file to hdd
                // removing the file's size from the hdd's used space
                spaceUsed[hdd] -= files[fileIDX];
            }
        }
        return false;   // nowhere for current file to go that's valid
    }

    public static void main(String[] args) {
        int[] hdds = {1000, 1000, 2000};
        int[] files = {300, 200, 300, 1200, 400, 700, 700};
        int[] allocation = new HDDAllocation(hdds, files).generate_allocation();
        for (int i=0; i<allocation.length; i++) {
            System.out.println("File "+i+" has size " + files[i] + "MB and goes on HDD"+allocation[i] + ".");
        }
        for (int j=0; j<hdds.length; j++)
        {
            int space_used = 0;
            for (int i=0; i<allocation.length; i++) {
                if (allocation[i]==j) {
                    space_used += files[i];
                }
            }
            System.out.println("HDD"+ j + " space used " + space_used + "MB / " + hdds[j] + "MB.");
        }
    }
}
