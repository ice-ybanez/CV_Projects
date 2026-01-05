package main

import (
	"encoding/json"
	"errors"
	"os"
	"path/filepath"
	"sync"
)

// Interfaces used by everything else
type WordLookup interface {
	Exists(word string) bool
}
type PangramList interface {
	All() []string
}

// Singleton cache
// built once (sync.Once) and reused everywhere
type DictionaryCache struct {
	Dictionary WordLookup
	Pangrams   PangramList
}

var (
	cache     *DictionaryCache
	cacheOnce sync.Once // singleton (sync.Once)
	cacheErr  error
)

// Candidates for dictionary
var dictCandidates = []string{
	"data/words_dictionary.json",
}
var pangramCandidates = []string{
	"data/pangrams.json",
}

// GetDictionaryCache returns the singleton cache
func GetDictionaryCache() (*DictionaryCache, error) {
	cacheOnce.Do(func() {
		dictPath, err := firstExisting(dictCandidates)
		if err != nil {
			cacheErr = err
			return
		}
		pangPath, err := firstExisting(pangramCandidates)
		if err != nil {
			cacheErr = err
			return
		}

		dict, err := NewJSONWordDictAdapter(dictPath)
		if err != nil {
			cacheErr = err
			return
		}
		pangs, err := NewJSONPangramAdapter(pangPath)
		if err != nil {
			cacheErr = err
			return
		}

		cache = &DictionaryCache{
			Dictionary: dict,
			Pangrams:   pangs,
		}
	})
	return cache, cacheErr
}

func firstExisting(cands []string) (string, error) {
	for _, p := range cands {
		if _, err := os.Stat(p); err == nil {
			return p, nil
		}
	}
	return "", errors.New("no file found in candidates: " + filepath.Base(cands[0]))
}

// Adapters

type JSONWordDictAdapter struct {
	words map[string]struct{}
}

// adapts words_dictionary.json to WordLookup

func NewJSONWordDictAdapter(path string) (*JSONWordDictAdapter, error) {
	raw := make(map[string]int)
	f, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer f.Close()

	if err := json.NewDecoder(f).Decode(&raw); err != nil {
		return nil, err
	}

	s := make(map[string]struct{}, len(raw))
	for w := range raw {
		s[w] = struct{}{}
	}
	return &JSONWordDictAdapter{words: s}, nil
}

func (a *JSONWordDictAdapter) Exists(word string) bool {
	_, ok := a.words[word]
	return ok
}

type JSONPangramAdapter struct {
	list []string
}

func NewJSONPangramAdapter(path string) (*JSONPangramAdapter, error) {
	raw := make(map[string]string) // pangrams.json is a dict of keys
	f, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	if err := json.NewDecoder(f).Decode(&raw); err != nil {
		return nil, err
	}

	out := make([]string, 0, len(raw))

	for k := range raw {
		out = append(out, k)
	}

	return &JSONPangramAdapter{list: out}, nil
}

func (a *JSONPangramAdapter) All() []string { return a.list }
