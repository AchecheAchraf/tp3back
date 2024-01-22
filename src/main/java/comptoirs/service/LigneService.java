package comptoirs.service;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.LigneRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Ligne;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated // Les contraintes de validation des méthodes sont vérifiées
public class LigneService {
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    private final CommandeRepository commandeDao;
    private final LigneRepository ligneDao;
    private final ProduitRepository produitDao;

    // @Autowired
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    public LigneService(CommandeRepository commandeDao, LigneRepository ligneDao, ProduitRepository produitDao) {
        this.commandeDao = commandeDao;
        this.ligneDao = ligneDao;
        this.produitDao = produitDao;
    }

    /**
     * <pre>
     * Service métier :
     *     Enregistre une nouvelle ligne de commande pour une commande connue par sa clé,
     *     Incrémente la quantité totale commandée (Produit.unitesCommandees) avec la quantite à commander
     * Règles métier :
     *     - le produit référencé doit exister
     *     - la commande doit exister
     *     - la commande ne doit pas être déjà envoyée (le champ 'envoyeele' doit être null)
     *     - la quantité doit être positive
     *     - On doit avoir une quantité en stock du produit suffisante
     * <pre>
     *
     *  @param commandeNum la clé de la commande
     *  @param produitRef la clé du produit
     *  @param quantite la quantité commandée (positive)
     *  @return la ligne de commande créée
     */
    @Transactional
    public Ligne ajouterLigne(@NonNull Integer commandeNum, @NonNull Integer produitRef, @Positive int quantite) {
        var commande = commandeDao.findById(commandeNum).orElseThrow();
        if (commande.getEnvoyeele() != null) {
            throw new IllegalStateException("Commande déjà envoyée");
        }
        var produit = produitDao.findById(produitRef).orElseThrow();
        if (produit.getUnitesEnStock() < quantite + produit.getUnitesCommandees()) {
            throw new IllegalArgumentException("Pas assez de stock disponible");
        }

        produit.setUnitesCommandees(produit.getUnitesCommandees() + quantite);

        var oLigne = commande.getLignes().stream()
                .filter(l -> l.getProduit().getReference().equals(produitRef))
                .findAny(); // On cherche une ligne pour ce produit
        if (oLigne.isPresent()) {
            // Si la ligne existe, on met à jour la quantité
            var ligne = oLigne.get();
            ligne.setQuantite(oLigne.get().getQuantite() + quantite);
            return ligne;
        } else {
            // Sinon, on crée une nouvelle ligne
            var ligne = new Ligne(commande, produit, quantite);
            return ligneDao.save(ligne);
        }
    }

}
