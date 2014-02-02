import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;

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
public class Reine extends Thread{
    private int numero;
    private Damier damier_local;
    private int[] position;
    private int taille_jeu;
    private EcouteUDP listener;

    private boolean en_marche;
    private int port;
    private String group;
    private MulticastSocket s;//*/
    private String delims = "[;]";

    private boolean est_libre(int colonne, int ligne){
        boolean rep=false;
        String reponseServeur="";
        try {

            reponseServeur=envoyerAuServeur("libre;"+colonne+";"+ligne+";");
        } catch (IOException e) {}
        if (reponseServeur.equals("OK")){


            rep=true;
        }
        return rep;
    }

    private void sePlacer(int colonne,int ligne){

        //System.out.println("Reine "+numero+" se place en " +colonne+" "+ligne);
        damier_local.placerReine(numero,position[0],position[1],colonne,ligne);
        try {
            envoyerAuServeur("bouge;" + numero + ";" + position[0] + ";" + position[1] + ";" + colonne + ";" + ligne + ";");
        } catch (IOException e) {}

        position[0]=colonne;
        position[1]=ligne;

    }

    private void initPosition(){
        int col;
        int ligne;

        while(true){
             col= (int) (Math.random() * (taille_jeu));

             ligne=(int)(Math.random() * (taille_jeu));
            if (est_libre(col,ligne))
                break;

        }

        sePlacer(col, ligne);



    }

    public Reine(int i,int taille ) throws IOException {

        System.out.println("I am The Queen n°"+i+" sur plateau de taille "+taille);
        taille_jeu=taille;
        numero=i;
        damier_local=new Damier("local",taille);
        position=new int[2];
        position[0]=-1;
        position[1]=-1;
        initPosition();
        port = 5000;
        group = "225.4.5.6";
        s = new MulticastSocket(port);
        s.joinGroup(InetAddress.getByName(group));//*/
        en_marche=true;
        listener= new EcouteUDP();
        listener.start();



    }

    private String envoyerAuServeur(String requete) throws IOException {

        Socket socket = new Socket("localhost", 6000);   //Contacte le Damier sur le port local 6000
        PrintWriter sortie = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sortie.println(requete);
        String rep=entree.readLine();//Attente de la réponse


        return rep;

    }

    private  void afficherPlateau(){
        damier_local.afficher();
    }

    private void broadcast_UDP_Multicast() throws IOException {
        // Which port should we send to

        // Which ttl
        int ttl = 1;
        // Create the socket but we don't bind it as we are only going to send data
        MulticastSocket s = new MulticastSocket();//*/
        // Note that we don't have to join the multicast group if we are only
        // sending data and not receiving
        // Fill the buffer with some data

        byte[] buf = new byte[1024];
        String req="Reine;"+numero+";"+position[0]+";"+position[1]+";";//
        buf=req.getBytes();

        //for (int i=0; i<buf.length; i++) buf[i] = (byte)i;
        // Create a DatagramPacket
        DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);

        // Do a send. Note that send takes a byte for the ttl and not an int.
        //System.out.println(numero+"envoie sa pos.");
        s.send(pack,(byte)ttl);
        //System.out.println("req:"+req);

        //ttl = s.getTimeToLive(); s.setTimeToLive(ttl); s.send(pack); s.setTimeToLive(ttl);

