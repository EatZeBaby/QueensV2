import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
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
public class Damier extends Thread{
    private int[][] plateau;
    private int tailleDamier;
    private static int nbReines;
    private ServerSocket s;
    private String delims = "[;]";

    public Damier(String loc,int taille){
        tailleDamier=taille;
        nbReines=0;
        initPlateau();
    }

    public int quiEstLa(int ligne, int colonne){
        return plateau[ligne][colonne];
    }

    public Damier(int taille){

        tailleDamier=taille;
        nbReines=0;
        initPlateau();
        try {
            s = new ServerSocket(6000);
        } catch (IOException e) {}
    }

    private void initPlateau(){

        plateau=new int[tailleDamier][tailleDamier];
        for (int i=0;i<tailleDamier;i++){
            for (int j=0; j< tailleDamier;j++){
                plateau[i][j]=-1;

            }
        }


    }
    private String getTailleString(){
        return Integer.toString(tailleDamier);
    }

    private String getNumeroReine(){
        return Integer.toString(nbReines++);
    }

    private boolean estLibre(int col,int ligne){
        boolean rep=false;

        if(plateau[col][ligne]==-1)
            rep=true;

        return rep;
    }

    public void placerReine(int numero,int colonneOld, int ligneOld,int colonneNew, int ligneNew){
        if ((colonneOld!=-1)&&(ligneOld!=-1)){
            plateau[colonneOld][ligneOld]=-1;
        }
        plateau[colonneNew][ligneNew]=numero;



    }

    private void chercherReineEtEffacer(int numero){
        for(int i=0;i<tailleDamier;i++){
            for(int j=0;j<tailleDamier;j++){
                if(plateau[i][j]==numero){
                    plateau[i][j]=-1;
                }
            }
        }
    }
    public void placerReine(int numero,int colonneNew, int ligneNew){
        chercherReineEtEffacer(numero);
        plateau[colonneNew][ligneNew]=numero;

    }

    private boolean verifFin(){
        int conflits_totaux=0;
        boolean fin=false;
        for(int i=0;i<tailleDamier;i++){
            for (int j=0; j<tailleDamier;j++){
                if (plateau[i][j]!=-1){
                    conflits_totaux=conflits_totaux+getConflits(i,j);

                }

            }
        }
        if (conflits_totaux==0){
            fin=true;
        }
        return fin;
    }

    public int getConflits(int colonne,int ligne){
        int res=0;

        //conflits sur la ligne
        for(int i=0;i<tailleDamier;i++){
            //si il y a une reine sur la ligne position[1]
            int test=plateau[i][ligne];
            if (test!=-1){
                if (test!=plateau[colonne][ligne]){
                    res++;
                }
            }
        }
        //conflits sur la colonne
        for(int i=0;i<tailleDamier;i++){
            //si il y a une reine sur la colonne position[0]
            int test=plateau[colonne][i];
            if (test!=-1){
                if (test!=plateau[colonne][ligne]){
                    res++;
                }
            }
        }





        //conflits sur les diagonales
        for(int j=1;j<tailleDamier;j++){
            //Haut Gauche
            if(((colonne-j)>=0)&&((ligne-j)>=0)){
                if (plateau[colonne-j][ligne-j]!=-1){
                    res++;
                }
            }

            //Haut Droit
            if(((colonne+j)<tailleDamier)&&((ligne-j)>=0)){
                if (plateau[colonne+j][ligne-j]!=-1){
                    res++;
                }
            }

            //Bas Gauche
            if(((colonne-j)>=0)&&((ligne+j)<tailleDamier)){
                if (plateau[colonne-j][ligne+j]!=-1){
                    res++;
                }
            }

            //Bas Droit
            if(((colonne+j)<tailleDamier)&&((ligne+j)<tailleDamier)){
                if (plateau[colonne+j][ligne+j]!=-1){
                    res++;
                }
            }

        }
        return res;
    }

    private String traitement(String requete){
        String reponse="";

        if (requete.equals("taille?")){
            reponse=getTailleString();

        }
        if (requete.equals("numero?"))
            reponse=getNumeroReine();
        String[] data;
        data=requete.split(delims);
        if (data[0].equals("libre")){
            if(estLibre( Integer.parseInt(data[1]),Integer.parseInt(data[2]))){
                reponse="OK";
            }else{
                reponse="NO";
            }
        }
        if (data[0].equals("bouge")){
            placerReine(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]),
                        Integer.parseInt(data[5]));
            afficher();
            Interface.refreshUI();

        }



        return reponse;
    }

    public void envoyerAuxReines(String message) throws IOException {
        // Which port should we send to
        int port = 5000;
        // Which address
        String group = "225.4.5.6";
        // Which ttl
        int ttl = 1;
        // Create the socket but we don't bind it as we are only going to send data
        MulticastSocket s = new MulticastSocket();
        // Note that we don't have to join the multicast group if we are only
        // sending data and not receiving
        // Fill the buffer with some data
        byte[] buf = new byte[1024];
        String req="Server;"+message+";";
        buf=req.getBytes();
        //for (int i=0; i<buf.length; i++) buf[i] = (byte)i;
        // Create a DatagramPacket
        DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
        // Do a send. Note that send takes a byte for the ttl and not an int.
        s.send(pack,(byte)ttl);
        // And when we have finished sending data close the socket
        // s.close();
    }

    public boolean presenceReine(int colonne,int ligne){
        boolean rep=false;
        if (plateau[colonne][ligne]!=-1){
            rep=true;

        }
        return rep;
    }
    public void run()  {



        while (true){
            Socket soc = null;
            try {
                soc = s.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // crée un nouveau thread pour le nouveau client
            ThreadDamier th = new ThreadDamier(soc);
            //lance l'execution du thread
            th.start();
        }
    }

    public void afficher(){

        for (int j=0;j<tailleDamier;j++){
            System.out.print(j + "\t");
            for (int i=0; i< tailleDamier;i++){
                if (plateau[i][j]!=-1){
                    //System.out.print("R ");
                    System.out.print(plateau[i][j] + " ");
                }else{
                    System.out.print("_ ");
                }

            }
            System.out.println();
        }
        System.out.println();


    }
    // Classe Interne écoutant sur un port les connexions des reines.
    public class ThreadDamier extends Thread{
        private String delims = "[;]";
        private Socket port;
        private BufferedReader entree;
        private PrintWriter sortie;

        public void run() {

            try{
                entree = new BufferedReader(new InputStreamReader( this.port.getInputStream()));
                sortie = new PrintWriter(this.port.getOutputStream(),true);

                String str=entree.readLine(); //Attente d'un message
                String reponseServeur=traitement(str);

                sortie.println(reponseServeur);

                entree.close();
                sortie.close();

            }catch(IOException e){}
            finally{
                try{
                    port.close();
                }catch(IOException e){}
            }
        }
        public ThreadDamier(Socket port){
            this.port=port;
        }
    }


}
