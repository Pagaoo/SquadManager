import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SquadManager extends JFrame {

    private JTable titularesTable, reservasTable, underSeventeenTable;
    private DefaultTableModel titularesModel, reservasModel, underSeventeenModel;

    public SquadManager() {
        setTitle("Squad Manager");
        setLayout(new BorderLayout());

        titularesModel = new DefaultTableModel(new Object[] {"Matricula","Nome do Jogador", "Idade", "Posição do Jogador", "Número da camisa"}, 0);
        titularesTable = new JTable(titularesModel);
        JScrollPane titularesPane = new JScrollPane(titularesTable);
        titularesPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Titulares", TitledBorder.CENTER, TitledBorder.TOP
        ));

        reservasModel = new DefaultTableModel(new Object[]{"Matricula","Nome do Jogador", "Idade", "Posição do Jogador", "Número da camisa"}, 0);
        reservasTable = new JTable(reservasModel);
        JScrollPane reservaPane = new JScrollPane(reservasTable);
        reservaPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Reservas", TitledBorder.CENTER, TitledBorder.TOP
        ));

        underSeventeenModel = new DefaultTableModel(new Object[]{"Matricula","Nome do Jogador", "Idade", "Posição do Jogador", "Número da camisa"}, 0);
        underSeventeenTable = new JTable(underSeventeenModel);
        JScrollPane underSeventeenPane = new JScrollPane(underSeventeenTable);
        underSeventeenPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Sub-17", TitledBorder.CENTER, TitledBorder.TOP
        ));

        titularesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        underSeventeenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableSelectionListener();

        JPanel buttonPanel = new JPanel();
        JButton editPlayerBtn = new JButton("Editar Jogador");
        JButton deletePlayerBtn = new JButton("Excluir Jogador");
        JButton movePlayerBtn = new JButton("Mover jogador");
        JButton addPlayerBtn = new JButton("Adicionar Jogador");

        buttonPanel.add(addPlayerBtn);
        buttonPanel.add(editPlayerBtn);
        buttonPanel.add(movePlayerBtn);
        buttonPanel.add(deletePlayerBtn);

        addPlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        editPlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPlayer();
            }
        });

        deletePlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });

        movePlayerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayers();
            }
        });

        add(titularesPane, BorderLayout.SOUTH);
        add(reservaPane, BorderLayout.WEST);
        add(underSeventeenPane, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.CENTER);

        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        createTables();
        loadDB();
    }

    private void createTables() {
        ensureTableExists("titulares");
        ensureTableExists("reservas");
        ensureTableExists("sub17");
    }

    private void loadDB() {
        try (Connection connection = DatabaseConnector.getConnection()){
            loadTableData(connection, "SELECT * FROM titulares", titularesModel);
            loadTableData(connection, "SELECT * FROM reservas", reservasModel);
            loadTableData(connection, "SELECT * FROM sub17", underSeventeenModel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTableData(Connection connection, String query, DefaultTableModel model) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            model.setRowCount(0);

            while (resultSet.next()) {
                int matricula = resultSet.getInt("matricula");
                String nome = resultSet.getString("nome");
                int idade = resultSet.getInt("idade");
                String posicao = resultSet.getString("posicao");
                int numeroCamisa = resultSet.getInt("numero_camisa");
                model.addRow(new Object[]{matricula, nome, idade, posicao, numeroCamisa});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void tableSelectionListener() {
        titularesTable.getSelectionModel().addListSelectionListener(new TableSelectionListener(titularesTable));
        reservasTable.getSelectionModel().addListSelectionListener(new TableSelectionListener(reservasTable));
        underSeventeenTable.getSelectionModel().addListSelectionListener(new TableSelectionListener(underSeventeenTable));
    }

    private class TableSelectionListener implements ListSelectionListener {
        private JTable currentTable;

        public TableSelectionListener(JTable table) {
            this.currentTable = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();

            if (!selectionModel.isSelectionEmpty()) {
                if (currentTable == titularesTable) {
                    reservasTable.getSelectionModel().clearSelection();
                    underSeventeenTable.getSelectionModel().clearSelection();
                }
                if (currentTable == reservasTable) {
                    titularesTable.getSelectionModel().clearSelection();
                    underSeventeenTable.getSelectionModel().clearSelection();
                }
                if (currentTable == underSeventeenTable) {
                    titularesTable.getSelectionModel().clearSelection();
                    reservasTable.getSelectionModel().clearSelection();
                }

            }
        }
    }

    private void ensureTableExists(String tableName) {
        String CreateTableSql = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (id SERIAL PRIMARY KEY, " +
                "matricula INT UNIQUE, " +
                "nome VARCHAR(255) NOT NULL, " +
                "idade INT, " +
                "posicao VARCHAR(255) NOT NULL, " +
                "numero_camisa INT UNIQUE" +
                ")";
        try (Connection connection = DatabaseConnector.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateTableSql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar tabela na base de dados " + e.getMessage());
        }
    }

    private static Player getPlayerData(DefaultTableModel tableModel, int selectedRow) {
        int matricula = Integer.parseInt(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
        String nomeJogador = String.valueOf(tableModel.getValueAt(selectedRow, 1));
        int idadeJogador = Integer.parseInt(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        String posicaoJogador = String.valueOf(tableModel.getValueAt(selectedRow, 3));
        int numeroCamisa = Integer.parseInt(String.valueOf(tableModel.getValueAt(selectedRow, 4)));


        return new Player(matricula, nomeJogador, idadeJogador, posicaoJogador, numeroCamisa);
    }

    private record TableSelection(JTable table, DefaultTableModel model) {
    }

    private static TableSelection getSelectedTable(JTable titularesTable, DefaultTableModel titularesModel,
                                                   JTable reservasTable, DefaultTableModel reservasModel,
                                                   JTable underSeventeenTable, DefaultTableModel underSeventeenModel) {

        if (titularesTable.getSelectedRow() != -1) {
            return new TableSelection(titularesTable, titularesModel);
        } else if (reservasTable.getSelectedRow() != -1) {
            return new TableSelection(reservasTable, reservasModel);
        } else if (underSeventeenTable.getSelectedRow() != -1) {
            return new TableSelection(underSeventeenTable, underSeventeenModel);
        }
        return null;
    }

    private Player playerData() {
        int matricula = Integer.parseInt(JOptionPane.showInputDialog(this, "Insira a matricula do jogador"));
        String nomeJogador = JOptionPane.showInputDialog(this, "Qual o nome do jogador");
        String idadeJogadorStr = JOptionPane.showInputDialog(this, "Qual a idade do jogador");
        int idadeJogador;
        try {
            idadeJogador = Integer.parseInt(idadeJogadorStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A idade do jogador deve estar em formato númerico");
            return null;
        }
        String posicaoJogador = JOptionPane.showInputDialog(this, "Qual a posicao do jogador");
        int numeroCamisa = Integer.parseInt(JOptionPane.showInputDialog(this, "Qual a numero de camisa do jogador"));

        return new Player(matricula, nomeJogador, idadeJogador, posicaoJogador, numeroCamisa);
    }

    private void addPlayer() {
        String[] options = {"Titulares", "Reservas", "Sub-17"};
        JComboBox<String> selectOption = new JComboBox<>(options);

        int result = JOptionPane.showConfirmDialog(this, selectOption,
                "Escolha para qual time vai adicionar o jogador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) selectOption.getSelectedItem();
            DefaultTableModel targetModel = null;
            String tableName = null;

            if (selected.equals("Titulares")) {
                targetModel = titularesModel;
                tableName = "titulares";
            } else if (selected.equals("Reservas")) {
                targetModel = reservasModel;
                tableName = "reservas";
            } else if (selected.equals("Sub-17")) {
                targetModel = underSeventeenModel;
                tableName = "sub_17";
            }

            Player newPlayer = playerData();
            if (newPlayer != null && targetModel != null && tableName != null) {
                boolean isInserted = insertPlayerIntoDatabase(newPlayer, tableName);
                if (isInserted) {
                    targetModel.addRow(new Object[] {newPlayer.getMatricula(), newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao(), newPlayer.getNumeroCamisa()});
                }
            }
        }
    }

    private boolean insertPlayerIntoDatabase(Player player, String tableName) {
        ensureTableExists(tableName);
        String sql = "INSERT INTO " + tableName + " (matricula, nome, idade, posicao, numero_camisa) VALUES (?,?,?,?,?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, player.getMatricula());
            preparedStatement.setString(2, player.getNome());
            preparedStatement.setInt(3, player.getIdade());
            preparedStatement.setString(4, player.getPosicao());
            preparedStatement.setInt(5, player.getNumeroCamisa());
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Jogador inserido com sucesso");
            return true;
        } catch (SQLException e) {
            //código para violação de UNIQUE no postgres
            if (e.getSQLState().equals("23505")) {
                String errorMessage = e.getMessage().toLowerCase();
                if (errorMessage.contains("matricula")) {
                    JOptionPane.showMessageDialog(this, "Matricula " + player.getMatricula() + " já existe!");
                } else {
                    JOptionPane.showMessageDialog(this, "Número de camisa " + player.getNumeroCamisa() + " já está atribuido a um jogador!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao inserir o jogador " + e.getMessage());
            }
        }
        return false;
    }

    private void movePlayers() {
        TableSelection selectedPanel = getSelectedTable(titularesTable, titularesModel, reservasTable, reservasModel,
                underSeventeenTable, underSeventeenModel);
        String sourceTable = null;

        JTable selectedTable = selectedPanel.table;
        DefaultTableModel selectedTableModel = selectedPanel.model;

        if (selectedTable == titularesTable) {
            sourceTable = "titulares";
        } else if (selectedTable == reservasTable) {
            sourceTable = "reservas";
        } else if (selectedTable == underSeventeenTable) {
            sourceTable = "sub17";
        }

        if (selectedTable != null && selectedTableModel != null) {
            String[] options = {"Titulares", "Reservas", "Sub-17"};
            JComboBox<String> selectOption = new JComboBox<>(options);

            int result = JOptionPane.showConfirmDialog(this, selectOption,
                    "Escolha para qual time quer mover o jogador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);


            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) selectOption.getSelectedItem();
                DefaultTableModel targetModel = null;
                JTable targetTable = null;
                String targertTableName = null;

                if (selected.equals("Titulares")) {
                    targetTable = titularesTable;
                    targetModel = titularesModel;
                    targertTableName = "titulares";
                } else if (selected.equals("Reservas")) {
                    targetTable = reservasTable;
                    targetModel = reservasModel;
                    targertTableName = "reservas";
                } else if (selected.equals("Sub-17")) {
                    targetTable = underSeventeenTable;
                    targetModel = underSeventeenModel;
                    targertTableName = "sub17";
                }

                if (targetModel != null && targetTable != selectedTable) {
                    int selectedRow = selectedTable.getSelectedRow();
                    Player player = getPlayerData(selectedTableModel, selectedRow);

                    if (targetTable == underSeventeenTable && player.getIdade() > 17 && contarJogadoresMais17AnosNaTabelaSub17() >= 3) {
                        JOptionPane.showMessageDialog(this, "Só pode ter 3 jogadores com mais de 17 anos no sub-17");
                    } else {
                        movePlayerToAnotherDbTable(player, sourceTable, targertTableName);
                        targetModel.addRow(new Object[]{player.getMatricula(), player.getNome(), player.getIdade(), player.getPosicao(), player.getNumeroCamisa()});
                        selectedTableModel.removeRow(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,"Escolha inválida, jogador já está nesse squad");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um jogador para mover");
        }
    }

    private void movePlayerToAnotherDbTable(Player player, String sourceTable, String targetTable) {
        insertPlayerIntoDatabase(player, targetTable);
        deletePlayerFromDbTable(player, sourceTable);
    }

    private void deletePlayer() {
       TableSelection selectedPanel = getSelectedTable(titularesTable, titularesModel, reservasTable, reservasModel,
               underSeventeenTable, underSeventeenModel);

        if (selectedPanel != null) {
            JTable selectedTable = selectedPanel.table;
            DefaultTableModel selectedTableModel = selectedPanel.model;
            int selectedRow = selectedTable.getSelectedRow();
            if (selectedRow != -1) {
                Player player = getPlayerData(selectedTableModel, selectedRow);
                String tableName = null;

                if (titularesTable == selectedTable) {
                    tableName = "titulares";
                } else if (reservasTable == selectedTable) {
                    tableName = "reservas";
                } else if (underSeventeenTable == selectedTable) {
                    tableName = "sub17";
                }

                if (tableName != null) {
                    deletePlayerFromDbTable(player, tableName);
                    selectedTableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this,"Jogador removido com sucesso");
                } else {
                    System.out.println( "Erro ao selecionar a tabela para excluir");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um jogador para remover");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum Jogador foi selecionado");
        }
    }

    private void deletePlayerFromDbTable(Player player, String tableName) {
        String deleteQuery = "DELETE FROM " + tableName + " WHERE matricula = ?";
        try (Connection connection = DatabaseConnector.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery) ){
            preparedStatement.setInt(1, player.getMatricula());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void editPlayer() {
        TableSelection selectedPanel = getSelectedTable(titularesTable, titularesModel, reservasTable, reservasModel,
                underSeventeenTable, underSeventeenModel);

        if (selectedPanel != null) {
            JTable selectedTable = selectedPanel.table;
            DefaultTableModel selectedTableModel = selectedPanel.model;
            int selectedRow = selectedTable.getSelectedRow();
            if (selectedRow != -1) {
                Player player = getPlayerData(selectedTableModel, selectedRow);

                JPanel editPanel = new JPanel(new GridLayout(5,2));
                JTextField nomeField = new JTextField(player.getNome());
                JTextField idadeField = new JTextField(String.valueOf(player.getIdade()));
                JTextField posicaoField = new JTextField(String.valueOf(player.getPosicao()));
                JTextField numeroCamisaField = new JTextField(String.valueOf(player.getNumeroCamisa()));

                editPanel.add(new JLabel("Nome:"));
                editPanel.add(nomeField);
                editPanel.add(new JLabel("Idade:"));
                editPanel.add(idadeField);
                editPanel.add(new JLabel("Posicao:"));
                editPanel.add(posicaoField);
                editPanel.add(new JLabel("Número da Camisa:"));
                editPanel.add(numeroCamisaField);

                int res = JOptionPane.showConfirmDialog(this, editPanel, "Editar Jogador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    String novoNome = nomeField.getText();
                    int novaIdade = Integer.parseInt(idadeField.getText());
                    String novaPosicao = posicaoField.getText();
                    int novoNumeroCamisa = Integer.parseInt(numeroCamisaField.getText());

                    Player editPlayer = new Player(player.getMatricula(), novoNome, novaIdade, novaPosicao, novoNumeroCamisa);

                    String tableName = null;

                    if (titularesTable == selectedTable) {
                        tableName = "titulares";
                    } else if (reservasTable == selectedTable) {
                        tableName = "reservas";
                    } else if (underSeventeenTable == selectedTable) {
                        tableName = "sub17";
                    }

                    if (tableName != null) {
                        editPlayerInDb(editPlayer, tableName);
                        selectedTableModel.setValueAt(novoNome, selectedRow, 1);
                        selectedTableModel.setValueAt(novaIdade, selectedRow, 2);
                        selectedTableModel.setValueAt(novaPosicao, selectedRow, 3);
                        selectedTableModel.setValueAt(novoNumeroCamisa, selectedRow, 4);
                        JOptionPane.showMessageDialog(this, "Jogador editado com sucesso");
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao selecionar jogador para editar");
                    }

                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um jogador para editar");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum Jogador foi selecionado");
        }

    }

    private void editPlayerInDb(Player player, String tableName) {
        String editQuery = "UPDATE " + tableName + " SET nome = ?, idade = ?, posicao = ?, numero_camisa = ? WHERE matricula = ?";
        try (Connection connection = DatabaseConnector.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(editQuery)){
            preparedStatement.setString(1, player.getNome());
            preparedStatement.setInt(2, player.getIdade());
            preparedStatement.setString(3, player.getPosicao());
            preparedStatement.setInt(4, player.getNumeroCamisa());
            preparedStatement.setInt(5, player.getMatricula());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int contarJogadoresMais17AnosNaTabelaSub17() {
        int count = 0;

        for (int i = 0; i < underSeventeenTable.getRowCount(); i++) {
            int idade = Integer.parseInt(underSeventeenTable.getValueAt(i, 1).toString());
            if (idade > 17) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        new SquadManager();
    }
}