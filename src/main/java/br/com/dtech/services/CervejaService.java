package br.com.dtech.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import br.com.dtech.model.Cerveja;
import br.com.dtech.model.CervejaJaExisteException;
import br.com.dtech.model.Estoque;
import br.com.dtech.model.rest.Cervejas;

@Path("/cervejas")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class CervejaService {

	private static Estoque estoques = new Estoque();
	
	private static final int TAMANHO_PAGINA = 20;
	
	private static Map<String, String> EXTENSOES;
	
	static {
		EXTENSOES = new HashMap<String, String>();
		EXTENSOES.put("image/jpg", ".jpg");
		
	}
	
	@POST
	@Path("{nome}")
	@Consumes("image/*")
	public Response criaImagem(@PathParam("nome") final String nomeDaImagem, @Context final HttpServletRequest request, final byte[] dados) throws IOException {
		final String userHome = System.getProperty("user.home");
		final String mimeType = request.getContentType();
		final FileOutputStream fileOutputStream = new FileOutputStream(userHome + File.separator + nomeDaImagem + EXTENSOES.get(mimeType));
		fileOutputStream.write(dados);
		fileOutputStream.flush();
		fileOutputStream.close();
		
		return Response.ok().build();
	}
	
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
	
	@HEAD
	@Path("{nome}")
	public Cerveja encontreCervejaHead(@PathParam("nome") final String nomeDaCerveja) {
		final Cerveja cerveja = estoques.recuperarCervejaPeloNome(nomeDaCerveja);
		if (cerveja != null) {
			return cerveja; 
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	@GET
	@Path("buscacerveja/nome/{nome}/descricao/{descricao}")
	public Cerveja buscaPorNomeDescricao(@PathParam("nome") final String nomeDaCerveja, @PathParam("descricao") final String decricao) {
		final Cerveja cerveja = estoques.recuperarCervejaPeloNome(nomeDaCerveja);
		if (cerveja != null) {
			return cerveja; 
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	
	@GET
	@Path("{nome}")
	@Produces("image/*")
	public Response recuperarImagem(@PathParam("nome") final String nomeDaCerveja) throws IOException {
		final InputStream stream = CervejaService.class.getResourceAsStream("/" + nomeDaCerveja + ".jpg");
		
		if (stream == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		final byte[] dados = new byte[stream.available()];
		stream.read(dados);
		stream.close();
		
		return Response.ok(dados).type("image/jpg").build();
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
