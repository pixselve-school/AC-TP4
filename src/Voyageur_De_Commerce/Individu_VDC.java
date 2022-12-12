package Voyageur_De_Commerce;

import Algo_Genetiques.Individu;

import java.util.*;

public class Individu_VDC implements Individu {

    /**
     * tableau contenant l'ordre des villes
     */
    private final double[] coord_x;
    /**
     * coordonnées y des villes
     */
    private final double[] coord_y;

    /**
     * Chemin de l'individu
     * chemin[i] = j, signifie que la ville j est visitée en i-ème position
     */
    private int[] chemin;

    //Constructeur
    public Individu_VDC(double[] coord_x, double[] coord_y) {
        this.coord_x = coord_x;
        this.coord_y = coord_y;

        //on crée un tableau de taille égale au nombre de villes pour stocker le chemin de l'individu
        int nbCities = coord_x.length;
        this.chemin = new int[nbCities];

        //on remplit le tableau chemin avec l'ordre des villes dans lequel elles doivent être visitées
        //ici on choisit aléatoirement l'ordre des villes
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < nbCities; i++) {
            cities.add(i);
        }
        Collections.shuffle(cities);
        for (int i = 0; i < nbCities; i++) {
            chemin[i] = cities.get(i);
        }
    }

    /**
     * renvoie l'adaptation de cet individu
     * La distance doit être minimisée
     *
     * @return distance
     */
    @Override
    public double adaptation() {
        //on calcule la distance totale parcourue en suivant l'ordre des villes dans le tableau chemin
        double total = 0;
        for (int i = 0; i < chemin.length - 1; i++) {
            //on ajoute cette distance à la distance totale
            total += distance(chemin[i], chemin[i + 1]);
        }

        //on retourne la distance totale
        return 1.0 / total;
    }

    @Override
    public Individu[] croisement(Individu conjoint) {
        Individu_VDC conjointVDC = (Individu_VDC) conjoint;

        //on crée deux enfants
        Individu_VDC enfant1 = new Individu_VDC(coord_x, coord_y);
        Individu_VDC enfant2 = new Individu_VDC(coord_x, coord_y);


        boolean[] visiteParEnfant1 = new boolean[chemin.length]; // l'indice i est à true si la ville i est visitée par enfant1
        boolean[] visiteParEnfant2 = new boolean[chemin.length]; // l'indice i est à true si la ville i est visitée par enfant2

        // tous les chemins sont à -1 au début
        Arrays.fill(enfant1.chemin, -1);
        Arrays.fill(enfant2.chemin, -1);

        Random random = new Random();
        int pointCroisement = random.nextInt(chemin.length);
        // on ajoute les premières villes jusqu'au point de croisement
        for (int i = 0; i < pointCroisement; i++) {
            enfant1.chemin[i] = chemin[i];
            enfant2.chemin[i] = conjointVDC.chemin[i];
            visiteParEnfant1[chemin[i]] = true;
            visiteParEnfant2[conjointVDC.chemin[i]] = true;
        }
        // on ajoute les villes restantes (en évitant les doublons)
        for (int i = pointCroisement; i < chemin.length; i++) {
            if (!visiteParEnfant1[conjointVDC.chemin[i]]) {
                enfant1.chemin[i] = conjointVDC.chemin[i];
                visiteParEnfant1[conjointVDC.chemin[i]] = true;
            }
            if (!visiteParEnfant2[chemin[i]]) {
                enfant2.chemin[i] = chemin[i];
                visiteParEnfant2[chemin[i]] = true;
            }
        }

        // on cherche les villes non visitées dans les enfants
        ArrayList<Integer> villesNonVisiteesEnfant1 = new ArrayList<>();
        ArrayList<Integer> villesNonVisiteesEnfant2 = new ArrayList<>();
        for (int i = 0; i < chemin.length; i++) {
            if (!visiteParEnfant1[conjointVDC.chemin[i]]) {
                villesNonVisiteesEnfant1.add(conjointVDC.chemin[i]);
            }
            if (!visiteParEnfant2[chemin[i]]) {
                villesNonVisiteesEnfant2.add(chemin[i]);
            }
        }


//        System.out.println("=================BEFORE===================");
//        System.out.println(villesNonVisiteesEnfant1);
//        System.out.println(Arrays.toString(visiteParEnfant1));
//        System.out.println(indicesNonVisiteesEnfant1);
//        System.out.println(Arrays.toString(enfant1.chemin));

        // on remplace les villes non visitées par les villes non visitées de l'autre enfant
        for (int i = 0; i < enfant1.chemin.length; i++) {
            if (enfant1.chemin[i] == -1) {
                enfant1.chemin[i] = villesNonVisiteesEnfant1.get(0);
                villesNonVisiteesEnfant1.remove(0);
            }
        }
        for (int i = 0; i < enfant2.chemin.length; i++) {
            if (enfant2.chemin[i] == -1) {
                enfant2.chemin[i] = villesNonVisiteesEnfant2.get(0);
                villesNonVisiteesEnfant2.remove(0);
            }
        }

//        System.out.println("=================AFTER===================");
//        System.out.println(villesNonVisiteesEnfant1);
//        System.out.println(Arrays.toString(visiteParEnfant1));
//        System.out.println(indicesNonVisiteesEnfant1);
//        System.out.println(Arrays.toString(enfant1.chemin));


        // si il reste des villes à -1, throw exception
        for (int i = 0; i < chemin.length; i++) {
            if (enfant1.chemin[i] == -1 || enfant2.chemin[i] == -1) {
                throw new RuntimeException("Erreur dans le croisement");
            }
        }

        enfant1.optim_2opt();
        enfant2.optim_2opt();
        return new Individu[]{enfant1, enfant2};


    }


    @Override
    public void mutation(double prob) {
        Random r = new Random();

        //on parcourt le chemin
        for (int i = 0; i < chemin.length; i++) {
            //on tire un nombre aléatoire entre 0 et 1
            double rand = r.nextDouble();
            //si ce nombre est inférieur à la probabilité de mutation
            if (rand < prob) {
                //on tire un indice aléatoire entre 0 et le nombre de villes
                int indice = r.nextInt(chemin.length);
                //on échange la ville courante avec la ville d'indice tiré aléatoirement
                int temp = chemin[i];
                chemin[i] = chemin[indice];
                chemin[indice] = temp;
            }
        }

    }

    public void optim_2opt() {
        for (int i = 0; i < chemin.length; i++) {
            for (int j = i + 1; j < chemin.length; j++) {
                if (gain(i, j) < 0) {
                    for (int k = 0; k < (j - i + 1) / 2; k++) {
                        int tmp = chemin[i + k];
                        chemin[i + k] = chemin[j - k];
                        chemin[j - k] = tmp;

                    }
                }
            }
        }
    }

    private double gain(int i, int j) {
        int nb_villes = chemin.length;
        double gain = distance(chemin[i], chemin[(j + 1) % nb_villes])
                + distance(chemin[(i + nb_villes - 1) % nb_villes], chemin[j])
                - distance(chemin[(i + nb_villes - 1) % nb_villes], chemin[i])
                - distance(chemin[j], chemin[(j + 1) % nb_villes]);
        return gain;
    }

    private double distance(int i, int i1) {
        //on récupère les coordonnées x et y des villes en question
        double x1 = coord_x[i];
        double y1 = coord_y[i];
        double x2 = coord_x[i1];
        double y2 = coord_y[i1];

        //on calcule la distance euclidienne entre les deux villes
        double dx = x1 - x2;
        double dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy);

    }

    private void swap(int i1, int i2) {
        int tmp = chemin[i1];
        chemin[i1] = chemin[i2];
        chemin[i2] = tmp;
    }

    @Override
    public Individu clone() {
        Individu_VDC clone = new Individu_VDC(coord_x, coord_y);
        clone.chemin = Arrays.copyOf(chemin, chemin.length);
        return clone;
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
