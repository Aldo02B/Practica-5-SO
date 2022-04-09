package Utilidades;
import Proceso.Proceso;

import java.util.*;

/**
 * Esta clase se encarga de métodos simples ya sea para llenar la memoria, imprimir listas, colas, etc.
 * Tiene la finalidad de no sobrecargar la clase Proceso.
 *
 * @author Karen Mariel Bastida Vargas, Luis Aldo Gomez Bolaños
 * @version 2.0
 */
public class Utilidades {

    /**
     * Este método llena o inicializa la lista, para que cuando se agreguen procesos pueda aparecer el hueco
     * del resto de la memoria que inicialmente no tiene procesos.
     *
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     */
    /**/public static void llenarLista(List<String> listaProcesos){
        for (int i = 0; i < 64; i++) {
            listaProcesos.add("H");
        }
    }

    /**
     * Imprime los elementos de la cola.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     */
    public static void imprimirCola(Queue<Proceso> colaProc){
        if(colaProc.isEmpty()){
            System.out.println("No hay nada que imprimir");
        }else{
            for (Proceso imp : colaProc){
                System.out.println(imp.imprimir());
            }
        }

    }

    /**
     * Imprime los procesos de la lista.
     *
     * @param impr Lista que contiene objetos Proceso.
     */
    public static void imprimirLista(List<Proceso> impr){
        int cont = 1;
        if(impr.isEmpty()){
            System.out.println("No hay procesos");
        }else {
            for(Proceso imp : impr){
                System.out.println(cont + ".- " + imp.getNombre() + "\n");
            }
        }
    }

    /**
     * Esta clase tiene el objetivo de buscar espacios para las páginas de los procesos.
     *
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     * @param proceso Objeto que contiene los atributos de un proceso.
     */
    /**/public static void buscarEspacioListaLigada(List<String> listaProcesos, Proceso proceso){
        short espacio = (short) (proceso.getTam()/16); // Esta operación sirve para saber el número de páginas que ocupa un proceso.

        for(int i = 0; i < 64; i++){
            String nombre = listaProcesos.get(i); // Se itera la lista para obtener el valor del índice de la lista.
            if(nombre.equals("H")){ // Si el valor o cadena que se encuentra en ese índice es H, significa que es un hueco.
                listaProcesos.set(i, proceso.getNombre()); // Se asigna el valor del nombre del proceso en el índice donde se
                espacio--;                                 // encuentra la H.
            }   // Se resta el espacio, ya que se asignan las páginas a las localidades, cuando esta variable es 0, ya no se asigna el nombre.
            if (espacio == 0){
                break;
            }
        }
    }

    /**
     * Libera la memoria y sustituye el nombre del proceso por un "H", lo que significa que es una
     * localidad o espacio libre.
     *
     * @param proceso Objeto proceso que contiene los atributos de un proceso.
     */
    /**/public static int liberarListaLigada(Proceso proceso, int memory, List<String> listaProceso){
        for(int i = 0; i < 64; i++){
            if(Objects.equals(proceso.getNombre(), listaProceso.get(i))){
                listaProceso.set(i, "H");
            }
        }
        System.out.println("Liberando memoria...");
        return memory + proceso.getTam();
    }

    /**
     * Este método actualiza la lista para que no haya huecos entre los procesos, si un proceso termina de ejecutarse
     * o se elimina, entonces recorre todos los procesos a las localidades de memoria que les correspondan
     * para que se encuentren de forma consecutiva.
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     */
    /**/public static void desfragmentarMemoria(List<String> listaProcesos) {
        String nombre;
        for(int i = 0; i < 64; i++){
            if(Objects.equals(listaProcesos.get(i), "H")){
                for(int j = i; j < 64; j++){
                    if(!Objects.equals(listaProcesos.get(j), "H")){
                        nombre = listaProcesos.get(j);
                        listaProcesos.set(i, nombre);
                        listaProcesos.set(j, "H");
                        i++;
                    }
                }
            }
        }
        System.out.println("\nDesfragmentando memoria...");
        System.out.println("Nueva lista de procesos:");
        Proceso.verListaLigada(listaProcesos);
    }

    /**
     * Este método tiene como objetivo actualizar la tabla de páginas de los procesos después de haber usado la opción
     * desfragmentación, ya que no se actualiza automáticamente, lo que hace es quitar la cabeza de la cola,
     * modificando sus nuevas localidades, y después lo agrega de nuevo, hasta que todos los procesos tienen su tabla
     * de páginas modificadas
     *
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     * @param colaProceso Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     */
    public static void actualizarTabla(List<String> listaProcesos, Queue<Proceso> colaProceso){
        for (int i = 0; i < colaProceso.size(); i++) { // El for itera hasta el número de procesos existentes
            Proceso proceso = colaProceso.poll();
            Proceso.tablaPaginas(listaProcesos,proceso);
            colaProceso.add(proceso);
        }
    }
}