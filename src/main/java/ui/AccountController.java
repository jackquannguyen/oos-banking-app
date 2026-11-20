package ui;

import bank.*;
import bank.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AccountController implements Initializable {

    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private PrivateBank bank;

    @FXML
    public Text text;
    @FXML
    public MenuButton addButton;
    @FXML
    public MenuItem payment;
    @FXML
    public MenuItem transfer;
    @FXML
    public Parent root;
    @FXML
    public MenuButton optionsButton;
    @FXML
    public MenuItem allTransaction;
    @FXML
    public MenuItem ascending;
    @FXML
    public MenuItem descending;
    @FXML
    public MenuItem positive;
    @FXML
    public MenuItem negative;
    @FXML
    public ListView<Transaction> transactionsListView;
    @FXML
    public Text accountName;
    @FXML
    public Button backButton;

    // Cập nhật ListView transactions theo list được truyền vào
    private void updateListView(List<Transaction> listTransaction) {
        transactionsList.clear();
        transactionsList.addAll(listTransaction);
        transactionsListView.setItems(transactionsList);
    }

    /**
     * Hàm này được MainController gọi khi mở AccountView cho 1 account cụ thể:
     * - Lưu lại bank & tên account
     * - Hiển thị tên + số dư account
     * - Hiển thị danh sách giao dịch ban đầu
     * - Cài context menu, sort/filter, và menu Add
     */
    public void setupData(PrivateBank privateBank, String name) {
        bank = privateBank;
        accountName.setText(name + " [" + bank.getAccountBalance(name) + "€]");
        updateListView(bank.getTransactions(name));

        setupDeleteContextMenu(name);
        setupSortAndFilter(name);
        setupAddButton(name);
    }

    /**
     * initialize() của AccountView:
     * - Chỉ cài nút Back để quay lại MainView
     * (còn dữ liệu account được truyền sau qua setupData)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backButton.setOnMouseClicked(mouseEvent -> {
            try {
                root = FXMLLoader.load(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResource("main-view.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setTitle("MainView");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });
    }

    /**
     * Dialog thêm Payment – xử lý mọi lỗi input bằng Alert
     */
    private void showAddPaymentDialog(String account) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add new payment");
        dialog.setHeaderText("Add a new payment to account [" + account + "]");
        dialog.getDialogPane().setMinWidth(350);
        dialog.getDialogPane().setMinHeight(250);

        GridPane gridPane = new GridPane();

        Label dateLabel = new Label("Date: ");
        Label descriptionLabel = new Label("Description: ");
        Label amountLabel = new Label("Amount: ");
        Label incomingInterestLabel = new Label("Incoming interest: ");
        Label outgoingInterestLabel = new Label("Outgoing interest: ");

        TextField dateText = new TextField();
        TextField descriptionText = new TextField();
        TextField amountText = new TextField();
        TextField incomingInterestText = new TextField();
        TextField outgoingInterestText = new TextField();

        gridPane.addRow(0, dateLabel, dateText);
        gridPane.addRow(1, descriptionLabel, descriptionText);
        gridPane.addRow(2, amountLabel, amountText);
        gridPane.addRow(3, incomingInterestLabel, incomingInterestText);
        gridPane.addRow(4, outgoingInterestLabel, outgoingInterestText);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResizable(true);

        ButtonType okButton = new ButtonType("ADD", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                Alert invalid = new Alert(Alert.AlertType.ERROR);

                // 1) Kiểm tra rỗng
                if (dateText.getText().isBlank()
                        || descriptionText.getText().isBlank()
                        || amountText.getText().isBlank()
                        || incomingInterestText.getText().isBlank()
                        || outgoingInterestText.getText().isBlank()) {

                    invalid.setContentText("Please insert all values!");
                    invalid.showAndWait();
                    text.setText("No new payment was added!");
                    return null;
                }

                // 2) Kiểm tra kiểu số
                double amount, inInterest, outInterest;
                try {
                    amount = Double.parseDouble(amountText.getText());
                    inInterest = Double.parseDouble(incomingInterestText.getText());
                    outInterest = Double.parseDouble(outgoingInterestText.getText());
                } catch (NumberFormatException e) {
                    invalid.setContentText("Amount and interests must be numeric values!");
                    invalid.showAndWait();
                    text.setText("No new payment was added!");
                    return null;
                }

                // 3) Tạo Payment (có thể ném TransferAmountException)
                Payment newPayment;
                try {
                    newPayment = new Payment(
                            dateText.getText(),
                            descriptionText.getText(),
                            amount,
                            inInterest,
                            outInterest);
                } catch (TransferAmountException e) {
                    invalid.setContentText("Invalid amount/interest values: " + e.getMessage());
                    invalid.showAndWait();
                    text.setText("No new payment was added!");
                    return null;
                }

                // 4) Thêm vào bank, xử lý lỗi bằng Alert
                try {
                    bank.addTransaction(account, newPayment);
                    text.setText("A new payment is added");
                    updateListView(bank.getTransactions(account));
                    accountName.setText(account + " [" + bank.getAccountBalance(account) + "€]");
                } catch (TransactionAlreadyExistException e) {
                    invalid.setContentText("Duplicated payment!");
                    invalid.showAndWait();
                    text.setText("This new payment is already in the bank!");
                } catch (AccountDoesNotExistException e) {
                    invalid.setContentText("Account does not exist!");
                    invalid.showAndWait();
                } catch (IOException | TransferAmountException e) {
                    invalid.setContentText("Error while saving the payment!");
                    invalid.showAndWait();
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Dialog thêm Transfer.
     * - Nếu account hiện tại là Sender → OutgoingTransfer cho account,
     * IncomingTransfer cho tài khoản nhận (nếu tồn tại).
     * - Nếu account hiện tại là Recipient → IncomingTransfer cho account,
     * OutgoingTransfer cho tài khoản gửi (nếu tồn tại).
     */
    private void showAddTransferDialog(String account) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add new transfer");
        dialog.setHeaderText("Add a new transfer to account [" + account + "]");
        dialog.getDialogPane().setMinWidth(350);
        dialog.getDialogPane().setMinHeight(250);

        GridPane gridPane = new GridPane();

        Label dateLabel = new Label("Date: ");
        Label descriptionLabel = new Label("Description: ");
        Label amountLabel = new Label("Amount: ");
        Label senderLabel = new Label("Sender: ");
        Label recipientLabel = new Label("Recipient: ");

        TextField dateText = new TextField();
        TextField descriptionText = new TextField();
        TextField amountText = new TextField();
        TextField senderText = new TextField();
        TextField recipientText = new TextField();

        gridPane.addRow(0, dateLabel, dateText);
        gridPane.addRow(1, descriptionLabel, descriptionText);
        gridPane.addRow(2, amountLabel, amountText);
        gridPane.addRow(3, senderLabel, senderText);
        gridPane.addRow(4, recipientLabel, recipientText);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResizable(true);

        ButtonType okButton = new ButtonType("ADD", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                Alert invalid = new Alert(Alert.AlertType.ERROR);

                // 1) Kiểm tra rỗng
                if (dateText.getText().isBlank()
                        || descriptionText.getText().isBlank()
                        || amountText.getText().isBlank()
                        || senderText.getText().isBlank()
                        || recipientText.getText().isBlank()) {

                    invalid.setContentText("Please insert all values!");
                    invalid.showAndWait();
                    text.setText("No new transfer was added!");
                    return null;
                }

                // 2) Kiểm tra kiểu số cho amount
                double amount;
                try {
                    amount = Double.parseDouble(amountText.getText());
                } catch (NumberFormatException e) {
                    invalid.setContentText("Amount must be a numeric value!");
                    invalid.showAndWait();
                    text.setText("No new transfer was added!");
                    return null;
                }

                String sender = senderText.getText().trim();
                String recipient = recipientText.getText().trim();

                boolean isOutgoing = sender.equals(account) && !recipient.equals(account);
                boolean isIncoming = !sender.equals(account) && recipient.equals(account);

                if (!isOutgoing && !isIncoming) {
                    invalid.setContentText(
                            "Current account must be either sender or recipient (but not both).");
                    invalid.showAndWait();
                    text.setText("No new transfer was added!");
                    return null;
                }

                Transaction mainTransfer;
                Transaction counterpartTransfer = null;

                try {
                    // Transfer cho account hiện tại
                    if (isOutgoing) {
                        mainTransfer = new OutgoingTransfer(
                                dateText.getText(),
                                descriptionText.getText(),
                                amount,
                                sender,
                                recipient);
                    } else { // isIncoming
                        mainTransfer = new IncomingTransfer(
                                dateText.getText(),
                                descriptionText.getText(),
                                amount,
                                sender,
                                recipient);
                    }
                } catch (TransferAmountException e) {
                    invalid.setContentText("Invalid amount value: " + e.getMessage());
                    invalid.showAndWait();
                    text.setText("No new transfer was added!");
                    return null;
                }

                // Xác định tài khoản còn lại (otherAccount)
                String otherAccount = isOutgoing ? recipient : sender;
                boolean otherExists = bank.getAllAccounts().contains(otherAccount);

                if (otherExists) {
                    try {
                        // Nếu account là sender → otherAccount nhận tiền
                        if (isOutgoing) {
                            counterpartTransfer = new IncomingTransfer(
                                    dateText.getText(),
                                    descriptionText.getText(),
                                    amount,
                                    sender,
                                    recipient);
                        } else {
                            // account là recipient → otherAccount gửi tiền
                            counterpartTransfer = new OutgoingTransfer(
                                    dateText.getText(),
                                    descriptionText.getText(),
                                    amount,
                                    sender,
                                    recipient);
                        }
                    } catch (TransferAmountException e) {
                        invalid.setContentText("Invalid amount value for counterpart transfer: " + e.getMessage());
                        invalid.showAndWait();
                        text.setText("No new transfer was added!");
                        return null;
                    }
                }

                // 3) Ghi vào bank
                try {
                    // Thêm cho account hiện tại
                    bank.addTransaction(account, mainTransfer);

                    // Nếu tài khoản bên kia cũng tồn tại trong PrivateBank, thêm giao dịch tương
                    // ứng
                    if (otherExists && counterpartTransfer != null) {
                        try {
                            bank.addTransaction(otherAccount, counterpartTransfer);
                        } catch (TransactionAlreadyExistException | AccountDoesNotExistException
                                | IOException | TransferAmountException e) {
                            // Rollback mainTransfer cho đẹp
                            try {
                                bank.removeTransaction(account, mainTransfer);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            invalid.setContentText("Error while saving transfer for other account: " + e.getMessage());
                            invalid.showAndWait();
                            text.setText("No new transfer was added!");
                            return null;
                        }
                    }

                    text.setText("A new transfer is added");
                    updateListView(bank.getTransactions(account));
                    accountName.setText(account + " [" + bank.getAccountBalance(account) + "€]");

                } catch (TransactionAlreadyExistException e) {
                    invalid.setContentText("Duplicated transfer for this account!");
                    invalid.showAndWait();
                    text.setText("This new transfer is already in the bank!");
                } catch (AccountDoesNotExistException e) {
                    invalid.setContentText("Account does not exist!");
                    invalid.showAndWait();
                } catch (IOException | TransferAmountException e) {
                    invalid.setContentText("Error while saving the transfer!");
                    invalid.showAndWait();
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Cài context menu (Delete) cho ListView transactions
     */
    private void setupDeleteContextMenu(String name) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteTransaction = new MenuItem("Delete transaction");
        contextMenu.getItems().addAll(deleteTransaction);
        transactionsListView.setContextMenu(contextMenu);

        AtomicReference<Transaction> selectedTransaction = new AtomicReference<>();

        // Chọn transaction trong ListView
        transactionsListView.setOnMouseClicked(mouseEvent -> {
            Transaction selected = transactionsListView.getSelectionModel().getSelectedItem();
            selectedTransaction.set(selected);
            if (selected != null) {
                System.out.println("[" + selected.toString().replace("\n", "]"));
            }
        });

        // Xóa transaction khi chọn menu Delete
        deleteTransaction.setOnAction(event -> {
            Transaction current = selectedTransaction.get();
            if (current == null) {
                Alert invalid = new Alert(Alert.AlertType.WARNING);
                invalid.setContentText("Please select a transaction first!");
                invalid.showAndWait();
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete transaction confirmation");
            confirmation.setContentText("Do you wanna delete this transaction?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    bank.removeTransaction(name, current);
                } catch (TransactionDoesNotExistException | IOException | AccountDoesNotExistException e) {
                    e.printStackTrace();
                }
                System.out.println("[" + current.toString().replace("\n", "]") + " is deleted");
                text.setText(current.toString().replace("\n", "]") + " is deleted");
                updateListView(bank.getTransactions(name));
                accountName.setText(name + " [" + bank.getAccountBalance(name) + "€]");
            }
        });
    }

    /**
     * Cài các nút Sort / Filter cho danh sách giao dịch
     */
    private void setupSortAndFilter(String name) {
        allTransaction.setOnAction(event -> updateListView(bank.getTransactions(name)));
        ascending.setOnAction(event -> updateListView(bank.getTransactionsSorted(name, true)));
        descending.setOnAction(event -> updateListView(bank.getTransactionsSorted(name, false)));
        positive.setOnAction(event -> updateListView(bank.getTransactionsByType(name, true)));
        negative.setOnAction(event -> updateListView(bank.getTransactionsByType(name, false)));
    }

    /**
     * Cài nút Add (Payment / Transfer)
     */
    private void setupAddButton(String name) {
        payment.setOnAction(event -> showAddPaymentDialog(name));
        transfer.setOnAction(event -> showAddTransferDialog(name));
    }
}
