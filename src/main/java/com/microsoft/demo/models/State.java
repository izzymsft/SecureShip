package com.microsoft.demo.models;

public class State {

    protected String abbreviation;

    protected String fullName;

    /**
     * Default Constructor
     */
    public State() {

    }

    /**
     * Constructor
     *
     * @param abbr State Abbreviation
     * @param name State Full Name
     */
    public State(final String abbr, final String name) {

        this.abbreviation = abbr;
        this.fullName = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
