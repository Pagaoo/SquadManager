public class Player {
    private int matricula;
    private String nome;
    private int idade;
    private String posicao;
    private int numeroCamisa;
    private String time;

    public Player(String nome, int idade, String posicao) {
        this.matricula = 0;
        this.nome = nome;
        this.idade = idade;
        this.posicao = posicao;
        this.numeroCamisa = 0;
        time = null;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getNumeroCamisa() {
        return numeroCamisa;
    }

    public void setNumeroCamisa(int numeroCamisa) {
        this.numeroCamisa = numeroCamisa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
