import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SquadManager extends JFrame {

    private JTable titularesTable, reservasTable;
    private DefaultTableModel titularesModel, reservasModel;

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

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Adicionar Jogador");
        JButton editButton = new JButton("Editar Jogador");
        JButton deleteButton = new JButton("Remover Jogador");
        JButton moveButton = new JButton("Mover Jogador para o time reserva");
        JButton moveButton2 = new JButton("Mover Jogador para o time titular");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(moveButton2);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editPlayer();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });

        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlayerToSecondSquad();
            }
        });

        moveButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movePlayerToFirstSquad();
            }
        });

        add(titularesPane, BorderLayout.WEST);
        add(reservaPane, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.CENTER);

        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void movePlayerToFirstSquad() {
        selectRow(reservasTable, reservasModel, titularesModel);
    }

    private void movePlayerToSecondSquad() {
        selectRow(titularesTable, titularesModel, reservasModel);
    }

    private void selectRow(JTable table, DefaultTableModel actualTable, DefaultTableModel toTable) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nomeJogador = actualTable.getValueAt(selectedRow, 0).toString();
            int idadeJogador = Integer.parseInt(actualTable.getValueAt(selectedRow, 1).toString());
            String posicaoJogador = actualTable.getValueAt(selectedRow, 2).toString();

            toTable.addRow(new Object[]{nomeJogador,idadeJogador, posicaoJogador});
            actualTable.removeRow(selectedRow);
        }
    }

    private void deletePlayer() {
        int selectedRow = titularesTable.getSelectedRow();
        if (selectedRow != -1) {
            titularesModel.removeRow(selectedRow);
        }
    }

    private void editPlayer() {
        int selectedRow = titularesTable.getSelectedRow();
        if (selectedRow != -1) {
            String nomeJogador = JOptionPane.showInputDialog(this,"Edite o nome do jogador:", titularesTable.getValueAt(selectedRow, 0));
            String posicaoJogador = JOptionPane.showInputDialog(this,"Edite a posição do jogador:", titularesTable.getValueAt(selectedRow, 1));
            titularesModel.setValueAt(nomeJogador, selectedRow, 0);
            titularesModel.setValueAt(posicaoJogador, selectedRow, 1);
        }
    }

    private void addPlayer() {
        String nomeJogador = JOptionPane.showInputDialog(this, "Qual o nome do jogador");
        int idadeJogador = Integer.parseInt(JOptionPane.showInputDialog(this, "Qual a idade do jogador"));
        String posicaoJogador = JOptionPane.showInputDialog(this, "Qual a posicao do jogador");

        Player newPlayer = new Player(nomeJogador, idadeJogador, posicaoJogador);

        titularesModel.addRow(new Object[]{newPlayer.getNome(), newPlayer.getIdade(), newPlayer.getPosicao()});
    }

    public static void main(String[] args) {
        new SquadManager();
    }
}
