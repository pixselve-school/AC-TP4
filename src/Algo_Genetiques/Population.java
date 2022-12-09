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
    public int selection(double adapt_totale) {
        double alea = Math.random() * adapt_totale;
        double somme = 0;
        int i = 0;
        while (somme < alea) {
            somme += population.get(i).adaptation();
            i++;
        }
        return i - 1;
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
        // Élitisme
        new_generation.add(individu_maximal());


        //on remplace l'ancienne par la nouvelle
        population = new_generation;
    }

    /**
     * renvoie l'individu de la population ayant l'adaptation maximale
     */
    public Indiv individu_maximal() {
        return population.stream().max(Comparator.comparingDouble(Individu::adaptation)).orElseThrow(NoSuchElementException::new);
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
