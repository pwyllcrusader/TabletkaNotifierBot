package by.pw.crr.engine;

import by.pw.crr.dao.ChatUserDAO;
import by.pw.crr.dao.MedicineDAO;
import by.pw.crr.dao.PharmacyOfferDAO;
import by.pw.crr.entities.ChatUser;
import by.pw.crr.entities.PharmacyOffer;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static java.lang.Thread.sleep;

public class TabletkaNotifierBot extends TelegramLongPollingBot {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("tg-bot");
    private final ChatUserDAO chatUserDAO = new ChatUserDAO(emf.createEntityManager());
    private final MedicineDAO medicineDAO = new MedicineDAO(emf.createEntityManager());
    private final PharmacyOfferDAO pharmacyOfferDAO = new PharmacyOfferDAO(emf.createEntityManager());

    @SneakyThrows
    public TabletkaNotifierBot() {
        Runnable updatingTask = this::updateOffers;
        Thread updatingThread = new Thread(updatingTask);
        updatingThread.start();

        Runnable sendingTask = this::sendPharmacyOffers;
        Thread sendingThread = new Thread(sendingTask);
        sendingThread.start();
    }

    @SneakyThrows
    private void sendPharmacyOffers() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        for (PharmacyOffer pharmacyOffer : pharmacyOfferDAO.findAll()) {
            if (!pharmacyOffer.isSent()) {
                sendMessage.setChatId(pharmacyOffer.getChatUser().getChatId());
                sendMessage.setText(pharmacyOffer.toString());
                execute(sendMessage);
                pharmacyOffer.setSent(true);
                pharmacyOfferDAO.update(pharmacyOffer);
                sleep(3125);
            }
        }
        sleep(300_000);
    }

    @SneakyThrows
    private void updateOffers() {
        for (ChatUser chatUser : chatUserDAO.findAll()) {
            List<PharmacyOffer> parsedOffers = TabletkaParser.INSTANCE.parseTabletka(chatUser);
            parsedOffers.forEach(pharmacyOfferDAO::update);
        }
        sleep(300_000);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        //TODO доделать инлайн меню

        if (update.getMessage().getText().equals("/start")) {
            ChatUser chatUser = new ChatUser(update.getMessage().getChatId());
            chatUserDAO.update(chatUser);
            execute(new SendMessage(chatUser.getChatId(), "Введите код город:"));
        } else {
            chatUserDAO.findByID(update.getMessage().getChatId())
                    .setLocationId(Integer.parseInt(update.getMessage().getText()));
        }
    }

    @Override
    public String getBotUsername() {
        return "TabletkaNotifierBot";
    }

    @Override
    public String getBotToken() {
        return "1332679824:AAF5tF7hSNsnhsmIl3v8D-9RpZmn-5t1-C8";
    }
}
