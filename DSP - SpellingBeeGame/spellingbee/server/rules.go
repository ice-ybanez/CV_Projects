package main

import "unicode/utf8"

// scoring & validation
// Factory for RuleSet

type RuleSetFactory struct{}

func NewRuleSetFactory() *RuleSetFactory {
	return &RuleSetFactory{}
}

func (f *RuleSetFactory) Standard() RuleSet {
	return &StandardRuleSet{}
}

// validation for RuleSet

type RuleSet interface {
	ValidateAndScore(word string, upperLetters string, center rune, dict WordLookup) Validation
}
type Validation struct {
	Valid         bool
	Reason        string
	Points        int
	Pangram       bool
	MissingCenter bool
}

// standard ruleset (may add different rules later for different difficulty levels)

type StandardRuleSet struct{}

// Scoring system/rules:
// if the length of the word is less than 4(len < 4) is invalid
// the word submitted MUST include center letter (wrapped in square brackets in the UI)
// can only use puzzle letters (repeats allowed)
// word submitted has to be in the dictionary
// scoring are as follows: 4 letters give +1 point; >=5 gives len(word) points
// pangram (+7 points) if submitted word uses all 7 unique letters

func (s *StandardRuleSet) ValidateAndScore(word string, upperLetters string, center rune, dict WordLookup) Validation {
	// length check
	if utf8.RuneCountInString(word) < 4 { // counts runes as opposed to bytes
		return Validation{
			Valid: false, Reason: "too short"}
	}

	// build allowed set from puzzle letters
	allowed := make(map[rune]struct{}, 7)
	for _, r := range upperLetters {
		allowed[toLower(r)] = struct{}{}
	}

	// includes center and validity
	hasCenter := false
	for _, r := range word {
		if _, ok := allowed[r]; !ok {
			return Validation{Valid: false, Reason: "uses invalid letter"}
		}

		if r == toLower(center) {
			hasCenter = true
		}
	}

	if !hasCenter {
		return Validation{Valid: false, MissingCenter: true, Reason: "missing center"}
	}

	// dictionary lookup through interface WordLookup (Adapter hides the JSOn details)
	if !dict.Exists(word) {
		return Validation{Valid: false, Reason: "not a valid word"}
	}

	// score
	l := utf8.RuneCountInString(word)
	points := 1
	if l >= 5 {
		points = l
	}

	// if pangram, get 7 points extra
	uniq := make(map[rune]struct{}, 7)
	for _, r := range word {
		uniq[r] = struct{}{}
	}

	pangram := len(uniq) == 7
	if pangram {
		points += 7
	}

	return Validation{
		Valid: true, Points: points, Pangram: pangram}
}
