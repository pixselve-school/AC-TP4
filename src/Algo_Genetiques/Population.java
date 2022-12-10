package Algo_Genetiques;

import java.util.*;

public class Population<Indiv extends Individu> {

    // Liste contenant les différents individus d'une génération
    private List<Indiv> population;


    /**
     * construit une population à partir d'un tableau d'individu
     */
    public Population(Indiv[] popu) {
        population = new ArrayList<>();
        population.addAll(Arrays.asList(popu));
    }

    /**
     * sélectionne un individu (sélection par roulette par exemple, cf TD)
     *
     * @param adapt_totale somme des adaptations de tous les individus (pour ne pas avoir à la recalculer)
     * @return indice de l'individu sélectionné
     */
    public int selection(double adapt_totale){
        //on utilise un générateur de nombres aléatoires pour choisir un nombre entre 0 et adapt_totale
        Random rand = new Random();
        double randValue = rand.nextDouble() * adapt_totale;

        //on parcourt tous les individus et on calcule leur adaptation relative
        //c'est-à-dire leur adaptation divisée par la somme des adaptations de tous les individus
        double adapt_relative = 0;
        for (int i = 0; i < population.size(); i++) {
            adapt_relative += population.get(i).adaptation() / adapt_totale;

            //si le nombre aléatoire est inférieur à l'adaptation relative de l'individu,
            //c'est que l'individu a été sélectionné
            if (randValue <= adapt_relative) {
                return i;
            }
        }

        //si aucun individu n'a été sélectionné, on retourne l'indice de l'individu ayant la plus grande adaptation
        return population.size() - 1;
    }

    /**
     * remplace la génération par la suivante
     * (croisement + mutation)
     *
     * @param prob_mut probabilité de mutation
     */
    @SuppressWarnings("unchecked")
    public void reproduction(double prob_mut) {

        /***** on construit la nouvelle génération ****/
        List<Indiv> new_generation = new ArrayList<Indiv>();

        // tant qu'on n'a pas le bon nombre
        double adaptTotale = adaptationSum();

        // Élitisme
        //on trie la liste des individus par ordre décroissant d'adaptation
//        population.sort((Indiv ind1, Indiv ind2) -> Double.compare(ind2.adaptation(), ind1.adaptation()));
////
////        //on ajoute les meilleurs individus de la génération courante à la nouvelle génération
//        int nbElites = (int) (population.size() * .1); //on choisit un pourcentage d'élites
//
//        for (int i = 0; i < nbElites; i++) {
//            new_generation.add(population.get(i));
//        }
////
//        // tant qu'on n'a pas le bon nombre

        while (new_generation.size() < population.size() - 1) {
            // on sélectionne les parents
            int pere = selection(adaptTotale);
            int mere = selection(adaptTotale);


            // ils se reproduisent
            Indiv[] enfants = (Indiv[]) population.get(pere).croisement(population.get(mere));

            // on les ajoute à la nouvelle génération
            new_generation.addAll(Arrays.asList(enfants));
        }

        // on applique une éventuelle mutation à toute la nouvelle génération
        for (Indiv individu : new_generation) {
            individu.mutation(prob_mut);
        }

        // on ajoute le max
        new_generation.add(individu_maximal());


//        for (int i = nbElites; i < population.size(); i++) {
//            new_generation.get(i).mutation(prob_mut);
//        }




        //on remplace l'ancienne par la nouvelle
        population = new_generation;
    }

    private List<Indiv> getElite(int nbElite){
        List<Indiv> elite = new ArrayList<>();
        List<Indiv> populationCopy = new ArrayList<>(population);
        populationCopy.sort(Comparator.comparing(Indiv::adaptation));
        for (int i = 0; i < nbElite; i++) {
            elite.add(populationCopy.get(i));
        }
        return elite;
    }

    /**
     * renvoie l'individu de la population ayant l'adaptation maximale
     */
    public Indiv individu_maximal() {
        return (Indiv) population.stream().max(Comparator.comparingDouble(Individu::adaptation)).orElseThrow(NoSuchElementException::new).clone();
    }

    /**
     * renvoie l'adaptation moyenne de la population
     */
    public double adaptation_moyenne() {
        return this.adaptationSum() / population.size();
    }

    public double adaptationSum() {
        return population.stream().mapToDouble(Individu::adaptation).sum();
    }

    /**
     * renvoie l'adaptation maximale de la population
     */
    public double adaptation_maximale() {
        return population.stream().map(Individu::adaptation).max(Double::compareTo).orElse(0.0);
    }
}
