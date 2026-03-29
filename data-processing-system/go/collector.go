package main

import (
	"bufio"
	"fmt"
	"os"
	"sort"
)

// collectResults owns the results slice, so there is no need for a shared lock.
// Only this goroutine appends to the slice, which keeps the design simple and safe.
func collectResults(results <-chan ProcessedResult, fileName string) ([]ProcessedResult, error) {
	collected := make([]ProcessedResult, 0)
	for result := range results {
		collected = append(collected, result)
	}

	sort.Slice(collected, func(i, j int) bool {
		return collected[i].TaskID < collected[j].TaskID
	})

	file, err := os.Create(fileName)
	if err != nil {
		return collected, fmt.Errorf("create results file: %w", err)
	}
	defer file.Close()

	writer := bufio.NewWriter(file)

	_, err = fmt.Fprintln(writer, "Data Processing System Results")
	if err != nil {
		return collected, fmt.Errorf("write header: %w", err)
	}
	_, err = fmt.Fprintln(writer, "========================================")
	if err != nil {
		return collected, fmt.Errorf("write separator: %w", err)
	}

	for _, result := range collected {
		_, err = fmt.Fprintln(writer, result.String())
		if err != nil {
			return collected, fmt.Errorf("write result: %w", err)
		}
	}

	_, err = fmt.Fprintln(writer, "========================================")
	if err != nil {
		return collected, fmt.Errorf("write footer separator: %w", err)
	}
	_, err = fmt.Fprintf(writer, "Total results: %d\n", len(collected))
	if err != nil {
		return collected, fmt.Errorf("write total: %w", err)
	}

	if err := writer.Flush(); err != nil {
		return collected, fmt.Errorf("flush results file: %w", err)
	}

	return collected, nil
}
