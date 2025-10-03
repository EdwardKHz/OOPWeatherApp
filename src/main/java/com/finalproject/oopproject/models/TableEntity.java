package com.finalproject.oopproject.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TableEntity {
    private final ObjectProperty<Image> image;
    private final String[] description;

    public TableEntity(Image image, String[] description) {
        this.image = new SimpleObjectProperty<>(image);
        this.description = description;
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public Image getImage() {
        return image.get();
    }

    public String[] getDescription() {
        return description;
    }

    public String getDescriptionAsString() {
        return String.join(", ", description);
    }
}
