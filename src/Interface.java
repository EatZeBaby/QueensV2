import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
Created by Axel on 18/01/2014. :)
╔══════════════╦════════════════════════════════════════════════════════════╗
║  ( (         ║						2013-2014							║
║    ) )	   ║				Université Dauphine Paris 9					║
║  ........	   ║					Master 1 - MIAGE						║
║  |      |]   ║			       Projet Java Avancé			            ║
║  \      /    ╟────────────────────────────────────────────────────────────╢
║   `----'     ║	Axel Richier - Thibault Schleret - Guillaume Fronczak   ║
╚══════════════╩════════════════════════════════════════════════════════════╝

*/

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class Interface extends JFrame implements ActionListener {
    private static int taille_jeu;

    // Message box
    JOptionPane JOPQuitter;


    // -- MENU -- //

    private JMenuBar menuBar = new JMenuBar();
    private JMenu Fichier = new JMenu("Fichier");

    // Items de Fichier
    private JMenuItem itemQuitter = new JMenuItem ("Quitter");


    private static JPanel container = new JPanel();

    // -- LES BOUTONS -- //

    // Classe Bouton dérivée de JButton
    public class Bouton extends JButton implements MouseListener
    {
        private String name;

        public Bouton(String str)
        {
            super(str);
            this.name = str;
            this.addMouseListener(this);
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            // Change la couleur du texte du bouton au survol
            Bouton source = (Bouton)e.getSource();
            source.setForeground(Color.blue);
        }

        public void mouseExited(MouseEvent e) {
            // Change la couleur du texte du bouton à la sortie de survol
            Bouton source = (Bouton)e.getSource();
            source.setForeground(Color.BLACK);
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent event) {
        }
    }


    // Création des boutons
    private Bouton refresh = new Bouton("Refresh");
    private Bouton boutonStart = new Bouton("Start");
    private Bouton boutonReset = new Bouton("Reset");
    private Bouton boutonStop = new Bouton("Stop");
    private Bouton boutonPlayBack = new Bouton("Play Back");
    private Bouton boutonEnd = new Bouton("End");
    private Bouton boutonQuitter = new Bouton("Quitter");
    private Bouton boutonCreerGrille = new Bouton("Créer Grille");



    // -- LES ACTIONS -- //

    public void actionPerformed(ActionEvent arg0)
    {
        // Bouton Quitter
        if (arg0.getSource() == boutonQuitter)
        {
            int option = JOPQuitter.showConfirmDialog(null, "Voulez-vous quitter l'application ?","Quitter",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                System.exit(0);
            }
        }
        if (arg0.getSource() == boutonStart)
        {

            damier_principal.afficher();
            try {

                damier_principal.envoyerAuxReines("DEPART;");
            } catch (IOException e) {
                e.printStackTrace();
            }


            container.updateUI();

        }
        if (arg0.getSource() == refresh)
        {
            damier_principal.afficher();
            container.removeAll();
            afficher_plateauUI(getTaille());
            container.updateUI();
            //TheQueens.afficherEnsembleConflits();

        }


    }


    // -- CONSTRUCTEUR -- //

    public Interface(int taille) throws IOException, InterruptedException {

        // --- FENETRE PRINCIPALE --- //

        // -- JFRAME -- //

        // Titre de la fenêtre
        taille_jeu=taille;

        JFrame MaFenetre = new JFrame();
        MaFenetre.setTitle("La Guerre des Reines");
        MaFenetre.setName("Les Reines");
        // Taille de la fenêtre
        MaFenetre.setSize(650,650);
        // Positionnement de la fenêtre (au centre)
        MaFenetre.setLocationRelativeTo(null);
        // Terminer le processus lorsque l'on quitte l'application
        MaFenetre.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        // --- ECHIQUIER --- //

        // -- JPANEL -- //

        // Définition du container et des Layout
        afficher_plateauUI(taille);

        // -- BOUTONS  ET COMBOBOX -- //

        // On rempli la combo box
        /*cbxChoixEchiquier.addItem(4);
        cbxChoixEchiquier.addItem(6);
        cbxChoixEchiquier.addItem(8);//*/

        // On ajoute la combo box et les boutons
        JPanel south = new JPanel();

        //south.add(cbxChoixEchiquier);
        south.add(refresh);
        south.add(boutonStart);
        south.add(boutonPlayBack);
        south.add(boutonReset);
        south.add(boutonStop);
        south.add(boutonEnd);
        south.add(boutonQuitter);

        // On ajoute tout ça à la fenêtre principale
        MaFenetre.add(south, BorderLayout.SOUTH);
        MaFenetre.add(container, BorderLayout.CENTER);


        // Interractions des boutons
        //boutonCreerGrille.addActionListener(this);
        refresh.addActionListener(this);
        boutonStart.addActionListener(this);
        boutonPlayBack.addActionListener(this);
        boutonReset.addActionListener(this);
        boutonStop.addActionListener(this);
        boutonEnd.addActionListener(this);
        boutonQuitter.addActionListener(this);


        // -- JOPanel -- //

        JOPQuitter = new JOptionPane();


        // -- MENU -- //

        // On initialise le contenu du menu
        menuBar.setBackground(Color.GRAY);
        Fichier.setBackground(Color.GRAY);
        itemQuitter.addActionListener(new ActionListener(){ public void actionPerformed (ActionEvent arg0) {System.exit(0);}});
        itemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        this.Fichier.add(itemQuitter);
        this.menuBar.add(Fichier);
        this.setJMenuBar(menuBar);

        // Rendre la fenêtre visible
        MaFenetre.setVisible(true);

    }
    public Interface() throws IOException {

        // --- FENETRE PRINCIPALE --- //

        // -- JFRAME -- //

        // Titre de la fenêtre

        JFrame MaFenetre = new JFrame();
        MaFenetre.setTitle("La guerre des Reines");
        MaFenetre.setName("Les Reines");
        // Taille de la fenêtre
        MaFenetre.setSize(600,600);
        // Positionnement de la fenêtre (au centre)
        MaFenetre.setLocationRelativeTo(null);
        // Terminer le processus lorsque l'on quitte l'application
        MaFenetre.setDefaultCloseOperation(this.EXIT_ON_CLOSE);



        // --- ECHIQUIER --- //

        // -- JPANEL -- //

        // Définition du container et des Layout





        // -- BOUTONS  ET COMBOBOX -- //

        // On rempli la combo box
        /*cbxChoixEchiquier.addItem(4);
        cbxChoixEchiquier.addItem(6);
        cbxChoixEchiquier.addItem(8);//*/

        // On ajoute la combo box et les boutons
        JPanel south = new JPanel();

        //south.add(cbxChoixEchiquier);
        south.add(refresh);
        south.add(boutonStart);
        south.add(boutonPlayBack);
        south.add(boutonReset);
        south.add(boutonStop);
        south.add(boutonEnd);
        south.add(boutonQuitter);

        // On ajoute tout ça à la fenêtre principale
        MaFenetre.add(south, BorderLayout.SOUTH);
        MaFenetre.add(container, BorderLayout.CENTER);


        // Interractions des boutons
        //boutonCreerGrille.addActionListener(this);
        refresh.addActionListener(this);
        boutonStart.addActionListener(this);
        boutonPlayBack.addActionListener(this);
        boutonReset.addActionListener(this);
        boutonStop.addActionListener(this);
        boutonEnd.addActionListener(this);
        boutonQuitter.addActionListener(this);


        // -- JOPanel -- //

        JOPQuitter = new JOptionPane();


        // -- MENU -- //

        // On initialise le contenu du menu
        menuBar.setBackground(Color.GRAY);
        Fichier.setBackground(Color.GRAY);
        itemQuitter.addActionListener(new ActionListener(){ public void actionPerformed (ActionEvent arg0) {System.exit(0);}});
        itemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        this.Fichier.add(itemQuitter);
        this.menuBar.add(Fichier);
        this.setJMenuBar(menuBar);

        // Rendre la fenêtre visible
        MaFenetre.setVisible(true);

    }

    private static void afficher_plateauUI(int taille) {
        container.setLayout(new GridLayout(taille,taille));
        JLabel tab[][]=new JLabel[taille][taille];
        int a=1;
        // On construit l'échiquier, on rempli chaque case du GridLayout par un JLabel noir ou blanc
        for(int ligne=0;ligne<taille;ligne++)
        {
            a = a==1 ? 0 : 1;
            for(int colonne=0;colonne<taille;colonne++)
            {
                tab[colonne][ligne]= new JLabel();

                if(damier_principal.presenceReine(colonne, ligne)){

                    tab[colonne][ligne].setIcon(new ImageIcon("queen.png"));
                }else{
                    //System.out.println("false");
                }
                if((colonne+1)%2==a) tab[colonne][ligne].setBackground(Color.white);
                else tab[colonne][ligne].setBackground(Color.black);
                tab[colonne][ligne].setOpaque(true);
                tab[colonne][ligne].setPreferredSize(new Dimension(80,80));
                container.add(tab[colonne][ligne]);
            }
        }
    }

    private static int getTaille(){
        return taille_jeu;
    }
    public static void refreshUI(){

        container.removeAll();

        afficher_plateauUI(getTaille());
        container.updateUI();

    }

    private static Damier damier_principal;




    public static void main(String[] args) throws IOException, InterruptedException {
        int taille=Integer.parseInt(args[0]);
        taille_jeu=taille;
        damier_principal=new Damier(taille);

        Interface interface_jeu= new Interface(taille);

        damier_principal.start();
    }
}

