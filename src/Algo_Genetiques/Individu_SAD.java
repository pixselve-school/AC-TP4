package Algo_Genetiques;

public class Individu_SAD implements Individu {
    boolean[] genes;
    double[] poids;
    double poidsMax;

    public Individu_SAD(double[] poids, double poidsMax) {
        this.poids = poids;
        this.poidsMax = poidsMax;
        genes = new boolean[this.poids.length];
        for (int i = 0; i < genes.length; i++) {
            genes[i] = Math.random() < 0.5;
        }
    }

    /**
     * renvoie l'adaptation de cet individu
     */
    public double adaptation() {
        double poidsTotal = 0;
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                poidsTotal += poids[i];
            }
        }
        if (poidsTotal > poidsMax) {
            return 0;
        } else {
            return poidsTotal;
        }
    }

    /**
     * renvoie un tableau de 2 individus constituant les
     * enfants de la reproduction entre this et conjoint
     * Pour le croisement des deux individus, on tire au hasard une position et les sous-chaînes
     * s’inversent.
     *
     * @param conjoint à accoupler avec l'objet courant
     * @return tableau des 2 enfants
     */
    public Individu[] croisement(Individu conjoint) {
        Individu_SAD[] enfants = new Individu_SAD[2];
        enfants[0] = new Individu_SAD(poids, poidsMax);
        enfants[1] = new Individu_SAD(poids, poidsMax);
        int pointCroisement = (int) (Math.random() * genes.length);
        for (int i = 0; i < pointCroisement; i++) {
            enfants[0].genes[i] = genes[i];
            enfants[1].genes[i] = ((Individu_SAD) conjoint).genes[i];
        }
        for (int i = pointCroisement; i < genes.length; i++) {
            enfants[0].genes[i] = ((Individu_SAD) conjoint).genes[i];
            enfants[1].genes[i] = genes[i];
        }
        return enfants;
    }

    /**
     * applique l'opérateur de mutation
     * associé à la probabilité prob
     *
     * @param prob
     */
    public void mutation(double prob) {
        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < prob) {
                genes[i] = !genes[i];
            }
        }

    }
}
