package com.example.kisanbandhu.models;

/** Chemical and natural/organic fertilizer suggestion for one growth stage. */
public class FertilizerAdvice {

    private final String chemical;
    private final String organic;

    public FertilizerAdvice(String chemical, String organic) {
        this.chemical = chemical;
        this.organic = organic;
    }

    public String getChemical() { return chemical; }
    public String getOrganic() { return organic; }
}
