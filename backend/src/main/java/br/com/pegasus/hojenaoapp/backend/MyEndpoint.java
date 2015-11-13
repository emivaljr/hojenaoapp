/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package br.com.pegasus.hojenaoapp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.hojenaoapp.pegasus.com.br", ownerName = "backend.hojenaoapp.pegasus.com.br", packagePath = ""))
public class MyEndpoint {

    private Map<String,String> mapaEstados;

    private Map<String,Map<String,String>> mapaCidades;

    private Map<String,List<Feriado>> mapaFeriadosEstado;

    public MyEndpoint() {
    }

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);
        try {
           String[] var =  name.split(",");
           getHolidays(var[0], var[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @ApiMethod(name = "listaEstados")
    public ListaEstadosCidades listarEstados() throws Exception{
        inicializaMapaEstados();
        ListaEstadosCidades listaEstados = new ListaEstadosCidades();
        listaEstados.setLista(new ArrayList<String>(mapaEstados.keySet()));
        return listaEstados;

    }

    private void inicializaMapaEstados() throws IOException, ParserException {
        if(mapaEstados == null) {
            mapaEstados = new HashMap<String,String>();
            preencheMapaEstados();
        }
    }

    @ApiMethod(name = "listarCidades")
    public ListaEstadosCidades listarCidades(@Named("state") String state) throws Exception{
        inicializaMapaEstados();
        if(mapaCidades== null){
            mapaCidades = new HashMap<String,Map<String,String>>();
        }
        if(!mapaCidades.containsKey(state)) {
            Map<String,String> mapa = new HashMap<String, String>();
            mapaCidades.put(state,mapa);
            String responseBody = recuperarDados(mapaEstados.get(state), null);
            NodeList nodeList = filterSelectNode(responseBody);
            Node cidadeNode = nodeList.elementAt(2);
            SimpleNodeIterator iteratorEstado = cidadeNode.getChildren().elements();
            while (iteratorEstado.hasMoreNodes()) {
                OptionTag node = (OptionTag) iteratorEstado.nextNode();
                String cidadeId = node.getValue();
                String cidadeNome = node.getChildren().elements().nextNode().getText();
                if(!(cidadeNome.indexOf("Selecione") != -1)) {
                    //System.out.println(cidadeId+","+cidadeNome+","+mapaEstados.get(state));
                    mapa.put(cidadeNome, cidadeId);
                }
            }
        }
        ListaEstadosCidades listaEstados = new ListaEstadosCidades();
        listaEstados.setLista(new ArrayList<String>(mapaCidades.get(state).keySet()));
        return listaEstados;

    }
   /* FileOutputStream file;

    {
        try {
            file = new FileOutputStream(new File("/home/emival/Holiday.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    @ApiMethod(name = "getHolidays")
    public ListaFeriados getHolidays(@Named("city") String city,@Named("state") String state) throws Exception{
        System.out.println("city="+city+" state="+state);
        if(mapaFeriadosEstado == null){
            mapaFeriadosEstado = new HashMap<String,List<Feriado>>();
            preencheMapaFeriadosEstaduais();
        }


        inicializaMapaEstados();
        if(mapaCidades== null){
                mapaCidades = new HashMap<String,Map<String,String>>();
            }
            if(!mapaCidades.containsKey(state)) {
                Map<String,String> mapa = new HashMap<String, String>();
                mapaCidades.put(state,mapa);
                String responseBody = recuperarDados(mapaEstados.get(state), null);
                NodeList nodeList = filterSelectNode(responseBody);
                Node cidadeNode = nodeList.elementAt(2);
                SimpleNodeIterator iteratorEstado = cidadeNode.getChildren().elements();
                while (iteratorEstado.hasMoreNodes()) {
                    OptionTag node = (OptionTag) iteratorEstado.nextNode();
                    String cidadeId = node.getValue();
                    String cidadeNome = node.getChildren().elements().nextNode().getText();
                    mapa.put(cidadeNome,cidadeId);
                }
            }
            String responseBody = recuperarDados(mapaEstados.get(state), mapaCidades.get(state).get(city));
            StringBuilder sb = new StringBuilder(responseBody);
        List<String> list = new ArrayList<>();
        List<Feriado> lista = new ArrayList<Feriado>();
            while(sb.indexOf("<font size=1>")!= -1) {
                sb.delete(0, sb.indexOf("<font size=1>"));
                String feriado = sb.substring(sb.indexOf("<font size=1>")+13, sb.indexOf("</font>"));
                String[] valores = feriado.split("-");
                Feriado feriadoObj = new Feriado();
                feriadoObj.setData(valores[0].trim() + "/2015");
                feriadoObj.setNome(valores[1].trim());
                sb.delete(0, sb.indexOf("</font>") + 7);
                //file.write((feriadoObj.getData() + "," + feriadoObj.getNome() + "," + mapaCidades.get(state).get(city) + "," + mapaEstados.get(state) + IOUtils.LINE_SEPARATOR_UNIX).getBytes());
                lista.add(feriadoObj);
            }
        //file.flush();
        if(mapaFeriadosEstado.get(state)!=null) {
            lista.addAll(mapaFeriadosEstado.get(state));
        }
        ListaFeriados listaFeriados = new ListaFeriados();
        listaFeriados.setLista(lista);
        return listaFeriados;
    }

    private void preencheMapaEstados() throws IOException, ParserException {
        String responseBody = recuperarDados(null, null);
        NodeList nodeList = filterSelectNode(responseBody);
        Node estadoNode = nodeList.elementAt(1);
        SimpleNodeIterator iteratorEstado = estadoNode.getChildren().elements();
        while (iteratorEstado.hasMoreNodes()) {
            OptionTag node = (OptionTag) iteratorEstado.nextNode();
            String estadoId = node.getValue();
            String estadoNome = node.getChildren().elements().nextNode().getText();
            //System.out.println(estadoId+","+estadoNome);
            mapaEstados.put(estadoNome,estadoId);
        }

    }

    public static void main(String[] args) throws Exception {
        MyEndpoint m =new MyEndpoint();
        m.inicializaMapaEstados();

        for (String state: m.mapaEstados.keySet()){
            m.listarCidades(state);
        }
        m.mapaFeriadosEstado = new HashMap<>();
        m.preencheMapaFeriadosEstaduais();

        /*for (String state: m.mapaEstados.keySet()){
            for(String cidade :m.mapaCidades.get(state).keySet()){
                m.getHolidays(cidade, state);
            }
        }
        m.file.close();*/

    }

    private void preencheMapaFeriadosEstaduais() throws IOException, ParserException,ParseException {
        String estadosPage =  recuperarDadosEstado();
        StringBuilder stringBuilder = new StringBuilder(estadosPage);
        stringBuilder.delete(0,estadosPage.indexOf("<h3"));
        NodeList nodeEstadoList = filterTable(stringBuilder.toString());
        String todosMeses[] = {"janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        Map<String,String> mapaMeses = new HashMap<String,String>();
        int i = 1;
        for (String mes:todosMeses){
            String valor = String.valueOf(i++);
            if(valor.length()< 2){
                valor ="0"+valor;
            }
            mapaMeses.put(mes,valor);
        }

        String estado = null;
        for (Node node:nodeEstadoList.toNodeArray()){
            if(node instanceof TableTag){
                NodeList lista = ((TableTag) node).searchFor(TableColumn.class, true);
                SimpleNodeIterator iterator  = lista.elements();
                while (iterator.hasMoreNodes()){
                    Feriado feriado = new Feriado();
                    Node data = iterator.nextNode();
                    String[] dataExtenso = data.toPlainTextString().split(" de ");
                    feriado.setData(dataExtenso[0] + "/" + mapaMeses.get(dataExtenso[1]) + "/2015");
                    Node nome = iterator.nextNode();
                    feriado.setNome(nome.toPlainTextString());
                    Node lei = iterator.nextNode();
                    if(dataExtenso[0].length()==1){
                        dataExtenso[0] = "0"+dataExtenso[0];
                    }
                    System.out.println(dataExtenso[0] + "/" + mapaMeses.get(dataExtenso[1]) + "/2015,"+nome.toPlainTextString()+","+mapaEstados.get(estado));
                    mapaFeriadosEstado.get(estado).add(feriado);
                }

            }
            if(node instanceof HeadingTag){
                estado =  node.getChildren().toHtml().trim();
                if(node.getChildren().elementAt(0).getChildren() != null){
                    estado =  node.getChildren().elementAt(0).getChildren().toHtml().trim();
                }
                mapaFeriadosEstado.put(estado,new ArrayList<Feriado>());
            }

        }
    }

    private NodeList filterSelectNode(String responseBody) throws ParserException {
        Parser parser = Parser.createParser(responseBody, HTTP.ISO_8859_1);
        return parser.extractAllNodesThatMatch(new NodeFilter() {
            @Override
            public boolean accept(Node node) {
                if (node.getText().startsWith("select")) {
                    return true;
                }
                return false;
            }
        });
    }
    private NodeList filterTable(String responseBody) throws ParserException {
        Parser parser = Parser.createParser(responseBody, HTTP.ISO_8859_1);
        return parser.extractAllNodesThatMatch(new NodeFilter() {
            @Override
            public boolean accept(Node node) {
                if (node.getText().toUpperCase().startsWith("TABLE") || node.getText().toUpperCase().startsWith("H3")) {
                    return true;
                }
                return false;
            }
        });
    }

    private String recuperarDados(String estado, String cidade) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        HttpPost post = new HttpPost("http://www.feriadoscalendario.com.br/index.php");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ano","2015"));
        if(estado != null) {
            params.add(new BasicNameValuePair("est", estado));
        }
        if(cidade != null) {
            params.add(new BasicNameValuePair("mun", cidade));
        }
        post.setEntity(new UrlEncodedFormEntity(
                params, HTTP.ISO_8859_1));
        //System.out.println("Executing request " + post.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, Charset.defaultCharset()) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        return httpclient.execute(post, responseHandler);
        } finally {
        httpclient.close();
        }
    }
    private String recuperarDadosEstado() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet post = new HttpGet("http://www.feriadoscalendario.com.br/feriadosestaduais.php");

            //System.out.println("Executing request " + post.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, Charset.defaultCharset()) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            return httpclient.execute(post, responseHandler);
        } finally {
            httpclient.close();
        }
    }

}