        // And when we have finished sending data close the socket
        //s.close();
    }

    private void chercherNouvellePosition() throws IOException {
        if(en_marche){
            //System.out.println("reine "+numero+" a "+getConflits()+" conflits.");
            int conflits_courants=getConflits();
            int colonne=position[0];
            int ligne=position[1];

            for(int i=0;i<taille_jeu;i++){
                for (int j=0; j<taille_jeu;j++){
                    if (!damier_local.presenceReine(i,j)){
                        int tmp=getConflits(i,j);
                        if(tmp<conflits_courants){
                            conflits_courants=tmp;
                            colonne=i;
                            ligne=j;
                        }
                    }

                }
            }
            sePlacer(colonne,ligne);

            broadcast_UDP_Multicast();
        }
        else{
            Thread.currentThread().interrupt();//preserve the message
            return;//Stop doing whatever I am doing and terminate
        }
    }


    public int getConflits(int colonne,int ligne){
        int res=0;

        //conflits sur la ligne
        for(int i=0;i<taille_jeu;i++){
            //si il y a une reine sur la ligne position[1]
            int test=damier_local.quiEstLa(i,ligne);
            if (test!=-1){
                if (test!=numero){
                    res++;
                }
            }
        }
        //conflits sur la colonne
        for(int i=0;i<taille_jeu;i++){
            //si il y a une reine sur la colonne position[0]
            int test=damier_local.quiEstLa(colonne,i);
            if (test!=-1){
                if (test!=numero){
                    res++;
                }
            }
        }





        //conflits sur les diagonales
        for(int j=1;j<taille_jeu;j++){
            //Haut Gauche
            if(((colonne-j)>=0)&&((ligne-j)>=0)){
                if (damier_local.presenceReine(colonne-j,ligne-j)){
                    res++;
                }
            }

            //Haut Droit
            if(((colonne+j)<taille_jeu)&&((ligne-j)>=0)){
                if (damier_local.presenceReine(colonne+j,ligne-j)){
                    res++;
                }
            }

            //Bas Gauche
            if(((colonne-j)>=0)&&((ligne+j)<taille_jeu)){
                if (damier_local.presenceReine(colonne-j,ligne+j)){
                    res++;
                }
            }

            //Bas Droit
            if(((colonne+j)<taille_jeu)&&((ligne+j)<taille_jeu)){
                if (damier_local.presenceReine(colonne+j,ligne+j)){
                    res++;
                }
            }

        }
        return res;
    }
    public int getConflits(){
        int res=0;

        //conflits sur la ligne
        for(int i=0;i<taille_jeu;i++){
            //si il y a une reine sur la ligne position[1]
            int test=damier_local.quiEstLa(i,position[1]);
            if (test!=-1){
                if (test!=numero){
                    res++;
                }
            }
        }
        //conflits sur la colonne
        for(int i=0;i<taille_jeu;i++){
            //si il y a une reine sur la ligne position[1]
            int test=damier_local.quiEstLa(position[0],i);
            if (test!=-1){
                if (test!=numero){
                    res++;
                }
            }
        }





        //conflits sur les diagonales
        for(int j=1;j<taille_jeu;j++){
            //Haut Gauche
            if(((position[0]-j)>=0)&&((position[1]-j)>=0)){
                if (damier_local.presenceReine(position[0]-j,position[1]-j)){
                    res++;
                }
            }

            //Haut Droit
            if(((position[0]+j)<taille_jeu)&&((position[1]-j)>=0)){
                if (damier_local.presenceReine(position[0]+j,position[1]-j)){
                    res++;
                }
            }

            //Bas Gauche
            if(((position[0]-j)>0)&&((position[1]+j)<taille_jeu)){
                if (damier_local.presenceReine(position[0]-j,position[1]+j)){
                    res++;
                }
            }

            //Bas Droit
            if(((position[0]+j)<taille_jeu)&&((position[1]+j)<taille_jeu)){
                if (damier_local.presenceReine(position[0]+j,position[1]+j)){
                    res++;
                }
            }

        }
        return res;
    }


    public void run(){

        damier_local.afficher();
        System.out.println("Reine "+numero+" prete");

        if(numero==0){
            try {
                chercherNouvellePosition();
            } catch (IOException e) {}
        }




    }

    public static void main(String[] args) throws IOException {
        int num=demanderNumero();
        int taille=demanderTaille();

        if(num<taille){
            Reine reine=new Reine(num,taille);

        }else{
            System.out.println("Plateau Plein.");

        }
    }

    private static int demanderNumero() throws IOException {
        Socket socket = new Socket("localhost", 6000);   //Contacte le Damier sur le port local 6000
        PrintWriter sortie = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sortie.println("numero?");
        String rep=entree.readLine();//Attente de la réponse
        int num = Integer.parseInt(rep);
        return num;
    }

    private static int demanderTaille() throws IOException {
        Socket socket = new Socket("localhost", 6000);   //Contacte le Damier sur le port local 6000
        PrintWriter sortie = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sortie.println("taille?");
        String rep=entree.readLine();//Attente de la réponse
        int taille = Integer.parseInt(rep);
        return taille;
    }

    private void traitement(String requete) throws IOException {



        String[] data;
        data=requete.split(delims);
        System.out.println("data0 "+data[0]+","+data[1]);
        if (data[0].equals("Server")){
            if (data[1].equals("DEPART")){
               this.start();
            }
            if (data[1].equals("STOP")){
                en_marche=false;



            }

        }

        if (data[0].equals("Reine")){
            //System.out.println("data[1]:"+data[1]+"\t(numero%taille_jeu)-1) : "+(((numero-1)%taille_jeu+taille_jeu)%taille_jeu));


            damier_local.placerReine(Integer.parseInt(data[1]),Integer.parseInt(data[2]),Integer.parseInt(data[3]));

            if(Integer.parseInt(data[1])==(((numero-1)%taille_jeu+taille_jeu)%taille_jeu)){
            //f(Integer.parseInt(data[1])==(((numero-1)%taille_jeu))){
                chercherNouvellePosition();
            }

        }



    }

    public class EcouteUDP extends Thread {
        private boolean stopThread = false;


        private void ecouteMultiCast() throws IOException {
            // Import some needed classes
            // Now the socket is set up and we are ready to receive packets
            // Create a DatagramPacket and do a receive

            byte[] buf = new byte[1024];


            DatagramPacket pack = new DatagramPacket(buf, buf.length);

            s.receive(pack);
            // Finally, let us do something useful with the data we just received,
            // like print it on stdout :-)
            // System.out.println("Received data from: " + pack.getAddress().toString() +  ":" + pack.getPort() + " with length: " + pack.getLength());
            // System.out.write(pack.getData(),0,pack.getLength());
            String req;

            req=new String(pack.getData());

            //System.out.println(identifiant()+" a reçu "+req);
            traitement(req);
            // And when we have finished receiving data leave the multicast group and
            // close the socket
            // s.leaveGroup(InetAddress.getByName(group));
            //s.close();
        }
        @Override
        public void run() {

            boolean fin = false;

            while( !fin ) {

                try{
                    while(true)
                        // ecoute();
                        ecouteMultiCast();
                }catch (IOException e){}

                synchronized(this) {
                    Thread.yield();

                    // lecture du boolean
                    fin = this.stopThread;
                }

            }
        }
        public synchronized void quit() {
            this.stopThread = true;
        }
    }



}
