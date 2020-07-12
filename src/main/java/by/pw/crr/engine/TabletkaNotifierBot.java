package by.pw.crr.engine;

import by.pw.crr.dao.ChatUserDAO;
import by.pw.crr.dao.MedicineDAO;
import by.pw.crr.dao.PharmacyOfferDAO;
import by.pw.crr.entities.ChatUser;
import by.pw.crr.entities.Medicine;
import by.pw.crr.entities.PharmacyOffer;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
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
                sleep(3_125);
            }
        }
        sleep(60_000);
    }

    @SneakyThrows
    private void updateOffers() {
        for (ChatUser chatUser : chatUserDAO.findAll()) {
            List<PharmacyOffer> parsedOffers = TabletkaParser.INSTANCE.parseTabletka(chatUser);
            parsedOffers.forEach(pharmacyOfferDAO::update);
        }
        sleep(60_000);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    ChatUser chatUser = new ChatUser(update.getMessage().getChatId());
                    chatUserDAO.update(chatUser);
                    execute(sendInlineKeyboardToChooseLocation(chatUser.getChatId()));
                } else {
                    ChatUser chatUser = chatUserDAO.findByID(update.getMessage().getChatId());
                    Medicine medicine = new Medicine();
                    medicine.setMedicineId(Long.parseLong(update.getMessage().getText()));
                    medicine.setMedicineName(TabletkaParser.INSTANCE.parseMedicineName(medicine.getMedicineId()));
                    chatUser.getMedicines().add(medicine);
                    medicineDAO.update(medicine);
                    chatUserDAO.update(chatUser);
                    execute(new SendMessage().setChatId(chatUser.getChatId())
                            .setText("Вы подписались на обновления препарата: " + medicine.getMedicineName()));
                }
            }
        } else {
            if (update.hasCallbackQuery()) {
                ChatUser chatUser = chatUserDAO.findByID(update.getCallbackQuery().getMessage().getChatId());
                chatUser.setLocationId(Integer.parseInt(update.getCallbackQuery().getData()));
                chatUserDAO.update(chatUser);
                execute(new SendMessage().setChatId(chatUser.getChatId()).setText("Введите код препарата для отслеживания: "));
            }
        }
    }

    public SendMessage sendInlineKeyboardToChooseLocation(Long chatID) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Минск").setCallbackData("1001"));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Гродно").setCallbackData("38"));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Брест").setCallbackData("41"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Гомель").setCallbackData("36"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Могилев").setCallbackData("40"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Витебск").setCallbackData("19"));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatID).setText("Выберите город:").setReplyMarkup(inlineKeyboardMarkup);
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
