package com.aquarius.simple.network;

/**
 * Created by aquarius on 2017/10/29.
 */
public interface RetryPolicy {

    /**
     * Returns the current timeout (used for logging).
     */
    public int getCurrentTimeout();

    /**
     * Returns the current retry count (used for logging).
     */
    public int getCurrentRetryCount();

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error The error code of the last attempt.
     * @throws Error In the event that the retry could not be performed (for example if we
     * ran out of attempts), the passed in error is thrown.
     */
    public void retry(Error error) throws Error;
}
