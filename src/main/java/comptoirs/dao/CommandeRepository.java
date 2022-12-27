package comptoirs.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import comptoirs.dto.CommandeProjection;
import comptoirs.entity.Commande;

// This will be AUTO IMPLEMENTED by Spring into a Bean called CommandeRepository

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    CommandeProjection findByNumero(Integer numero);

    @Query("select c from Commande c join fetch c.lignes l join fetch l.produit p join fetch c.client cl join fetch p.categorie ct where c.numero = :numero")
    CommandeProjection findProjectionByNumero(Integer numero);


}
