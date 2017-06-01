package ENSK.Windows;

import ENSK.ConnectionClass;
import ENSK.Email;
import ENSK.SaltAndHashPassword;
import ENSK.Username;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.sql.*;


/**
 * Created by Enathen on 2017-05-26.
 */
public class CreateNewAccount extends JFrame{

    private JPanel JPanelCreateNewAccount;
    private JButton createAccountButton;
    private JTextField emailTextField;
    private JTextField usernameTextField;
    private JCheckBox administratorCheckBox;
    private JPasswordField passwordPasswordField;
    private JPasswordField passwordPasswordField1;
    private JComboBox comboBox1;
    private JLabel emailIncorrectLabel;
    private JLabel passwordIncorrectLabel;
    private JLabel createAccountIncorrect;
    private JLabel userNameIncorrectLabel;
    private ConnectionClass connection = new ConnectionClass();
    /**
     * creates a new account
     */
    public CreateNewAccount() throws SQLException, ClassNotFoundException {
        initialize();
        comboBox1.addItem("Coop Forum Ersboda");

        /**
         * when create account button listener try to create a new account.
         */
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean working = true;
                String password = String.valueOf(passwordPasswordField.getPassword());
                String password2 = String.valueOf(passwordPasswordField1.getPassword());
                if(!password.equals(password2)){

                    passwordIncorrectLabel.setText("<html>Password incorrect!<br>not equals!</html>");
                    createAccountIncorrect.setVisible(true);
                    working = false;
                }else{
                    passwordIncorrectLabel.setText("");
                }
                if(!(passwordPasswordField.getPassword().length > 7)){
                    passwordIncorrectLabel.setText("<html>Password incorrect!<br>to short!</html>");
                    createAccountIncorrect.setVisible(true);
                    working = false;

                }
                for(char character : passwordPasswordField.getPassword()){

                    if(!((character >= 'A' && character <= 'z') || (character >= '1' && character <= '9'))){
                        passwordIncorrectLabel.setText("<html>Password incorrect!<br>cant allow character</html>");
                        working = false;

                    }
                }
                userNameIncorrectLabel.setVisible(false);
                for(int i = 0; i< usernameTextField.getText().length(); i++){
                    if(!(usernameTextField.getText().charAt(i) >= 'A' &&
                            usernameTextField.getText().charAt(i) <= 'z')){
                        userNameIncorrectLabel.setText("<html>Username incorrect!<br>cant allow character</html>");
                        userNameIncorrectLabel.setVisible(true);
                        working = false;

                    }
                }
                if(!(usernameTextField.getText().length() > 3)){
                    userNameIncorrectLabel.setText("<html>Username incorrect!<br>Atleast 3 char!</html>");
                    userNameIncorrectLabel.setVisible(true);
                    working = false;

                }
                Email email= null;
                try {
                    email = new Email(emailTextField.getText());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                if(!email.checkIfEmail()){
                    emailIncorrectLabel.setText("<html>Email incorrect!</html>");
                    emailIncorrectLabel.setVisible(true);
                    working = false;
                }else{
                    emailIncorrectLabel.setVisible(false);
                }
                try {
                    if(email.checkIfEmailExists()){
                        emailIncorrectLabel.setText("<html>Email already<br> Exists!</html>");
                        emailIncorrectLabel.setVisible(true);
                        working = false;
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                if(working){
                    try {
                        addUser();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * starts the form.
     */
    private void initialize(){
        JPanelCreateNewAccount.setMinimumSize(new Dimension(540,280));

        setContentPane(JPanelCreateNewAccount);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    /**
     * add a user.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void addUser() throws SQLException, ClassNotFoundException {
        Username username = new Username(usernameTextField.getText());

        if(!username.checkIfEqualUsername()){
            String sql = "INSERT INTO Account (id ,userName, email,workstation,admin, saltedHash, hash) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(2, username.userNameCorrect());
            preparedStatement.setString(3, emailTextField.getText().toLowerCase());

            if(String.valueOf(comboBox1.getSelectedItem()).endsWith("you work?")){
                preparedStatement.setString(4,null);
            }
            else {
                preparedStatement.setString(4, String.valueOf(comboBox1.getSelectedItem()));
            }
            preparedStatement.setBoolean(5, administratorCheckBox.isSelected());
            SaltAndHashPassword saltAndHashPassword = new
                    SaltAndHashPassword(String.valueOf(passwordPasswordField.getPassword()));
            preparedStatement.setString(6,saltAndHashPassword.createSalt());
            preparedStatement.setString(7, saltAndHashPassword.createHash());

            preparedStatement.execute();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        }
        userNameIncorrectLabel.setText("Username Exist!");
        userNameIncorrectLabel.setVisible(true);

    }
}
