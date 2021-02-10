package diversosCodes;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Aplicação simples para desenhar uma curva de Bezier .
 *
 * @author google,isa
 */
public class Tela extends JPanel {

    private JFrame frame;
    private JPanel painel;
    private JLabel campo;
    private JButton JBGerarCurva;
    private JButton JBNovo;
    private JSpinner spinner;
    private SpinnerModel modelo;
    private ArrayList<Point> pontos;
//    private ClickListener mouseClick;
    private Graphics2D g2;

    /**
     * Construtor da classe Criação da interface gráfica
     */
    public Tela() {

        pontos = new ArrayList<>();
        frame = new JFrame("Aplicativo - Gerador de Curvas Bezier | ");
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPainel();
        inicializarComponentes();
        setIcon();

    }

    /**
     * Método que inicia os componentes de execução
     */
    public void inicializarComponentes() {

        painel.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                gerarPonto(e, painel.getGraphics());
                pontos.add(new Point(e.getX(), e.getY()));
                gerarLinha(painel.getGraphics());                
                if (pontos.size() > 2) {
                    gerarCurva(painel.getGraphics(), (int) modelo.getValue());
                    

                }

            }


        }
        );

        modelo = new SpinnerNumberModel(200, 1, 1000, 10);
        spinner = new JSpinner(modelo);

        JBNovo.addActionListener(
                new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                painel.repaint();
                pontos.clear();

            }

        }
        );

        JBGerarCurva.addActionListener(
                new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (pontos.size() > 2) {
                    gerarCurva(painel.getGraphics(), (int) modelo.getValue());
                    pontos.clear();
                }

            }

        }
        );

    }

    /**
     * Executa a aplicação
     */
    public static void main(String[] args) {

        new Tela();

    }

    public void paintComponent(Graphics g) {
        super.paintComponents(g);

    }

    /*
    * Método que cria as linhas que ligam os pontos
     */
    public void gerarPonto(MouseEvent e, Graphics g) {
        g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.fillOval(e.getX() - 5, e.getY() - 5, 14, 14);
        
    }

    /*
    * Método que cria as linhas que ligam os pontos
     */
    public void gerarLinha(Graphics g) {
        g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLUE);
        if (pontos.size() > 1) {
            int n = pontos.size();
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine((int) pontos.get(i).getX(), (int) pontos.get(i).getY(), (int) pontos.get(i + 1).getX(), (int) pontos.get(i + 1).getY());

            }
        }
        
    }

    /**
     * Método que cria a curva bezier
     *
     * @param g - objeto gráfico para desenhar na tela
     * @param pontos - os Pontos de controle capturados para desenhar a curva
     * @param numeroPuntos - O número de Pontos que formam a curva ( quanto mais
     * a curva tem maior qualidade, mas requer mais processamento )
     */
    public void gerarCurva(Graphics g, int numeroPuntos) {

        g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.RED);
        //Variáveis ​​para armazenar os pontos calculados
        double pontoX = 0, pontoY = 0;
        int cont = 0;

        // Esta variasveis armazenam os pontos
        ArrayList<int[]> anteriores = new ArrayList<>();
        double avance = 1 / ((double) numeroPuntos);
        int tamLista = pontos.size();

        //Este ciclo realiza o número de interações que o usuário deseja fazer
        for (double u = 0; u <= 1; u += avance) {
            //Este ciclo repete o Pontos que vai desenhar e calcula o próximo ponto na curva
            for (int k = 0; k < tamLista; k++) {
                double b = calcularB(u, tamLista - 1, k);
                pontoX += pontos.get(k).getX() * b;
                pontoY += pontos.get(k).getY() * b;
            }

            //Eles são armazenados e desenhados encima dos pontos calculados
            anteriores.add(new int[]{(int) pontoX, (int) pontoY});
            g2.drawLine((int) pontoX, (int) pontoY, (int) pontoX, (int) pontoY);

            // A linha a partir do ponto anterior é desenhado para o recém calculada
            // Dessa forma, não calculamos ponto por ponto de toda a curva
            if (anteriores.size() > 1) {
                g2.drawLine(anteriores.get(anteriores.size() - 2)[cont], anteriores.get(anteriores.size() - 2)[cont + 1], (int) pontoX, (int) pontoY);
                // Nós removemos o primeiro item que não é mais usado
                anteriores.remove(cont);

            }

            //Nós apagamos os valores antigos para a próxima iteração
            pontoX = pontoY = 0;
        }

        //A última perna da curva é desenhada
        g2.drawLine((int) anteriores.get(anteriores.size() - 1)[cont], (int) anteriores.get(anteriores.size() - 1)[cont + 1],
                (int) pontos.get(tamLista - 1).getX(), (int) pontos.get(tamLista - 1).getY());
        updateUI();
    }

    /**
     * Método de cálculo B é a função de ordem k Combinação N + 1 Pontos
     * controlar .
     *
     * @param u - número de iteração atual
     * @ Param N - número de Pontos de controle
     * @ Param K - o número de ponto atual
     *
     * @return b
     */
    private double calcularB(double u, int n, int k) {

        return (factorial(n) / (factorial(k) * factorial(n - k)))
                * Math.pow(u, k) * Math.pow(1 - u, n - k);

    }

    /**
     * Método de cálculo do fatorial de um número .
     *
     * @ Param N - número que recebe o valor do fatorial
     *
     * @return Fatorial de n
     */
    private double factorial(int n) {

        double factorial = 1;

        if (n == 0 || n == 1) {
            return factorial;
        } else {
            for (int i = 2; i <= n; i++) {
                factorial *= i;
            }

            return factorial;
        }

    }

    /**
     * Método que adiciona o logo do aplicativo na GUI .
     */
    public void setIcon() {
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../icon/line.png")));
    }

    /**
     * Método que adiciona o componentes de execução do aplicativo passando seus
     * devidos tamanhos e posições na GUI(Botões)
     */
    public void setPainel() {
        painel = new JPanel();
        painel.setBackground(Color.LIGHT_GRAY);
        painel.setBounds(0, 0, 150, 600);
        painel.setLayout(new BorderLayout());
        JBNovo = new JButton("LIMPAR");
        JBNovo.setBounds(250, 490, 150, 50);
        painel.add(JBNovo);
        JBGerarCurva = new JButton("GERAR CURVA");
        JBGerarCurva.setBounds(400, 490, 150, 50);
        painel.add(JBGerarCurva);
        frame.add(painel);
        campo = new JLabel();
        campo.setBounds(0, 450, 150, 150);
        painel.add(campo);

    }

}