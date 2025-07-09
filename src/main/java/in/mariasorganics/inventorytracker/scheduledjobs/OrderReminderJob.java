package in.mariasorganics.inventorytracker.scheduledjobs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import in.mariasorganics.inventorytracker.entity.NotificationRecipient;
import in.mariasorganics.inventorytracker.entity.OrderReminder;
import in.mariasorganics.inventorytracker.repository.NotificationRecipientRepository;
import in.mariasorganics.inventorytracker.repository.OrderReminderRepository;
import in.mariasorganics.inventorytracker.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderReminderJob {

    private final OrderReminderRepository reminderRepo;
    private final NotificationRecipientRepository recipientRepo;
    private final EmailServiceImpl emailService;

    // @Scheduled(cron = "0 30 9 * * *") // Daily at 9:30 AM
    @Scheduled(cron = "0 * * * * *") // Runs at 0th second of every minute
    public void sendOrderReminders() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        List<OrderReminder> upcomingReminders = reminderRepo.findByOrderDateBetween(today, threeDaysLater);
        List<NotificationRecipient> recipients = recipientRepo.findByActiveTrue();

        for (OrderReminder reminder : upcomingReminders) {
            String subject = "Upcoming Order Reminder Maria's Mushroom Farm: " + reminder.getMaterialName();
            String body = String.format("""
                    <p><b>Material:</b> %s<br/>
                       <b>Supplier:</b> %s<br/>
                       <b>Room:</b> %s<br/>
                       <b>Order Date:</b> %s</p>
                    <p style="color:gray; font-size:0.9em;">
                        If this item has already been ordered, please ignore this reminder.
                    </p>
                    """,
                    reminder.getMaterialName(),
                    reminder.getSupplierName(),
                    reminder.getGrowRoom().getName(),
                    reminder.getOrderDate());

            for (NotificationRecipient recipient : recipients) {
                emailService.sendEmail(recipient.getEmail(), subject, body);
            }
        }
    }
}
