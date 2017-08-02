package br.com.dtech.services;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import br.com.dtech.model.Cerveja;
import br.com.dtech.model.CervejaJaExisteException;
import br.com.dtech.model.Estoque;
import br.com.dtech.model.rest.Cervejas;

@Path("/cervejas")
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
public class CervejaService {

	private static Estoque estoques = new Estoque();
	
	private static final int TAMANHO_PAGINA = 20;
	
	@GET
	public Cervejas listaTodasAsCervejas(@QueryParam("pagina") final int paginaParam) {
		final List<Cerveja> cervejas = estoques.listarCervejas(paginaParam, TAMANHO_PAGINA);
		return new Cervejas(cervejas);
	}
	
	@GET
	@Path("{nome}")
	public Cerveja encontreCerveja(@PathParam("nome") final String nomeDaCerveja) {
		final Cerveja cerveja = estoques.recuperarCervejaPeloNome(nomeDaCerveja);
		if (cerveja != null) {
			return cerveja; 
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}
	
	@POST
	public Response criarCerveja(final Cerveja cerveja) {
		try {
			
			estoques.adicionarCerveja(cerveja);
			
		} catch (final CervejaJaExisteException e) {
			throw new WebApplicationException(Status.CONFLICT);
		}
		
		final URI url = UriBuilder.fromPath("cervejas/{nome}").build(cerveja.getNome());
		
		return Response.created(url).entity(cerveja).build();
	}
	
	@PUT
	@Path("{nome}")
	public void atualizarCerveja(@PathParam("nome") final String nome, final Cerveja cerveja) {
		this.encontreCerveja(nome);
		cerveja.setNome(nome);
		estoques.atualizarCerveja(cerveja);
	}
	
	@DELETE
	@Path("{nome}")
	public void apagarCerveja(@PathParam("nome") final String nome) {
		estoques.apagarCerveja(nome);
	}
	
}
