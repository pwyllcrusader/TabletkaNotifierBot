package by.pw.crr.engine;

import by.pw.crr.entities.ChatUser;
import by.pw.crr.entities.Medicine;
import by.pw.crr.entities.PharmacyOffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum TabletkaParser {
    INSTANCE;

    public String parseMedicineName(Long medicineId) {
        String medicineName = "";
        try {
            Document document = Jsoup.connect("https://tabletka.by/result?ls=" + medicineId)
                    .userAgent("Chrome/81.0.4044.138")
                    .referrer("http://www.google.com")
                    .get();
            medicineName = document.select("h1").textNodes().get(0).text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medicineName;
    }

    public List<PharmacyOffer> parseTabletka(ChatUser chatUser) {
        List<PharmacyOffer> parsedOffers = new ArrayList<>();
        for (Medicine medicine : chatUser.getMedicines()) {
            try {
                Document document = Jsoup.connect("https://tabletka.by/result?ls=" + medicine.getMedicineId() + "&region=" + chatUser.getLocationId())
                        .userAgent("Chrome/81.0.4044.138")
                        .referrer("http://www.google.com")
                        .get();
                Elements pharmacyOffers = document.getElementsByClass("tr-border");
                for (Element element : pharmacyOffers) {
                    PharmacyOffer parsedOffer = new PharmacyOffer(element.select("div.tooltip-info-header > a").text(),
                            element.select("div.text-wrap > span:not([class])").text(),
                            medicine,
                            element.select("span.price-value").text(),
                            chatUser);
                    parsedOffers.add(parsedOffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parsedOffers;
    }
}
