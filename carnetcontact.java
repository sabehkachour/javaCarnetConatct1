package com.example.carnet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class carnetcontact extends Application {

    /// Liste contenant les objets de type Contact
    private List<Contact> contacts = new ArrayList<>();

    /// Chemin du fichier JSON pour la sauvegarde
    private final String FILE_PATH = "contacts.json";

    /// Objet Gson pour la conversion JSON ↔ Objet Java
    private final Gson gson = new Gson();

    /// Méthode principale qui démarre l'application JavaFX
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Carnet de Contacts");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Téléphone");


        ListView<String> contactListView = new ListView<>();
        /// Hauteur fixe de 300
        contactListView.setPrefHeight(300);
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");


        Button addButton = new Button("Ajouter");
        Button deleteButton = new Button("Supprimer");
        Button searchButton = new Button("Rechercher");

        /// Champ de recherche
        TextField searchField = new TextField();
        ///Cette ligne affiche un texte gris clair à l’intérieur du champ
        searchField.setPromptText("Rechercher par prénom, nom ou téléphone");

        VBox layout = new VBox(10, firstNameField, nameField, phoneField,errorLabel ,addButton, deleteButton, searchField, searchButton, contactListView);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);



        layout.setStyle("-fx-background-color: #f0f8ff;");
        firstNameField.setStyle("-fx-background-color: white; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-padding: 5;");
        nameField.setStyle("-fx-background-color: white; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-padding: 5;");
        phoneField.setStyle("-fx-background-color: white; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-padding: 5;");
        searchField.setStyle("-fx-background-color: #FFFACD; -fx-border-color: #DAA520; -fx-border-radius: 5; -fx-padding: 5;");


        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");


        contactListView.setStyle("-fx-border-color: #4682B4; -fx-border-radius: 5;");

        ///Bouton Ajouter
        addButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String name = nameField.getText();
            String phone = phoneField.getText();
            if (!firstName.matches("[a-zA-Z]+")) {
                errorLabel.setText("Prénom invalide. Lettres uniquement.");
                firstNameField.clear();
                return;}
            if (!name.matches("[a-zA-Z]+")) {
                errorLabel.setText("Nom invalide. Lettres uniquement.");
                nameField.clear();
                return;}
            if (!phone.matches("\\d{8}")) {
                errorLabel.setText("Téléphone invalide. 8 chiffres requis.");
                phoneField.clear();
                return;}
            for (Contact c : contacts) {

                if (c.getPhone().equals(phone)) {
                    errorLabel.setText("Ce téléphone existe déjà !");
                    return;}}
            Contact contact = new Contact(firstName, name, phone);
            contacts.add(contact);
            saveContactsToFile();
            updateContactListView(contactListView);
            firstNameField.clear();
            nameField.clear();
            phoneField.clear();
            /// Supprimer l’erreur
            errorLabel.setText("");
        });

        deleteButton.setOnAction(e -> {
            String selectedContact = contactListView.getSelectionModel().getSelectedItem();

            if (selectedContact != null) {
                Contact contactToRemove = null;
                for (Contact contact : contacts) {
                    if (contact.toString().equals(selectedContact)) {
                        contactToRemove = contact;
                        break;}}
                if (contactToRemove != null) {
                    contacts.remove(contactToRemove);
                    updateContactListView(contactListView);
                    saveContactsToFile();}
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Aucun contact sélectionné à supprimer.");
                /// affichage de message
                alert.show();
            }});
        searchButton.setOnAction(e -> {
            String searchQuery = searchField.getText().toLowerCase();
            List<Contact> filteredContacts = new ArrayList<>();

            for (Contact contact : contacts) {
                if (contact.getFirstName().toLowerCase().contains(searchQuery) ||
                        contact.getName().toLowerCase().contains(searchQuery) ||
                        contact.getPhone().toLowerCase().contains(searchQuery)) {
                    filteredContacts.add(contact);
                }
            }
            updateFilteredContactListView(contactListView, filteredContacts);
        });

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);

        loadContactsFromFile();/// Charger les contacts depuis le fichier :
        updateContactListView(contactListView); ///Afficher les contacts dans la liste :
        primaryStage.show(); /// Affiche la fenêtre
    }

    private void updateContactListView(ListView<String> listView) {
        listView.getItems().clear(); /// Vide l'affichage
        for (Contact contact : contacts) {
            listView.getItems().add(contact.toString());
        }
    }

    ///Affiche uniquement les contacts filtrés
    private void updateFilteredContactListView(ListView<String> listView, List<Contact> filteredContacts) {
        listView.getItems().clear(); /// Vide la liste
        for (Contact contact : filteredContacts) {
            listView.getItems().add(contact.toString());
        }
    }
    private void loadContactsFromFile() {
        /// try ferme automatiquement le fichier après utilisation
        try (Reader reader = new FileReader(FILE_PATH)) {
            ///  convertir le contenu JSON du fichier en une liste d’objets Contact
            Type listType = new TypeToken<ArrayList<Contact>>() {}.getType();
            contacts = gson.fromJson(reader, listType);

            /// Si le fichier est vide ou invalide
            if (contacts == null) {
                contacts = new ArrayList<>();
            }
        } catch (IOException e) {
            /// Si le fichier n'existe pas, on ignore l'erreur
        }
    }
    private void saveContactsToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(contacts, writer);
        } catch (IOException e) {
            e.printStackTrace(); /// Affiche l'erreur en cas de problème
        }
    }

    public static class Contact {
        private String firstName;
        private String name;
        private String phone;

        public Contact(String firstName, String name, String phone) {
            this.firstName = firstName;
            this.name = name;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return firstName + " " + name + " - " + phone;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }
    }
}
