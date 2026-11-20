package ui;

import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
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
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MainController:
 * - Hiển thị danh sách tất cả accounts
 * - Cho phép thêm mới, xem chi tiết, xóa account
 */
public class MainController implements Initializable {

    // Cửa sổ / Scene hiện tại
    private Stage stage;
    private Scene scene;

    // Danh sách tên account dùng cho ListView
    private final ObservableList<String> accountList = FXCollections.observableArrayList();

    // Ngân hàng chính của ứng dụng (đọc/ghi dữ liệu ở data/PostBank)
    private final PrivateBank globalBank = new PrivateBank("PostBank", "PostBank", 0.12, 0.09);

    // Text hiển thị thông báo phía dưới
    @FXML
    private Text text;

    // Nút "ADD" để thêm account mới
    @FXML
    private Button addButton;

    // ListView hiển thị danh sách accounts
    @FXML
    private ListView<String> accountsListView;

    // Root của MainView (dùng khi chuyển scene)
    @FXML
    private Parent root;

    // Lưu tên account đang được chọn
    private final AtomicReference<String> selectedAccount = new AtomicReference<>();

    public MainController() throws AccountAlreadyExistsException {
    }

    /**
     * Lấy tất cả account từ globalBank và đổ vào ListView
     * accountList là accounts từ globalBank, accountsListView có kiểu ListView
     */
    private void updateListView() {
        accountList.clear(); // nếu không clear thì UI sẽ bị trùng account
        accountList.addAll(globalBank.getAllAccounts());
        accountList.sort(Comparator.naturalOrder());
        accountsListView.setItems(accountList);
    }

    
    /**
     * Khởi tạo MainView sau khi FXML đã load:
     * - Nạp danh sách accounts
     * - Cài context menu
     * - Cài handler click ListView
     * - Cài handler nút ADD
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateListView();
        setupViewDeleteContextMenu();
        setupListViewClickHandler();
        setupAddButtonHandler();
    }

    /**
     * Cài đặt handler click cho ListView (chọn account + double-click để mở AccountView)
     */
    private void setupListViewClickHandler() {
        accountsListView.setOnMouseClicked(mouseEvent -> {
            String account = accountsListView.getSelectionModel().getSelectedItem();
            if (account == null) {
                return; // click vào vùng trống, không làm gì
            }

            // Lưu account đang chọn
            selectedAccount.set(account);
            System.out.println(account + " is selected");
            text.setText("Account [" + account + "] is selected");

            // Double-click: mở AccountView cho account đó
            if (mouseEvent.getClickCount() == 2) {
                Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                loadAndShowAccountView(account, currentStage);
            }
        });
    }

    /**
     * Tạo ContextMenu (View / Delete) và gắn handler
     */
    private void setupViewDeleteContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewAccount = new MenuItem("View account");
        MenuItem deleteAccount = new MenuItem("Delete account");

        contextMenu.getItems().addAll(viewAccount, deleteAccount);
        accountsListView.setContextMenu(contextMenu);

        setupViewAccountAction(viewAccount);
        setupDeleteAccountAction(deleteAccount);
    }

    /**
     * Gắn hành vi cho menu "View account" (chuột phải)
     */
    private void setupViewAccountAction(MenuItem viewAccount) {
        viewAccount.setOnAction(event -> {
            String account = selectedAccount.get();
            if (account == null || account.isEmpty()) {
                // chưa chọn account nào thì thôi không làm gì
                return;
            }

            Stage currentStage = (Stage) root.getScene().getWindow();
            loadAndShowAccountView(account, currentStage);
        });
    }

    /**
     * Gắn hành vi cho menu "Delete account" (chuột phải)
     */
    private void setupDeleteAccountAction(MenuItem deleteAccount) {
        deleteAccount.setOnAction(event -> {
            String account = selectedAccount.get();
            if (account == null || account.isEmpty()) {
                return;
            }

            // 1. Hộp thoại xác nhận
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete account confirmation");
            confirmation.setContentText("Do you want to delete this account?");
            Optional<ButtonType> result = confirmation.showAndWait();

            // 2. Nếu người dùng bấm OK thì mới xóa
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Gọi hàm xóa account trong PrivateBank
                    globalBank.deleteAccount(account);
                } catch (AccountDoesNotExistException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Cập nhật console + UI
                System.out.println(account + " is deleted");
                text.setText("Account [" + account + "] is deleted");

                // Cập nhật lại ListView để account vừa xóa biến mất
                updateListView();
            }
        });
    }

    /**
     * Load account-view.fxml và hiển thị AccountView cho account được chọn
     */
    private void loadAndShowAccountView(String account, Stage currentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource("account-view.fxml"));
            root = loader.load(); //tạo 1 cây Node chứa AnchorPane với nội dung là account-view

            AccountController accountController = loader.getController();
            accountController.setupData(globalBank, account);

            scene = new Scene(root);
            currentStage.setTitle("[" + account + "]");
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gắn handler cho nút ADD (mở dialog tạo account mới)
     */
    private void setupAddButtonHandler() {
        addButton.setOnMouseClicked(event -> {
            text.setText(""); // xóa message cũ

            Dialog<String> dialog = new Dialog<>(); // dialog nhập tên account

            dialog.setTitle("Add new account");
            dialog.setHeaderText("Add a new account to bank");
            dialog.getDialogPane().setMinWidth(300);

            Label nameLabel = new Label("Name: ");
            TextField nameTextField = new TextField();

            GridPane grid = new GridPane();
            grid.add(nameLabel, 2, 1);        // label "Name:"
            grid.add(nameTextField, 3, 1);    // ô nhập tên
            dialog.getDialogPane().setContent(grid);
            dialog.setResizable(true);

            ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

            // Xử lý khi bấm "Okay"
            dialog.setResultConverter(buttonType -> {
                if (buttonType == buttonTypeOk) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    if (!Objects.equals(nameTextField.getText(), "")) {
                        try {
                            // Thử tạo account mới
                            globalBank.createAccount(nameTextField.getText());
                            text.setText("Account [" + nameTextField.getText() + "] is added to the bank");
                        } catch (AccountAlreadyExistsException | IOException e) {
                            // Nếu tên bị trùng hoặc lỗi IO
                            alert.setContentText("Duplicated account!");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                text.setText("Account [" + nameTextField.getText() + "] is already in the bank!");
                            }
                            System.out.println(e.getMessage());
                        }
                        updateListView();
                    } else {
                        // Tên rỗng → báo lỗi
                        alert.setContentText("Please insert a valid name!");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            text.setText("No new account was added!");
                        }
                    }
                }
                return null;
            });

            dialog.show(); // hiển thị dialog
        });
    }
}
