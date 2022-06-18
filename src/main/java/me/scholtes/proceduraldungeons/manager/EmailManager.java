package me.scholtes.proceduraldungeons.manager;

import me.scholtes.proceduraldungeons.ProceduralDungeons;
import org.bukkit.Bukkit;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailManager {

    public static void sendEmail(String to, String subject, String text) {
        Bukkit.getScheduler().runTaskAsynchronously(ProceduralDungeons.getInstance(), () -> {
            Properties properties = System.getProperties();

            // Setup mail server
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass username and password
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication("proceduraldungeons@gmail.com", "rtnzxwvvgpqqvcft");

                }

            });
            System.out.println("Sending email...");

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress("proceduraldungeons@gmail.com"));

                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                // Set Subject: header field
                message.setSubject(subject);

                // Now set the actual message
                message.setText(text);

                // Send message
                Transport.send(message);
                System.out.println("Email sent!");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
        });

    }

}
