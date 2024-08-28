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
                insertPlayerIntoDatabase(newPlayer, tableName);
                targetModel.addRow(new Object[] {newPlayer.getMatricula(), newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao(), newPlayer.getNumeroCamisa()});
            }
        }
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

    private void insertPlayerIntoDatabase(Player player, String tableName) {
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir o jogador " + e.getMessage());
        }
    }

    private void ensureTableExists(String tableName) {
        String CreateTableSql = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (id SERIAL PRIMARY KEY, " +
                "matricula INT, " +
                "nome VARCHAR(255) NOT NULL, " +
                "idade INT, " +
                "posicao VARCHAR(255) NOT NULL, " +
                "numero_camisa INT" +
                ")";
        try (Connection connection = DatabaseConnector.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateTableSql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar tabela na base de dados " + e.getMessage());
        }
    }

    private void movePlayers() {
        JTable selectedTable = null;
        DefaultTableModel selectedTableModel = null;
        String sourceTable = null;

        if (titularesTable.getSelectedRow() != -1) {
            selectedTable = titularesTable;
            selectedTableModel = titularesModel;
            sourceTable = "titulares";
        } else if (reservasTable.getSelectedRow() != -1) {
            selectedTable = reservasTable;
            selectedTableModel = reservasModel;
            sourceTable = "reservas";
        } else if (underSeventeenTable.getSelectedRow() != -1) {
            selectedTable = underSeventeenTable;
            selectedTableModel = underSeventeenModel;
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
                String targerTableName = null;

                if (selected.equals("Titulares")) {
                    targetTable = titularesTable;
                    targetModel = titularesModel;
                    targerTableName = "titulares";
                } else if (selected.equals("Reservas")) {
                    targetTable = reservasTable;
                    targetModel = reservasModel;
                    targerTableName = "reservas";
                } else if (selected.equals("Sub-17")) {
                    targetTable = underSeventeenTable;
                    targetModel = underSeventeenModel;
                    targerTableName = "sub17";
                }

                if (targetModel != null && targetTable != selectedTable) {
                    int selectRow = selectedTable.getSelectedRow();

                    int matricula = Integer.parseInt(String.valueOf(targetModel.getValueAt(selectRow, 0)));
                    String nomeJogador = String.valueOf(selectedTableModel.getValueAt(selectRow, 1));
                    int idadeJogador = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectRow, 2)));
                    String posicaoJogador = String.valueOf(selectedTableModel.getValueAt(selectRow, 3));
                    int numeroCamisa = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectRow, 4)));

                    if (targetTable == underSeventeenTable && idadeJogador > 17 && contarJogadoresMais17AnosNaTabelaSub17() >= 3) {
                        JOptionPane.showMessageDialog(this, "Só pode ter 3 jogadores com mais de 17 anos no sub-17");
                    } else {
                        movePlayerToAnotherDbTable(new Player(matricula,nomeJogador, idadeJogador, posicaoJogador, numeroCamisa), sourceTable, targerTableName);
                        targetModel.addRow(new Object[]{matricula, nomeJogador, idadeJogador, posicaoJogador, numeroCamisa});
                        selectedTableModel.removeRow(selectRow);
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
        JTable selectedTable = null;
        DefaultTableModel selectedTableModel = null;

        if (titularesTable.getSelectedRow() != -1) {
            selectedTable = titularesTable;
            selectedTableModel = titularesModel;
        } else if (reservasTable.getSelectedRow() != -1) {
            selectedTable = reservasTable;
            selectedTableModel = reservasModel;
        } else if (underSeventeenTable.getSelectedRow() != -1) {
            selectedTable = underSeventeenTable;
            selectedTableModel = underSeventeenModel;
        }

        if (selectedTable != null && selectedTableModel != null) {
            int selectedRow = selectedTable.getSelectedRow();
            if (selectedRow != -1) {
                int matricula = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectedRow, 0)));
                String nomeJogador = String.valueOf(selectedTableModel.getValueAt(selectedRow, 1));
                int idadeJogador = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectedRow, 2)));
                String posicaoJogador = String.valueOf(selectedTableModel.getValueAt(selectedRow, 3));
                int numeroCamisa = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectedRow, 4)));

                String tableName = null;

                if (titularesTable == selectedTable) {
                    tableName = "titulares";
                } else if (reservasTable == selectedTable) {
                    tableName = "reservas";
                } else if (underSeventeenTable == selectedTable) {
                    tableName = "sub17";
                }

                if (tableName != null) {
                    deletePlayerFromDbTable(new Player(matricula, nomeJogador, idadeJogador, posicaoJogador, numeroCamisa), tableName);
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
        String deleteQuery = "DELETE FROM " + tableName + " WHERE nome = ? AND idade = ? AND posicao = ?";
        try (Connection connection = DatabaseConnector.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery) ){
            preparedStatement.setString(1, player.getNome());
            preparedStatement.setInt(2, player.getIdade());
            preparedStatement.setString(3, player.getPosicao());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void editPlayer() {

        int [] selectedRows = {
                titularesTable.getSelectedRow(),
                reservasTable.getSelectedRow(),
                underSeventeenTable.getSelectedRow()
        };

        JTable selectedTable = getSelectedTable(selectedRows, titularesTable, reservasTable, underSeventeenTable);

        if (selectedTable != null) {
            DefaultTableModel selectedTableModel = getTableModel(selectedTable);
            if (selectedTableModel != null) {
                editInfoPlayersSelector(selectedTable.getSelectedRow(), selectedTable, selectedTableModel);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um Jogador para editar");
            }
        }

    }

    private JTable getSelectedTable(int[] selectedRow, JTable... tables) {
        for (int i = 0; i < tables.length; i++) {
            if (selectedRow[i] != -1) {
                return tables[i];
            }
        }
        return null;
    }

    private DefaultTableModel getTableModel(JTable table) {
        if (table == titularesTable) {
            return titularesModel;
        } else if (table == reservasTable) {
            return reservasModel;
        } else if (table == underSeventeenTable) {
            return underSeventeenModel;
        }
        return null;
    }

    private void editInfoPlayersSelector(int selectedRow, JTable table, DefaultTableModel model) {
        String nomeJogador;
        int idadeJogador;
        String posicaoJogador;

        nomeJogador = JOptionPane.showInputDialog(this, "Edite o nome do jogador:", table.getValueAt(selectedRow, 0));
        idadeJogador = Integer.parseInt(JOptionPane.showInputDialog(this, "Edite a idade do jogador:", table.getValueAt(selectedRow, 1)));
        posicaoJogador = JOptionPane.showInputDialog(this, "Edite a posição do jogador:", table.getValueAt(selectedRow, 2));

        model.setValueAt(nomeJogador, selectedRow, 0);
        model.setValueAt(idadeJogador, selectedRow, 1);
        model.setValueAt(posicaoJogador, selectedRow, 2);
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

    public static void main(String[] args) {
        new SquadManager();
    }
}