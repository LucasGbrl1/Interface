import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroViewController {
    @FXML
    private TextField nomeField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private PasswordField confirmarSenhaField;

    @FXML
    private Button cadastrarButton;

    @FXML
    private Label situacaoLabel;

    @FXML
    private TableView<Usuario> tableView;

    @FXML
    private TableColumn<Usuario, String> nomeColumn;

    @FXML
    private TableColumn<Usuario, String> emailColumn;

    private ObservableList<Usuario> usuarioList;

    @FXML
    private void initialize() {
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usuarioList = FXCollections.observableArrayList();
        tableView.setItems(usuarioList);
        carregarUsuarios();
    }

    @FXML
    private void handleCadastrar() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String senha = senhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();

        if (senha.equals(confirmarSenha)) {
            inserirDadosNoBanco(nome, email, senha);
            carregarUsuarios();
        } else {
            situacaoLabel.setText("As senhas não coincidem. Tente novamente.");
        }
    }

    private void inserirDadosNoBanco(String nome, String email, String senha) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:Usuarios.db");
            String sql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, senha);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                situacaoLabel.setText("Usuário cadastrado com sucesso!");
            } else {
                situacaoLabel.setText("Erro ao cadastrar usuário.");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            situacaoLabel.setText("Erro ao conectar ao banco de dados.");
        }
    }

    private void carregarUsuarios() {
        usuarioList.clear();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:Usuarios.db");
            String sql = "SELECT * FROM usuarios";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                usuarioList.add(new Usuario(nome, email));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            situacaoLabel.setText("Erro ao carregar usuários.");
        }
    }
}
