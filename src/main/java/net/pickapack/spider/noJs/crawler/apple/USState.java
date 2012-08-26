package net.pickapack.spider.noJs.crawler.apple;

import java.util.ArrayList;
import java.util.List;

public class USState {
    private int id;
    private String abbrev;
    private String name;

    public USState(String abbrev, String name) {
        this.id = currentId++;
        this.abbrev = abbrev;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public String getName() {
        return name;
    }

    private static int currentId = 0;

    private static List<USState> usStates = new ArrayList<USState>();

    static {
        usStates.add(new USState("AA", "Armed Forces Americas (except Canada)"));
        usStates.add(new USState("AE", "Armed Forces Middle East"));
        usStates.add(new USState("AK", "ALASKA"));
        usStates.add(new USState("AL", "ALABAMA"));
        usStates.add(new USState("AP", "Armed Forces Pacific"));
        usStates.add(new USState("AR", "ARKANSAS"));
        usStates.add(new USState("AS", "AMERICAN SAMOA"));
        usStates.add(new USState("AZ", "ARIZONA"));
        usStates.add(new USState("CA", "CALIFORNIA"));
        usStates.add(new USState("CO", "COLORADO"));
        usStates.add(new USState("CT", "CONNECTICUT"));
        usStates.add(new USState("DC", "DISTRICT OF COLUMBIA"));
        usStates.add(new USState("DE", "DELAWARE"));
        usStates.add(new USState("FL", "FLORIDA"));
        usStates.add(new USState("GA", "GEORGIA"));
        usStates.add(new USState("GU", "GUAM"));
        usStates.add(new USState("HI", "HAWAII"));
        usStates.add(new USState("IA", "IOWA"));
        usStates.add(new USState("ID", "IDAHO"));
        usStates.add(new USState("IL", "ILLINOIS"));
        usStates.add(new USState("IN", "INDIANA"));
        usStates.add(new USState("KS", "KANSAS"));
        usStates.add(new USState("KY", "KENTUCKY"));
        usStates.add(new USState("LA", "LOUISIANA"));
        usStates.add(new USState("MA", "MASSACHUSETTS"));
        usStates.add(new USState("MD", "MARYLAND"));
        usStates.add(new USState("ME", "MAINE"));
        usStates.add(new USState("MI", "MICHIGAN"));
        usStates.add(new USState("MN", "MINNESOTA"));
        usStates.add(new USState("MO", "MISSOURI"));
        usStates.add(new USState("MS", "MISSISSIPPI"));
        usStates.add(new USState("MT", "MONTANA"));
        usStates.add(new USState("NC", "NORTH CAROLINA"));
        usStates.add(new USState("ND", "NORTH DAKOTA"));
        usStates.add(new USState("NE", "NEBRASKA"));
        usStates.add(new USState("NH", "NEW HAMPSHIRE"));
        usStates.add(new USState("NJ", "NEW JERSEY"));
        usStates.add(new USState("NM", "NEW MEXICO"));
        usStates.add(new USState("NV", "NEVADA"));
        usStates.add(new USState("NY", "NEW YORK"));
        usStates.add(new USState("OH", "OHIO"));
        usStates.add(new USState("OK", "OKLAHOMA"));
        usStates.add(new USState("OR", "OREGON"));
        usStates.add(new USState("PA", "PENNSYLVANIA"));
        usStates.add(new USState("PR", "PUERTO RICO"));
        usStates.add(new USState("PW", "PALAU"));
        usStates.add(new USState("RI", "RHODE ISLAND"));
        usStates.add(new USState("SC", "SOUTH CAROLINA"));
        usStates.add(new USState("SD", "SOUTH DAKOTA"));
        usStates.add(new USState("TN", "TENNESSEE"));
        usStates.add(new USState("TX", "TEXAS"));
        usStates.add(new USState("UT", "UTAH"));
        usStates.add(new USState("VA", "VIRGINIA"));
        usStates.add(new USState("VI", "VIRGIN ISLANDS"));
        usStates.add(new USState("VT", "VERMONT"));
        usStates.add(new USState("WA", "WASHINGTON"));
        usStates.add(new USState("WI", "WISCONSIN"));
        usStates.add(new USState("WV", "WEST VIRGINIA"));
        usStates.add(new USState("WY", "WYOMING"));
    }

    public static List<USState> getUsStates() {
        return usStates;
    }

    public static USState getByName(String name) {
        for (USState state : usStates) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state;
            }
        }

        throw new IllegalArgumentException();
    }
}
