package com.finalproject.oopproject.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class UIController {

    public static void setFontSize(double size, Label... labels) {
        for (Label label : labels) {
            label.setStyle("-fx-font-size: " + size + "px;" +
                    " -fx-text-fill: white;" +
                    "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        }
    }

    public static HBox createRow(Node... elements) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        row.getChildren().addAll(elements);
        return row;
    }
    public void updateUI(WeatherController weatherController, Label currentCity, Label description, ImageView weatherIconView, Label cloudiness, Label feelsLikeLabel, Label sunriseLabel, Label sunsetLabel, Label text1, Label text3, Label text4, Label text5, Label text6 ,HBox footer) {
        currentCity.setText(weatherController.getCity());
        description.setText(weatherController.getDescription());

        weatherIconView.setImage(new Image(weatherController.getIconUrl()));

        cloudiness.setText(String.format("Cloud coverage:\n               %d%%", weatherController.getCloudCoverage()));
        System.out.println(cloudiness.getText());
        feelsLikeLabel.setText(String.format(" Feels like\n        %.1f", weatherController.getFeelsLike()));
        sunriseLabel.setText(String.format("Sunrise\n %s", weatherController.getSunrise()));
        sunsetLabel.setText(String.format("Sunset\n %s", weatherController.getSunset()));

        text6.setText(String.format("%s\n%s", weatherController.getCity(), weatherController.getCountry()));
        text1.setText(String.format("Temperature is %.1f", weatherController.getTemperature()));
        text3.setText(String.format("Humidity is %.1f\nPressure is %.1f", weatherController.getHumidity(), weatherController.getPressure()));
        text4.setText(String.format("Speed is %.1f\nDirection: %s", weatherController.getWindSpeed(), weatherController.getWindDirection()));
        text5.setText(String.format("Rain chance: %.0f%%\nVisibility: %dm", weatherController.getCurrentRainChance(), weatherController.getVisibility()));

        // Update the footer (7-day forecast)
        footer.getChildren().clear();
        for (int i = 1; i < 6; i++) {
            VBox dayBox = new VBox();
            dayBox.setSpacing(5);
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPrefWidth(1200 / 7);

            LocalDate date = LocalDate.now().plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

            String iconUrl = weatherController.getDailyIcon(i);
            ImageView iconView;
            if (!iconUrl.isEmpty()) {
                Image iconImage = new Image(iconUrl, 50, 50, true, true);
                iconView = new ImageView(iconImage);
            } else {
                iconView = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/TitleIcon.png")));
            }

            double maxTemp = weatherController.getDailyMaxTemp(i);
            double minTemp = weatherController.getDailyMinTemp(i);
            Label tempLabel = new Label(String.format("High: %.1f°C\nLow: %.1f°C", maxTemp, minTemp));
            tempLabel.setStyle("-fx-text-fill: white;");

            dayBox.getChildren().addAll(dayLabel, iconView, tempLabel);
            footer.getChildren().add(dayBox);
        }
    }


}
