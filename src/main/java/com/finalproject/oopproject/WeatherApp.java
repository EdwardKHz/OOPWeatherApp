package com.finalproject.oopproject;

import com.finalproject.oopproject.controllers.DatabaseController;
import com.finalproject.oopproject.controllers.UIController;
import com.finalproject.oopproject.controllers.WeatherController;
import com.finalproject.oopproject.models.TableEntity;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeatherApp extends Application {
    UIController uiController = new UIController();
    ObservableList<TableEntity> tableContents = FXCollections.observableArrayList();
    Font font = Font.loadFont(
            getClass().getResourceAsStream("/com/finalproject/oopproject/THEBOLDFONT-FREEVERSION.otf"),
            16
    );


    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(font.getName());
        System.out.println(WeatherController.getLocation());
        WeatherController weatherController = new WeatherController(WeatherController.getLocation());

        LocalDate currentDate = LocalDate.now();
        String currentDay = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH); // gets current day (Monday, tuesday ,etc)

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(11,19,30,255)");


        BorderPane mainLayout = new BorderPane();
        HBox header = new HBox();
        HBox main = new HBox();
        VBox leftMain = new VBox();
        VBox rightMain = new VBox();
        HBox midMain = new HBox();
        HBox footer = new HBox();

        mainLayout.setTop(header);
        mainLayout.setCenter(main);
        mainLayout.setBottom(footer);

        main.setTranslateX(-50);

        root.getChildren().add(mainLayout);


        /* Header */

        Image logo = new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/image.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(50);
        logoView.setFitWidth(100);

        HBox searchBox = new HBox(5);
        Image searchIcon = new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/search.png"));
        ImageView searchIconView = new ImageView(searchIcon);
        Button searchButton = new Button();
        TextField searchBar = new TextField();

        searchButton.setGraphic(searchIconView);
        searchButton.setStyle("-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-border-color: transparent;");
        searchIconView.setFitHeight(25);
        searchIconView.setFitWidth(25);
        searchBox.setTranslateY(10);
        searchBar.setPromptText("Search");
        searchBar.setPrefWidth(150);
        searchBar.setPrefHeight(30);
        searchBox.getChildren().addAll(searchButton, searchBar);


        HBox.setHgrow(searchBox, Priority.NEVER);
        Region spacer = new Region();                       // Moves search bar and button to the far right
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;");
        header.setPadding(new Insets(10));

        header.getChildren().addAll(logoView, spacer, searchBox);


        /* Suggestions for search bar */

        ContextMenu suggestionsPopup = new ContextMenu();


        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 2) {
                suggestionsPopup.hide();
                return;
            }

            ObservableList<String> suggestions = (ObservableList<String>) DatabaseController.getMatchingCities(newValue);
            System.out.println("Suggestions: " + suggestions);
            if (suggestions.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);
                item.setOnAction(e -> {
                    searchBar.setText(suggestion);
                    suggestionsPopup.hide();

                });
                menuItems.add(item);
            }

            suggestionsPopup.getItems().setAll(menuItems);
            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(searchBar, Side.BOTTOM, 0, 0);
            }
        });


        searchBar.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                suggestionsPopup.hide();
            }
        });


        // Left section of the UI
        VBox upperLeft = new VBox();
        VBox lowerLeft = new VBox();

        leftMain.setTranslateX(25);

        Label currentDayLabel = new Label(currentDay);
        currentDayLabel.setStyle("-fx-font-size: 25px;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");


        Label currentCity = new Label(weatherController.getCity());
        currentCity.setPadding(new Insets(10, 10, 10, 20));
        currentCity.setStyle("-fx-font-size: 30px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");

        Image weatherIcon = new Image(weatherController.getIconUrl());
        ImageView weatherIconView = new ImageView(weatherIcon);
        System.out.println("Icon URL: " + weatherController.getIconUrl());
        weatherIconView.setFitHeight(150);
        weatherIconView.setFitWidth(150);
        weatherIconView.setTranslateX(30);
        weatherIconView.setTranslateY(30);

        Label description = new Label(weatherController.getDescription());
        description.setStyle("-fx-font-size: 25px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        description.setPadding(new Insets(60, 0, 0, 20));

        upperLeft.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;");

        Image clouds = new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/clouds.png"));
        ImageView cloudsView = new ImageView(clouds);
        cloudsView.setFitHeight(50);
        cloudsView.setFitWidth(50);
        Label cloudiness = new Label(String.format("Cloud coverage:\n               %d%%", weatherController.getCloudCoverage()));
        cloudiness.setAlignment(Pos.CENTER);
        cloudiness.setStyle("-fx-font-size: 25px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        lowerLeft.getChildren().addAll(cloudsView, cloudiness);
        lowerLeft.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;" );
        lowerLeft.setPrefHeight(200);
        lowerLeft.setTranslateY(20);
        lowerLeft.setAlignment(Pos.CENTER);
        leftMain.setPadding(new Insets(10));
        main.setPadding(new Insets(20,0,0,0));
        upperLeft.setPadding(new Insets(0,0,10,0));
        upperLeft.getChildren().addAll(currentDayLabel, currentCity, weatherIconView, description);
        leftMain.getChildren().addAll(upperLeft,lowerLeft);

        /* Mid part of main */


        VBox midMainBox = new VBox(10);
        midMainBox.setAlignment(Pos.CENTER);
        midMain.setTranslateX(45);


        VBox sunriseBox = new VBox();
        sunriseBox.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;");
        sunriseBox.setPadding(new Insets(10));
        sunriseBox.setMinWidth(150);
        sunriseBox.setAlignment(Pos.CENTER);

        Label sunriseLabel = new Label(String.format("Sunrise\n %s", weatherController.getSunrise()));
        sunriseLabel.setStyle("-fx-font-size: 25px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        sunriseBox.getChildren().add(sunriseLabel);

        VBox sunsetBox = new VBox();
        sunsetBox.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;");
        sunsetBox.setPadding(new Insets(10));
        sunsetBox.setMinWidth(150);
        sunsetBox.setAlignment(Pos.CENTER);

        Label sunsetLabel = new Label(String.format("Sunset\n  %s", weatherController.getSunset()));
        sunsetLabel.setStyle("-fx-font-size: 25px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        sunsetBox.getChildren().add(sunsetLabel);

        VBox feelsLikeBox = new VBox();
        feelsLikeBox.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;");
        feelsLikeBox.setPadding(new Insets(10));
        feelsLikeBox.setMinWidth(150);
        feelsLikeBox.setAlignment(Pos.CENTER);

        Label feelsLikeLabel = new Label(String.format(" Feels like\n        %.1f", weatherController.getFeelsLike()));
        feelsLikeLabel.setStyle("-fx-font-size: 25px;" +
                " -fx-text-fill: white;" +
                "-fx-font-family: 'THE BOLD FONT FREE VERSION';");
        feelsLikeBox.getChildren().add(feelsLikeLabel);


        midMainBox.getChildren().addAll(sunriseBox, sunsetBox, feelsLikeBox);
        midMainBox.setPadding(new Insets(0,0,0,20));


        midMain.getChildren().add(midMainBox);

        // Right section of the UI

        rightMain.setStyle("-fx-background-color: rgba(32,43,59,255);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 15px;" );
        rightMain.setTranslateX(90);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(15);


        ImageView img1 = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/thermometer.png")));
        img1.setFitHeight(30);
        img1.setFitWidth(30);
        Label text1 = new Label(String.format("Temperature: %.1f°C ", weatherController.getTemperature()));
        grid.add(img1, 0, 0);
        grid.add(text1, 1, 0);


        ImageView img5 = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/placeholder.png")));
        Label text2 = new Label(String.format("%s\n%s", weatherController.getCity(), weatherController.getCountry()));
        img5.setFitHeight(30);
        img5.setFitWidth(30);
        grid.add(img5, 0, 1);
        grid.add(text2, 1, 1);


        ImageView img2 = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/humidity.png")));
        img2.setFitHeight(30);
        img2.setFitWidth(30);
        Label text3 = new Label(String.format("Humidity: %.0f%%\nPressure: %.0fhPa", weatherController.getHumidity(), weatherController.getPressure()));
        grid.add(img2, 0, 2);
        grid.add(text3, 1, 2);


        ImageView img3 = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/wind.png")));
        img3.setFitHeight(30);
        img3.setFitWidth(30);
        Label text4 = new Label(String.format("Speed: %.0fm/s\nDirection: %s°", weatherController.getWindSpeed(), weatherController.getWindDirection()));
        grid.add(img3, 0, 3);
        grid.add(text4, 1, 3);


        ImageView img4 = new ImageView(new Image(getClass().getResourceAsStream("/com/finalproject/oopproject/cloudy.png")));
        img4.setFitHeight(30);
        img4.setFitWidth(30);
        Label text5 = new Label(String.format("Rain chance: %.0f%%\nVisibility: %dm", weatherController.getCurrentRainChance(), weatherController.getVisibility()));
        grid.add(img4, 0, 4);
        grid.add(text5, 1, 4);

        UIController.setFontSize(30, text1, text2, text3, text4, text5);

        rightMain.getChildren().add(grid);

        main.getChildren().addAll(leftMain, midMain, rightMain);
        main.setAlignment(Pos.CENTER);

        // Footer
        footer.setPadding(new Insets(20));
        footer.setSpacing(30);
        footer.setAlignment(Pos.CENTER);


        for (int i = 1; i < 6; i++) {
            VBox dayBox = new VBox();
            dayBox.setSpacing(5);
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPrefWidth(1200 / 7);


            LocalDate date = LocalDate.now().plusDays(i); //get day after today
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH); //convert to day of week

            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");


            String iconUrl = weatherController.getDailyIcon(i);
            ImageView iconView;
            Image iconImage = new Image(iconUrl, 50, 50, true, true);
            iconView = new ImageView(iconImage);


            double maxTemp = weatherController.getDailyMaxTemp(i);
            double minTemp = weatherController.getDailyMinTemp(i);
            Label tempLabel = new Label(String.format("High: %.1f°C\nLow: %.1f°C", maxTemp, minTemp));
            tempLabel.setStyle("-fx-text-fill: white;");

            dayBox.getChildren().addAll(dayLabel, iconView, tempLabel);
            footer.getChildren().add(dayBox);
        }

        searchButton.setOnAction(e -> {
            String cityName = searchBar.getText().trim();
            if (!cityName.isEmpty()) {
                try {
                    weatherController.setCity(cityName);
                    uiController.updateUI(weatherController, currentCity, description, weatherIconView, cloudiness, feelsLikeLabel, sunriseLabel, sunsetLabel, text1, text3, text4, text5,text2, footer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Weather App");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
