/*
 * TFWC.java - This file is part of TFWC
 * Copyright (C) 2016  Thomas Kuenneth
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.tfwc;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Welcome to That Famous WebComic. The aim of this app is to read recent XKCD
 * comics.
 *
 * @see http://xkcd.com/
 * @author Thomas Kuenneth
 */
public class TFWC extends Application {

    private static final Logger LOGGER = Logger.getLogger(TFWC.class.getName());
    private static final ResourceBundle RESOURCES
            = ResourceBundle.getBundle(TFWC.class.getName());
    private static final Insets INSETS = new Insets(10, 10, 10, 10);

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getString("name"));
        createUI(primaryStage);
    }

    private void createUI(Stage stage) {
        final ScrollPane center = new ScrollPane();
        Text summary = new Text();
        Font currentFont = summary.getFont();
        summary.setFont(Font.font(currentFont.getFamily(), currentFont.getSize() * 1.5));

        ImageView comicStrip = new ImageView();
        comicStrip.setSmooth(true);
        comicStrip.setPreserveRatio(true);
        ComboBox<Comic> comicChooser = new ComboBox<>();
        comicChooser.valueProperty().addListener((ov, oldC, newC) -> {
            comicStrip.setImage(newC.image);
            summary.setText(newC.summary);
            summary.setWrappingWidth(center.getWidth() - INSETS.getLeft() - INSETS.getRight());
        });

        FlowPane top = new FlowPane(comicChooser);
        top.setPadding(INSETS);

        VBox summaryParent = new VBox(summary);
        summaryParent.setPadding(INSETS);
        VBox centerContents = new VBox(comicStrip, summaryParent);
        center.setContent(centerContents);
        center.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        center.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        center.viewportBoundsProperty().addListener((ov, oldSize, newSize) -> {
            double width = newSize.getWidth();
            comicStrip.setFitWidth(width);
            summary.setWrappingWidth(width - INSETS.getLeft() - INSETS.getRight());
        });

        final Provider p = new Provider();
        Task task = new Task<List<Comic>>() {

            @Override
            public List<Comic> call() {
                return p.getSortedListOfComics();
            }

            @Override
            protected void succeeded() {
                try {
                    List<Comic> comics = get();
                    BorderPane root;
                    if (comics.size() > 0) {
                        boolean first = true;
                        for (Comic current : comics) {
                            current.image = new Image(current.url, !first);
                            comicChooser.getItems().add(current);
                            if (first) {
                                first = false;
                                comicChooser.getSelectionModel().select(current);
                            }
                        }
                        root = new BorderPane(center, top, null, null, null);
                    } else {
                        Text info = new Text(getString("error"));
                        root = new BorderPane(info);
                        root.setPadding(INSETS);
                    }
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.sizeToScene();
                    stage.show();
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, "succeeded()", ex);
                }
            }
        };
        new Thread(task).start();
    }

    private String getString(String key) {
        if (RESOURCES.containsKey(key)) {
            return RESOURCES.getString(key);
        }
        return "???";
    }
}
