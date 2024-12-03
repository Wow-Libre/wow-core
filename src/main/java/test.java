import java.util.*;

public class test {


    public static void main(String[] args) {
        // Lista de nombres de personajes
        String[] nombres = {
                "Sasha", "Paudd", "Graótiko", "Yaniel", "Brujacio", "Azmuy", "Jatkal",
                "Sleymo", "Tupapa", "Calbincley", "Durotar", "Sylar", "Ckusco", "Albedo",
                "Garrows", "Lili", "Missa", "Skulltracer", "Teamo", "Nomercy", "Macheto",
                "Mbarakaja", "Stomo", "Tense", "Kaima", "Heracross", "Syf", "Lunch",
                "Magui", "Luke", "Dasga", "Itachi", "Chandalari", "Chamanita", "Darkray",
                "Rock", "Kijana", "Sed", "Arthass", "Curandera", "Soytankebro", "Nortlel",
                "Broxxigar", "Kendo", "Vacuno", "Bazaliell", "Noney", "Glimk", "Dasdru",
                "Kasique", "Aishaa", "Spells", "Taltaro", "Ralekk", "Zirigduim", "Caosxd",
                "Caos", "Youlouse", "Pristkill", "Caoos", "Catatau", "Evito", "Gritona",
                "Shuncha", "Chamaquito", "Leila", "Natasha", "Xiths", "Lamuerte", "Rextroy",
                "Rudios", "Satella", "Roadark", "Wotndark", "Pentagono", "Madara", "Bron",
                "Adouken", "Mcqeen", "Kanna", "Twitchblade", "Venganza", "Walo", "Asdasd",
                "Baein", "Artia", "Missae", "Blaziken", "Tito", "Caraechimba", "Caradevrga",
                "Putarraca", "Chupapijas", "Passenger", "Sophitya", "Cowalski", "Reanette",
                "Luigi", "Toad", "Azai", "Cptfalcon", "Admin", "Verhatrixx", "Kratos",
                "Baby", "Erick"
        };


        // Lista de códigos de ítems
        String[] codigos = {
                "51395", "51535", "51411", "51412", "51481", "51480",
                "51432", "51404", "51402", "51431", "51456", "51400", "51401", "51405",
                "51403", "51457", "51450", "51449",
                "51389", "51388", "51439", "51526", "51440", "51515", "51392", "51393",
                "51516", "51448", "51522",
                "51518", "51528", "51398", "51442", "51399", "51529", "51524", "51444",
                "51390", "51520", "51446", "51454",
                "51454", "51451", "51531", "51410"
        };


        // Generar comandos
        List<String> comandos = generarComandos(nombres, codigos, 12);

        // Imprimir los comandos generados
        for (String comando : comandos) {
            System.out.println(comando);
        }
    }

    /**
     * Genera los comandos .send items para cada nombre y conjunto de ítems.
     *
     * @param nombres       Lista de nombres de personajes.
     * @param codigos       Lista de códigos de ítems.
     * @param maxPorComando Máximo número de ítems por comando.
     * @return Lista de comandos generados.
     */
    public static List<String> generarComandos(String[] nombres, String[] codigos, int maxPorComando) {
        List<String> comandos = new ArrayList<>();

        for (String nombre : nombres) {
            // Dividir los códigos en lotes de tamaño máximo permitido
            for (int i = 0; i < codigos.length; i += maxPorComando) {
                StringBuilder comando = new StringBuilder();

                // Agregar comando inicial
                comando.append(".send items ").append(nombre).append(" \"\" \"\" ");

                // Agregar ítems al comando, asegurando que no se excedan los 12 elementos
                for (int j = i; j < i + maxPorComando && j < codigos.length; j++) {
                    comando.append(codigos[j]).append(":1 ");
                }

                // Agregar el comando completo a la lista
                comandos.add(comando.toString().trim());
            }
        }


        return comandos;
    }


}
