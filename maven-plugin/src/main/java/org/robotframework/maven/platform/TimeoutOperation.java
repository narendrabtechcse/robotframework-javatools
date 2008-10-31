package org.robotframework.maven.platform;

public class TimeoutOperation {

    private final long timeout;

    private final Runnable operation;

    private final String description;

    public TimeoutOperation(long timeout, Runnable operation) {
        this(timeout, operation, "Operation");
    }

    public TimeoutOperation(long timeout, Runnable operation,
            String description) {
        this.timeout = timeout;
        this.operation = operation;
        this.description = description;
    }

    public void start() {
        Thread t = new Thread(operation);
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException e) {
        }
        if (t.isAlive()) {
            t.interrupt();
            throw new RuntimeException(description
                    + " did not complete within " + timeout + "ms.");
        }
    }
}
