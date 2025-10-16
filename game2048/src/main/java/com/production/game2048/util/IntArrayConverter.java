package com.production.game2048.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A JPA AttributeConverter to convert a 2D integer array (int[][])
 * into a single String for database persistence, and back.
 * This allows storing the game board state in a standard text column.
 *
 * The format is: rows separated by semicolons (;), and columns by commas (,).
 * Example: "2,0,0,0;4,0,2,0;0,0,0,0;0,0,4"
 */
@Converter
public class IntArrayConverter implements AttributeConverter<int[][], String> {

    private static final String ROW_SEPARATOR = ";";
    private static final String COLUMN_SEPARATOR = ",";

    /**
     * Converts the 2D integer array into a String for database storage.
     *
     * @param attribute The 2D array representing the game board. Must not be null.
     * @return A String representation of the board.
     */
    @Override
    public String convertToDatabaseColumn(int[][] attribute) {
        // Defend against null input, returning null for a null board.
        if (attribute == null) {
            return null;
        }

        // Using Streams for a modern, functional approach to transformation.
        return Arrays.stream(attribute)
                .map(row -> Arrays.stream(row)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(COLUMN_SEPARATOR)))
                .collect(Collectors.joining(ROW_SEPARATOR));
    }

    /**
     * Converts the String from the database back into a 2D integer array.
     *
     * @param dbData The String representation of the board from the database.
     * @return A 2D integer array, or null if the input is null or empty.
     */
    @Override
    public int[][] convertToEntityAttribute(String dbData) {
        // Defend against null or empty data from the database.
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        try {
            return Arrays.stream(dbData.split(ROW_SEPARATOR))
                    .map(row -> Arrays.stream(row.split(COLUMN_SEPARATOR))
                            // This will throw NumberFormatException for malformed data, which is handled below.
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        } catch (NumberFormatException e) {
            // If the data is corrupted (e.g., contains non-integer values),
            // throw a specific exception. This prevents the application from proceeding with a corrupt state.
            throw new IllegalArgumentException("Failed to convert database data to board. Invalid number format.", e);
        }
    }
}