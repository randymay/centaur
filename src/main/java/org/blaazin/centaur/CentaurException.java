package org.blaazin.centaur;

public class CentaurException extends Exception {
    public CentaurException() {

    }

    public CentaurException(String message) {
        super(message);
    }

    public CentaurException(Exception e) {
        super(e);
    }

    public String getStackTraceString() {
        StringBuilder stackTraceString = new StringBuilder();

        for (StackTraceElement element : this.getStackTrace()) {
            stackTraceString.append(element.toString());
            stackTraceString.append(System.getProperty("line.separator"));
        }

        return stackTraceString.toString();
    }
}
