package comptoirs.rest;

import comptoirs.dao.CommandeRepository;
import comptoirs.dto.CommandeProjection;
import comptoirs.entity.Commande;
import comptoirs.service.LigneService;

import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import comptoirs.service.CommandeService;
import org.springframework.web.servlet.view.RedirectView;

@RestController // Cette classe est un contrôleur REST
@RequestMapping(path = "/comptoirs/commande/other") // chemin d'accès
public class OtherCommandeController {

	private final CommandeService commandeService;
	private final LigneService ligneService;
	private final CommandeRepository commandeDao;
	
	// @Autowired
	public OtherCommandeController(CommandeService commandeService, LigneService ligneService, CommandeRepository commandeDao) {
		this.commandeService = commandeService;
		this.ligneService = ligneService;
		this.commandeDao = commandeDao;
	}

	@GetMapping("projection/{commandeNum}")
	public CommandeProjection projection(@PathVariable Integer commandeNum) {
		return commandeDao.findProjectionByNumero(commandeNum);
	}

	@GetMapping("ajouterPourClient/{clientCode}")
	public EntityModel<Commande> ajouterEntity(@PathVariable String clientCode) {
		return EntityModel.of(commandeService.creerCommande(clientCode));
	}

	@GetMapping("ajouterRedirect")
	public RedirectView ajouterLigneRedirect(@RequestParam Integer commandeNum, @RequestParam Integer produitRef, @RequestParam Integer quantite) {
		var ligne = ligneService.ajouterLigne(commandeNum, produitRef, quantite);
		return new RedirectView("/api/lignes/" + ligne.getId());
	}

}
