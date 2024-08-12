import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SquadManager extends JFrame {

    private JTable titularesTable, reservasTable, underSeventeenTable;
    private DefaultTableModel titularesModel, reservasModel, underSeventeenModel;

    public SquadManager() {
        setTitle("Squad Manager");
        setLayout(new BorderLayout());

        titularesModel = new DefaultTableModel(new Object[] {"Nome do Jogador", "Idade", "Posição do Jogador"}, 0);
        titularesTable = new JTable(titularesModel);
        JScrollPane titularesPane = new JScrollPane(titularesTable);
        titularesPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Titulares", TitledBorder.CENTER, TitledBorder.TOP
        ));

        reservasModel = new DefaultTableModel(new Object[]{"Nome do Jogador","Idade", "Posição do Jogador"}, 0);
        reservasTable = new JTable(reservasModel);
        JScrollPane reservaPane = new JScrollPane(reservasTable);
        reservaPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Reservas", TitledBorder.CENTER, TitledBorder.TOP
        ));

        underSeventeenModel = new DefaultTableModel(new Object[]{"Nome do Jogador", "Idade", "Posição do Jogador"}, 0);
        underSeventeenTable = new JTable(underSeventeenModel);
        JScrollPane underSeventeenPane = new JScrollPane(underSeventeenTable);
        underSeventeenPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Sub-17", TitledBorder.CENTER, TitledBorder.TOP
        ));


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
                addPlayerTest();
            }
        });

        editPlayerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editPlayer();
            }
        });

        deletePlayerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });

        movePlayerBtn.addActionListener(new ActionListener() {
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
    }

    private void addPlayerTest() {
        String[] options = {"Titulares", "Reservas", "Sub-17"};
        JComboBox<String> selectOption = new JComboBox<>(options);

        int result = JOptionPane.showConfirmDialog(this, selectOption,
                "Escolha para qual time vai adicionar o jogador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) selectOption.getSelectedItem();
            DefaultTableModel targetModel = null;

            String nomeJogador;
            int idadeJogador;
            String posicaoJogador;

            if (selected.equals("Titulares")) {
                targetModel = titularesModel;

                nomeJogador = JOptionPane.showInputDialog(this, "Qual o nome do jogador");
                String idadeJogadorStr = JOptionPane.showInputDialog(this, "Qual a idade do jogador");

                try {
                    idadeJogador = Integer.parseInt(idadeJogadorStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "A idade do jogador deve ser em formato númerico");
                    return;
                }

                posicaoJogador = JOptionPane.showInputDialog(this, "Qual a posicao do jogador");
                Player newPlayer = new Player(nomeJogador, idadeJogador, posicaoJogador);
                targetModel.addRow(new Object[]{newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao()});
            } else if (selected.equals("Reservas")) {
                targetModel = reservasModel;

                nomeJogador = JOptionPane.showInputDialog(this, "Qual o nome do jogador");
                String idadeJogadorStr = JOptionPane.showInputDialog(this, "Qual a idade do jogador");

                try {
                    idadeJogador = Integer.parseInt(idadeJogadorStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "A idade do jogador deve ser em formato númerico");
                    return;
                }

                posicaoJogador = JOptionPane.showInputDialog(this, "Qual a posicao do jogador");
                Player newPlayer = new Player(nomeJogador, idadeJogador, posicaoJogador);
                targetModel.addRow(new Object[]{newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao()});
            } else if (selected.equals("Sub-17")) {
                targetModel = underSeventeenModel;

                nomeJogador = JOptionPane.showInputDialog(this, "Qual o nome do jogador");
                String idadeJogadorStr = JOptionPane.showInputDialog(this, "Qual a idade do jogador");

                try {
                    idadeJogador = Integer.parseInt(idadeJogadorStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "A idade do jogador deve ser em formato númerico");
                    return;
                }

                posicaoJogador = JOptionPane.showInputDialog(this, "Qual a posicao do jogador");
                Player newPlayer = new Player(nomeJogador, idadeJogador, posicaoJogador);
                targetModel.addRow(new Object[]{newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao()});
            }
        }
    }

    private void movePlayers() {
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
            String[] options = {"Titulares", "Reservas", "Sub-17"};
            JComboBox<String> selectOption = new JComboBox<>(options);

            int result = JOptionPane.showConfirmDialog(this, selectOption,
                    "Escolha para qual time quer mover o jogador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);


            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) selectOption.getSelectedItem();
                DefaultTableModel targetModel = null;
                JTable targetTable = null;

                if (selected.equals("Titulares")) {
                    targetTable = titularesTable;
                    targetModel = titularesModel;
                } else if (selected.equals("Reservas")) {
                    targetTable = reservasTable;
                    targetModel = reservasModel;
                } else if (selected.equals("Sub-17")) {
                    targetTable = underSeventeenTable;
                    targetModel = underSeventeenModel;
                }

                if (targetModel != null && targetTable != selectedTable) {
                    int selectRow = selectedTable.getSelectedRow();

                    String nomeJogador = String.valueOf(selectedTableModel.getValueAt(selectRow, 0));
                    int idadeJogador = Integer.parseInt(String.valueOf(selectedTableModel.getValueAt(selectRow, 1)));
                    String posicaoJogador = String.valueOf(selectedTableModel.getValueAt(selectRow, 2));

                    if (targetTable == underSeventeenTable && idadeJogador > 17 && contarJogadoresMais17AnosNaTabelaSub17() >= 3) {
                        JOptionPane.showMessageDialog(this, "Só pode ter 3 jogadores com mais de 17 anos no sub-17");
                    } else {
                        targetModel.addRow(new Object[]{nomeJogador, idadeJogador, posicaoJogador});
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

    private void deletePlayer() {
        int [] selectedRows = {
                titularesTable.getSelectedRow(),
                reservasTable.getSelectedRow(),
                underSeventeenTable.getSelectedRow()
        };

        JTable selectedTable = getSelectedTable(selectedRows, titularesTable, reservasTable, underSeventeenTable);
        if (selectedTable != null) {
            DefaultTableModel selectedTableModel = getTableModel(selectedTable);
            if (selectedTableModel != null) {
                selectedTableModel.removeRow(selectedTable.getSelectedRow());
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um Jogador para deletar");
            }
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

    public static void main(String[] args) {
        new SquadManager();
    }
}
