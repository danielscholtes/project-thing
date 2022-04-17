package me.scholtes.proceduraldungeons.dungeon.manager;

public class Verification {

    private int userID;
    private String code;

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
