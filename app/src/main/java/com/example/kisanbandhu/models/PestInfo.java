package com.example.kisanbandhu.models;

/** A common pest/disease of a crop with chemical and natural control options. */
public class PestInfo {

    private final String name;
    private final String symptoms;
    private final String chemicalControl;
    private final String organicControl;

    public PestInfo(String name, String symptoms, String chemicalControl, String organicControl) {
        this.name = name;
        this.symptoms = symptoms;
        this.chemicalControl = chemicalControl;
        this.organicControl = organicControl;
    }

    public String getName() { return name; }
    public String getSymptoms() { return symptoms; }
    public String getChemicalControl() { return chemicalControl; }
    public String getOrganicControl() { return organicControl; }
}
