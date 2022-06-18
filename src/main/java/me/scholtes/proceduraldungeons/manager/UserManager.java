package me.scholtes.proceduraldungeons.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserManager {

    private final Random random = new Random();
    private final SecureRandom secureRandom = new SecureRandom();
    private final SQLManager sqlManager;

    private final Map<Integer, UUID> loggedInUserID;
    private final Map<UUID, Integer> loggedInUUID;
    private final Cache<Integer, String> emailVerification;
    private final Cache<UUID, Verification> authentication;

    public UserManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
        loggedInUserID = new HashMap<>();
        loggedInUUID = new HashMap<>();
        emailVerification = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
        authentication = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    public void register(UUID uuid, String username, String email, String password) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO users (username, email, password, salt, verified, games_played, games_won, total_kills) VALUES (?, ?, ?, ?, FALSE, 0, 0, 0)"
            );

            byte[] salt = generateSalt();
            byte[] hash = generateHash(password, salt);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, Base64.getEncoder().encodeToString(hash));
            statement.setString(4, Base64.getEncoder().encodeToString(salt));
            statement.executeUpdate();

            sendEmailVerification(getUserIDEmail(email), email);
            int id = getUserIDUsername(username);
            loggedInUUID.put(uuid, id);
            loggedInUserID.put(id, uuid);
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
    }

    public boolean verifyEmail(int id, String code) {
        if (emailVerification.getIfPresent(id) == null) return false;
        if (!emailVerification.getIfPresent(id).equals(code)) return false;

        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET verified=TRUE WHERE id=?"
            );

            statement.setInt(1, id);
            statement.executeUpdate();

            emailVerification.invalidate(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return false;
    }

    public boolean authenticate(UUID uuid, String code) {
        if (authentication.getIfPresent(uuid) == null) return false;
        if (!authentication.getIfPresent(uuid).getCode().equals(code)) return false;
        login(uuid, authentication.getIfPresent(uuid).getUserID());
        return true;
    }

    public boolean isCodeExpired(int id) {
        return (emailVerification.getIfPresent(id) == null);
    }

    public void sendEmailVerification(int id, String email) {
        String code = String.format("%5s", random.nextInt(10000)).replace(' ', '0');
        emailVerification.put(id, code);

        EmailManager.sendEmail(email, "Email Verification - Procedural Dungeons", "Your verification code is: " + code);
    }

    public void sendEmailAuthentication(UUID uuid, int id, String email) {
        String code = String.format("%5s", random.nextInt(10000)).replace(' ', '0');
        authentication.put(uuid, new Verification(id, code));

        EmailManager.sendEmail(email, "Authentication Code - Procedural Dungeons", "Your authentication code is: " + code);
    }

    public boolean loginAttempt(UUID uuid, String userOrEmail, String password) {
        if (isEmail(userOrEmail)) {
            int id = getUserIDEmail(userOrEmail);
            if (id == -1 || !checkPassword(id, password)) return false;

            sendEmailAuthentication(uuid, id, userOrEmail);
            return true;
        }

        int id = getUserIDUsername(userOrEmail);
        if (id == -1 || !checkPassword(id, password)) return false;

        sendEmailAuthentication(uuid, id, getEmailUserID(id));
        return true;
    }

    public void login(UUID uuid, int id) {
        loggedInUUID.put(uuid, id);
        loggedInUserID.put(id, uuid);
        authentication.invalidate(uuid);
    }

    public void logout(UUID uuid) {
        if (!loggedInUUID.containsKey(uuid)) return;
        loggedInUserID.remove(loggedInUUID.get(uuid));
        loggedInUUID.remove(uuid);
    }

    public boolean isLoggedIn(UUID uuid) {
        return loggedInUUID.containsKey(uuid);
    }

    public boolean isLoggedIn(int id) {
        return loggedInUserID.containsKey(id);
    }

    public boolean isLoggedIn(String userOrEmail) {
        if (isEmail(userOrEmail)) {
            int id = getUserIDEmail(userOrEmail);
            if (id == -1) return false;
            return isLoggedIn(id);
        }

        int id = getUserIDUsername(userOrEmail);
        if (id == -1) return false;
        return isLoggedIn(id);
    }

    public boolean hasAuthCode(UUID uuid) {
        return (authentication.getIfPresent(uuid) != null);
    }

    public int getID(UUID uuid) {
        return loggedInUUID.get(uuid);
    }

    public UUID getUUID(int id) {
        return loggedInUserID.get(id);
    }

    public boolean isVerified(int id) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE id=? AND verified=TRUE"
            );

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return false;
    }

    public boolean userExists(String username) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE upper(username)=upper(?)"
            );

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return false;
    }

    public boolean emailExists(String email) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE upper(email)=upper(?)"
            );

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return false;
    }

    private boolean checkPassword(int userID, String password) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            );

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] hashedPass = Base64.getDecoder().decode(resultSet.getString(4));
                byte[] salt = Base64.getDecoder().decode(resultSet.getString(5));

                byte[] comparison = generateHash(password, salt);

                return comparePasswords(hashedPass, comparison);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return false;
    }

    public int getUserIDUsername(String username) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE upper(username)=upper(?)"
            );

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return -1;
    }

    private int getUserIDEmail(String email) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE upper(email)=upper(?)"
            );

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return -1;
    }

    public String getEmailUserID(int id) {
        return getStringValue(id, 3);
    }

    public String getStringValue(int id, int column) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            );

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString(column);
            } else {
                return "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return "";
    }

    public int getIntegerValue(int id, int column) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            );

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(column);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
        return 0;
    }

    public void incrementGamesPlayed(int id) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET games_played=games_played + 1 WHERE id=?"
            );

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
    }

    public void incrementGamesWon(int id) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET games_won=games_won + 1 WHERE id=?"
            );

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
    }

    public void incrementTotalKills(int id) {
        Connection con = null;
        try {
            con = sqlManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE users SET total_kills=total_kills + 1 WHERE id=?"
            );

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sqlManager.closeConnection(con);
        }
    }

    public String getUsernameID(int id) {
        return getStringValue(id, 2);
    }

    public int getTotalKills(int id) {
        return getIntegerValue(id, 9);
    }

    public int getGamesWon(int id) {
        return getIntegerValue(id, 8);
    }

    public int getGamesPlayed(int id) {
        return getIntegerValue(id, 7);
    }

    private byte[] generateHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 512);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);

        return salt;
    }

    public static boolean isEmail(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }

    // Code from https://www.javatips.net/api/JavaSecurity-master/crypto-hash/src/main/java/de/dominikschadow/javasecurity/hash/PBKDF2.java
    private boolean comparePasswords(byte[] originalHash, byte[] comparisonHash) {
        int diff = originalHash.length ^ comparisonHash.length;
        for (int i = 0; i < originalHash.length && i < comparisonHash.length; i++) {
            diff |= originalHash[i] ^ comparisonHash[i];
        }

        return diff == 0;
    }

}
