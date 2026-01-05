#include <stdlib.h>
#include <string.h>
#include "hungry_monkey.h"

// struct for monkey's current state
struct monkey_state_str{
    int monkey_col;     // monkey's column pos
    int target_col;     // col w/ most treats
    int osc_count;      // track target_col's consistency
};

struct monkey_action move_monkey(int field[][FIELD_WIDTH], void *monkey_state){
    // Your code here

    struct monkey_state_str *state;

    // if this is first call, allocate memory and initialize state
    int is_first_call;
    if (monkey_state == NULL) {
        state = malloc(sizeof(struct monkey_state_str));

        state ->monkey_col = FIELD_WIDTH / 2;   // starting at middle
        state ->target_col = state ->monkey_col;    // make target col the monkey pos
        state ->osc_count = 2;     
        is_first_call = 1;
    } 
    // otherwise, cast existing state
    else {
        state = (struct monkey_state_str *)monkey_state;   
    }


    int treat_count[FIELD_WIDTH] = {0};    // initial treat count per col

    // // ID treats top to bottom
    // for (int i = 0; i < FIELD_HEIGHT; i++) {
    //     for (int j = 0; j < FIELD_WIDTH; j++) {
    //         if (field[i][j] == TREAT_VAL) {
    //             treat_count[j]++;
    //         }
    //     }
    // }

    // ID treats from bottom-up
    for (int i = FIELD_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < FIELD_WIDTH; j++) { // scanning left to right
                if (field[i][j] == TREAT_VAL) {
                    treat_count[j]++;
                }
            }
    }

    
    // finding col w/ most treats
    int best_col = state -> monkey_col;
    int max_treats = 0;
    for (int j = 0; j < FIELD_WIDTH; j++) {
        if (treat_count[j] > max_treats) {
            max_treats = treat_count[j];
            best_col = j;
        }
    }   

    // avoiding oscillation
    // only change target_col if the new best_col has been stable for 2+ steps
    if (best_col == state ->target_col) { 
        state ->osc_count++;   // column with most treats is still the same
    } 
    else {
        state ->osc_count = 0; // column wit most treats changed
    }

    // only update the target(target_col) if it's been the best for enough frames
    if (state ->osc_count >= 2 || is_first_call) {
        state ->target_col = best_col;
    }


    int move = MOVE_FWD;    // initially no movement
    if (state ->target_col < state ->monkey_col) {       // if target is to left of monkey
        move = MOVE_LEFT;       // move left
        state ->monkey_col--;   // update position
    } 
    else if (state ->target_col > state ->monkey_col) {  // if """"""
        move = MOVE_RIGHT;
        state ->monkey_col++;
    }

    // making sure monkey doesn't go out of bounds
    if (state ->monkey_col < 0) {
        state ->monkey_col = 0;
    }
    if (state ->monkey_col >= FIELD_WIDTH) {
        state ->monkey_col = FIELD_WIDTH - 1;
    }

    // return monkey actions
    struct monkey_action retval;
    retval.move = move;
    retval.state = state;
    return retval;
}