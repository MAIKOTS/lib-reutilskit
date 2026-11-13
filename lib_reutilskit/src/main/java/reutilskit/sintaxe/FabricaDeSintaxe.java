package reutilskit.sintaxe;
import android.content.Context;

public class FabricaDeSintaxe {
    private static GerenciadorSintaxeAssets gerenciador;

    public static void inicializar(Context contexto) {
        if (gerenciador == null) gerenciador = new GerenciadorSintaxeAssets(contexto);
    }

    public static SintaxeJson obterSintaxePorNome(String nomeArquivo) {
        if (nomeArquivo == null || gerenciador == null) return null;
        int barraIndex = nomeArquivo.lastIndexOf('/');
        if (barraIndex != -1) nomeArquivo = nomeArquivo.substring(barraIndex + 1);

        String chave;
        int pontoIndex = nomeArquivo.lastIndexOf('.');
        if (pontoIndex <= 0 || pontoIndex == nomeArquivo.length() - 1) {
            chave = nomeArquivo.toLowerCase();
        } else {
            chave = nomeArquivo.substring(pontoIndex + 1).toLowerCase();
        }
        return gerenciador.obterSintaxePorExtensao(chave);
    }
}