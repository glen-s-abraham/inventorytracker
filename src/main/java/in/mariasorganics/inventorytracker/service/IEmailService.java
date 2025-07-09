package in.mariasorganics.inventorytracker.service;

public interface IEmailService {

     public void sendEmail(String to, String subject, String htmlBody);
} 