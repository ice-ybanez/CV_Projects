package assignment2;

import java.util.LinkedList;

public class Greedy {

    public static LinkedList<Activity> activitySelection(LinkedList<Activity> activities) {
        // TASK 1.B.a
        // activity selection problem tries to find max num of non overlapping activities given start n finish times
        /* takes as input a list of activities and should produce as output a maximum list of non overlapping
        activities selected from the input */
        // determine if 2 activities overlap using Activity class

        LinkedList<Activity> selectedActivities = new LinkedList<>();

        if (activities == null || activities.isEmpty()) {
            return selectedActivities;      // return an empty list if no activities are given
        }

        // select first activity
        Activity lastSelected = activities.get(0);
        selectedActivities.add(lastSelected);

        // iterate through remaining activities
        for (int i = 1; i < activities.size(); i++) {
            Activity current = activities.get(i);

            // check if current activity does not overlap with last selected activity
            if (!lastSelected.overlap(current)) {
                selectedActivities.add(current);    // add to result
                lastSelected = current;         // update the last selected activity
            }
        }

        return selectedActivities;      // return list of selected activities

    }

    public static LinkedList<Integer> makeChange(int amount, int[] denominations) {
        // TASK 1.B.b
        // change making problem tries to find the min number of coins/notes for given amount of money.

        LinkedList<Integer> change = new LinkedList<>();

        for (int denomination : denominations) {
            while (amount >= denomination) {
                amount -= denomination;
                change.add(denomination);
            }
        }
        return change;
    }

    public static void main(String[] args) {
        LinkedList<Activity> activities = new LinkedList<Activity>();
        activities.add(new Activity(1,1, 4));
        activities.add(new Activity(2, 3, 5));
        activities.add(new Activity(3, 0, 6));
        activities.add(new Activity(4, 5, 7));
        activities.add(new Activity(5, 3, 8));
        activities.add(new Activity(6, 5, 9));
        activities.add(new Activity(7, 6, 10));
        activities.add(new Activity(8, 8, 11));
        activities.add(new Activity(9, 8, 12));
        activities.add(new Activity(10, 2, 13));
        activities.add(new Activity(11, 12, 14));
        activitySelection(activities).forEach(a -> a.print());

        System.out.println();
        makeChange(1234, new int[] { 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 }).forEach(i -> System.out.println(i));
    }
}
