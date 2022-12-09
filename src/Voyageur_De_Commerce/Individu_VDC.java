package Voyageur_De_Commerce;

import Algo_Genetiques.Individu;

import java.util.Random;

public class Individu_VDC implements Individu {

    private double[] coord_x;
    private double[] coord_y;

    private int[] chemin;

    //Constructeur
    public Individu_VDC(double[] coord_x, double[] coord_y) {
        this.coord_x = coord_x;
        this.coord_y = coord_y;
        this.chemin = new int[coord_x.length];
        for (int i = 0; i < coord_x.length; i++) {
            this.chemin[i] = i;
        }

        //On mélange le chemin
        shuffle();

    }

    private void shuffle() {
        for (int i = 0; i < this.coord_x.length; i++) {
            int j = (int) (Math.random() * this.coord_x.length);
            int tmp = this.chemin[i];
            this.chemin[i] = this.chemin[j];
            this.chemin[j] = tmp;
        }
    }

    /* Classes de l'interface Individu
     */

    /**
     * renvoie l'adaptation de cet individu
     * La distance doit être minimisée
     * @return distance
     */
    @Override
    public double adaptation() {
        double distance = 0;
        for (int i = 0; i < this.coord_x.length - 1; i++) {
            distance += Math.sqrt(Math.pow(this.coord_x[this.chemin[i]] - this.coord_x[this.chemin[i + 1]], 2) + Math.pow(this.coord_y[this.chemin[i]] - this.coord_y[this.chemin[i + 1]], 2));
        }
        return 1 / distance;

    }

    @Override
    public Individu[] croisement(Individu conjoint) {
        // Une possibilité : croisement "prudent"
        // A COMPLETER ET À ADAPTER A VOS CHOIX DE REPRESENTATION

        boolean[] b1 = new boolean[chemin.length];
        boolean[] b2 = new boolean[chemin.length];
        for (int i = 0; i < chemin.length; i++) {
            b1[i] = false;
            b2[i] = false;
        }
        Random r = new Random();
        int ind = r.nextInt(chemin.length);

        // // on regarde les villes qu'on rencontre dans la premiere partie
        Individu_VDC[] enfants = {
                new Individu_VDC(coord_x, coord_y),
                new Individu_VDC(coord_x, coord_y)
        };
        for (int i = 0; i < ind; i++) {
            enfants[0].chemin[i] = this.chemin[i];
            b1[this.chemin[i]] = true;

            enfants[1].chemin[i] = ((Individu_VDC) conjoint).chemin[i];
            b2[((Individu_VDC)conjoint).chemin[i]] = true;
        }

        // //deuxieme partie : si la ville n'a pas été visitée dans la premiere partie, on prend

        int j = 0;
        for (int i = ind; i < chemin.length; i++) {
            while (b1[((Individu_VDC)conjoint).chemin[j]]) {
                j++;
            }
            enfants[0].chemin[i] = ((Individu_VDC)conjoint).chemin[j];
            b1[((Individu_VDC)conjoint).chemin[j]] = true;
            j++;
        }

        // //fin : on complète avec les villes non rencontrées

        j = 0;
        for (int i = ind; i < chemin.length; i++) {
            while (b2[this.chemin[j]]) {
                j++;
            }
            enfants[1].chemin[i] = this.chemin[j];
            b2[this.chemin[j]] = true;
            j++;
        }

        return enfants;
    }

    @Override
    public void mutation(double prob) {
        for (int i = 0; i < this.coord_x.length; i++) {
            if (Math.random() < prob) {
                int ind = (int) (Math.random() * this.coord_x.length);
                int tmp = this.chemin[i];
                this.chemin[i] = this.chemin[ind];
                this.chemin[ind] = tmp;
            }
        }
    }


    /* Accesseurs (pour Display_VDC)
     */
    public int[] get_parcours() {
        return chemin;
    }

    public double[] get_coord_x() {
        return coord_x;
    }

    public double[] get_coord_y() {
        return coord_y;
    }
}
