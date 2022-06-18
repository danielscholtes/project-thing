package me.scholtes.proceduraldungeons.manager;

public class Verification {

    private final int userID;
    private final String code;

    public Verification(int userID, String code) {
        this.userID = userID;
        this.code = code;
    }

    public int getUserID() {
        return userID;
    }

    public String getCode() {
        return code;
    }
}
