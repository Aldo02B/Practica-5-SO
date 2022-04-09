import Proceso.Proceso;
import Utilidades.Utilidades;

import java.util.*;

/* SALUDOS DESDE GITHUB */

/**
 * Clase principal, contiene el método main que llama a los diferentes métodos para crear, borrar, guardar procesos,
 * desfragmentar memoria, ver lista ligada, etc.
 * Se declara la memoria, listas, colas.
 *
 * @author Karen Mariel Bastida Vargas, Luis Aldo Gomez Bolaños
 * @version 2.0
 */
public class Principal {
    static Scanner sc = new Scanner(System.in); // Objeto Scanner para poder obtener lecturas del teclado.

    /**
     * Corre el programa principal, contiene el menú para llamar a los demás procesos.
     * @param args Parámetros por default del método main
     */
    public static void main(String[] args) {
        short opc = 0;
        /**/int memory = 1024; // Variable que nos ayuda a controlar el tamaño de la memoria.
        Queue <Proceso> colaProc = new LinkedList<>(); // Contiene los procesos activos y los que están en espera.
        List <Proceso> finalizados = new LinkedList<>(); // Lista de procesos finalizados.
        List <Proceso> eliminados = new LinkedList<>(); // Lista de procesos eliminados.
        /**/List <String> listaProcesos = new LinkedList<>(); // Lista de procesos en memoria.
        /**/Utilidades.llenarLista(listaProcesos); // Se llena la lista donde se almacenarán los procesos.

        // MENÚ
        do {
            System.out.println("""                                      
                    \sADMINISTRADOR DE PROCESOS
                    Elija una opcion:\s
                    1)Crear nuevo Proceso\s
                    2)Ver estado de los procesos\s
                    3)Imprimir cola de procesos\s
                    4)Ver estado de la memoria (lista ligada)
                    5)Ver proceso actual\s
                    6)Ejecutar proceso actual\s
                    7)Pasar al siguiente proceso\s
                    8)Matar proceso actual\s
                    9)Desfragmentar memoria\s
                    10)Salir""");
            try{
                opc = sc.nextShort();
                switch (opc) {
                    case 1 -> {
                        System.out.println("MEMORIA ACTUALIZADA = " + memory);
                        memory = Proceso.crearProceso(memory, colaProc, listaProcesos);

                    }
                    case 2 -> Proceso.estadoActual(colaProc, finalizados, eliminados);
                    case 3 -> Utilidades.imprimirCola(colaProc);
                    /**/case 4 -> Proceso.verListaLigada(listaProcesos);
                    case 5 -> Proceso.procesoActual(colaProc);
                    case 6 -> memory = Proceso.ejecutarProceso(colaProc, finalizados, memory, listaProcesos);
                    case 7 -> Proceso.siguienteProceso(colaProc);
                    case 8 -> memory = Proceso.matarProceso(colaProc, eliminados, memory, listaProcesos);
                    /**/case 9 -> {
                        Utilidades.desfragmentarMemoria(listaProcesos);
                        Utilidades.actualizarTabla(listaProcesos, colaProc);
                    }
                    case 10 -> {
                        System.out.println("Saliendo...");
                        Utilidades.imprimirCola(colaProc);
                    }
                    default -> System.out.println("Digita un numero valido");
                }
            }catch (InputMismatchException exe){
                System.out.println("Tienes que digitar un numero, intente de nuevo.");
                sc.nextLine();
            }
        }while (opc != 10);
    }
}
