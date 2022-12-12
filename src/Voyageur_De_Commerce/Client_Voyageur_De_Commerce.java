package Voyageur_De_Commerce;

import Algo_Genetiques.Population;
import Util.Lecture;

import java.io.InputStream;
import java.util.Arrays;

public class Client_Voyageur_De_Commerce {


    /**
     * lit une liste de poids dans un fichier
     *
     * @param nomFichier nom du fichier texte contenant les coordonnées des villes
     * @param nbr_villes nombre de villes
     * @param coord_x    et coord_y : les 2 tableaux que la fonction remplit et qui vont contenir les coordonnées des villes
     */
    public static void charge_coords(String nomFichier, int nbr_villes, double[] coord_x, double[] coord_y) {
        assert (coord_x.length == coord_y.length) : "charge_coords : coord_x et coord_y n'ont pas la même taille ?";
        InputStream IS = Lecture.ouvrir(nomFichier);
        if (IS == null) {
            System.err.println("pb d'ouverture du fichier " + nomFichier);
        }
        int i = 0;
        while (!Lecture.finFichier(IS) && i < coord_x.length) {
            coord_x[i] = Lecture.lireDouble(IS);
            coord_y[i] = Lecture.lireDouble(IS);
            i++;
        }
        Lecture.fermer(IS);
    }

    public static void main(String[] args) throws InterruptedException {

        /* on initialise les coordonnées des villes en les lisant ds un fichier
         */

        int nbr_villes = 250;
        double[] coord_x = new double[nbr_villes];
        double[] coord_y = new double[nbr_villes];
        charge_coords("data_vdc/" + nbr_villes + "coords.txt", nbr_villes, coord_x, coord_y);


        int nbr_indiv = 100;
        double prob_mut = 0.01;

        Individu_VDC[] popu = Arrays.stream(new Individu_VDC[nbr_indiv]).map(i -> new Individu_VDC(coord_x, coord_y)).toArray(Individu_VDC[]::new);

        Population<Individu_VDC> pop = new Population<>(popu);

        Display_VDC disp = new Display_VDC(pop.individu_maximal());

        int nb_iter = 0;
        int nb_iter_max = 10;
        while (nb_iter < nb_iter_max) {
            pop.reproduction(prob_mut);
            System.out.println("génération " + nb_iter + " : adaptation moyenne = " + pop.adaptation_moyenne() + ", adaptation max = " + pop.adaptation_maximale());
            disp.refresh(pop.individu_maximal());
            nb_iter++;
        }

    }
}
