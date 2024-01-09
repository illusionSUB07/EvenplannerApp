package com.example.Eveplanner;

import android.content.ContentValues;

/**
 * EventDatabase interface defines the contract for managing events in the database.
 */
public interface EventDatabase {
    /**
     * Checks if an event with the given name already exists in the database.
     *
     * @param eventName The name of the event to check.
     * @return {@code true} if the event exists, {@code false} otherwise.
     */
    boolean eventExists(String eventName);

    /**
     * Inserts a new event into the database.
     *
     * @param values The ContentValues object containing the event details.
     * @return The row ID of the inserted event, or -1 if insertion failed.
     */
    long insertEvent(ContentValues values);
}
