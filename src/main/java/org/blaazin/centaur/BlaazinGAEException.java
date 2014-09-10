package org.blaazin.centaur;

public class BlaazinGAEException extends Exception {
    public BlaazinGAEException() {

    }

    public BlaazinGAEException(String message) {
        super(message);
    }

    public BlaazinGAEException(Exception e) {
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
