package com.microsoft.demo.models;

/**
 * Represents and Object with Confidential Information
 *
 */
public class StateConfidentialData extends State {

    protected String capital;

    public StateConfidentialData()
    {
        super();
    }

    /**
     *
     * @param abbr Abbreviation
     * @param name Full Name
     */
    public StateConfidentialData(final String abbr, final String name)
    {
        super(abbr, name);
    }

    /**
     *
     * @param abbr Abbreviation
     * @param name Full Name
     * @param stateCapital State Capital location stored in a secure location
     */
    public StateConfidentialData(final String abbr, final String name, String stateCapital)
    {
        super(abbr, name);

        this.capital = stateCapital;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }
}
