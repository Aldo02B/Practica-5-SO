package Proceso;
import Utilidades.Utilidades;

import java.util.*;

/**
 * Contiene todos los métodos para crear, ver, eliminar procesos, desfragmentar memoria, imprimir tabla de páginas,
 * ver lista ligada, entre otras.
 *
 * @author Karen Mariel Bastida Vargas, Luis Aldo Gomez Bolaños
 * @version 2.0
 */
public class Proceso {
    String nombre; // Nombre del proceso.
    String id; //ID único del proceso.
    short instrucciones; // Número de instrucciones a ejecutar del proceso.
    int instruccionesEjecutadas; // Variable que lleva el total de instrucciones ejecutadas.
    short ejecuciones; // Contador que cuenta el número de ejecuciones de un proceso.
    int tam; // Tamaño original del proceso.
    /**/int[] tablaPaginas; // Tabla de páginas del proceso.
    static Scanner sc = new Scanner(System.in); // Objeto Scanner para poder obtener lecturas del teclado.


    public String getNombre() { return nombre; }
    public int getTam() { return tam; }


    /**
     * Crea un proceso en caso de que la memoria esté llena arrojara un error, caso contario declara las instrucciones
     * necesarias para poder crear el objeto asi como hacer las asignaciones en la memoria correspondientes, se
     * busca en la lista ligada su espacio correspondiente y se llena su arreglo de las direcciones de la tabla de
     * páginas.
     *
     * @param memory Sirve para saber el espacio en la memoria, que hay disponible para que un proceso lo use.
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     * @param listaProceso Lista en donde se almacenan los nombres de los procesos en memoria.
     * @return Si no se puede colocar el proceso en la memoria, retorna el valor del espacio disponible.
     */
    public static int crearProceso(int memory, Queue<Proceso> colaProc, List<String> listaProceso){
        // Se le pedirá al usuario los datos del nuevo proceso
        int tam = (int)Math.pow(2, (int)(Math.random()*(10-6)+6)); // En este metodo el min, o numero menor es exclusivo.

        if(memory < tam){
            System.out.println("No se puede crear el proceso, es necesario ejecutar o matar otros procesos");
            return memory;
        }else {
            // En este else se encuentra la creación del proceso en caso de que haya espacio disponible para el
            // Él usuario ingresa el nombre del proceso, también en la línea 41, se actualiza la memoria libre en el arreglo
            Proceso proceso = new Proceso();
            memory -= tam;
            System.out.println("Ingrese el nombre del proceso");
            proceso.nombre = sc.nextLine();
            proceso.id = UUID.randomUUID().toString(); // Este método de la clase UUID, se generan ids unicos de tipo String
            proceso.instrucciones = (short)(Math.random()*(30-10)+10); // Se le asigna aleatoriamente un numero de instrucciones al proceso, entre 10 y 30
            proceso.tam = tam; // El tamaño del proceso entre los valores asignados en la práctica, se asigna a ese atributo del objeto proceso
            /**/ proceso.tablaPaginas = new int[(tam/16)];
            colaProc.add(proceso);

            /**/Utilidades.buscarEspacioListaLigada(listaProceso, proceso);
            /**/Proceso.tablaPaginas(listaProceso, proceso);
        }
        return memory;
    }

    /**
     * Muestra el estado actual del proceso, los que están en cola, finalizados exitosamente y los eliminados. Se
     * muestra el estado actual de la memoria.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     * @param finalizados  Contiene los procesos finalizados del programa exitosamente.
     * @param eliminados Contiene procesos que no pudieron finalizar correctamente sus ejecuciones.
     */
    public static void estadoActual(Queue<Proceso> colaProc, List<Proceso> finalizados, List <Proceso> eliminados) {
        short cuentaProc = (short)colaProc.size();
        System.out.println("\nNumero de procesos en cola: " + cuentaProc);
        System.out.println("\nProcesos finalizados exitosamente:");
        Utilidades.imprimirLista(finalizados);
        System.out.println("\nProcesos eliminados:");
        Utilidades.imprimirLista(eliminados);
    }

    /**
     * Imprime la lista ligada que contiene todos los procesos cargados en memoria, el formato es el mismo como se vió
     * en la clase de teoría.
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     */
    /**/public static void verListaLigada(List<String> listaProcesos){
        for(int h = 0; h < 64; h++){
            System.out.println(h + ".- " + listaProcesos.get(h));
        }

        int cont1 = 0, cont2 = 0;
        String nombre;
        nombre = listaProcesos.get(0);

        for(int i = 0; i < 64; i++){ // Compara si existe un hueco en la memoria y los cuenta.
            if(Objects.equals(listaProcesos.get(i), nombre)){
                cont1++;
                if(listaProcesos.size() - 1 == i){
                    System.out.print("|" + nombre + "|" + cont2 + "|" + cont1 + "|-> ");
                    cont2 = cont2 + cont1;
                    cont1 = 1;
                }
            }else{
                System.out.print("|" + nombre + "|" + cont2 + "|" + cont1 + "|-> ");
                cont2 = cont2 + cont1;
                cont1 = 1;
                nombre = listaProcesos.get(i);
            }
        }
        System.out.println();
    }

    /**
     * Imprime información del proceso actual, el nombre, su identificador, instrucciones totales, instrucciones
     * ejecutadas y la tabla de páginas.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     */
    public static void procesoActual(Queue<Proceso> colaProc) {
        if (colaProc.isEmpty()) {
            System.out.println("No hay nada que ver aqui papu");
        } else {
            Proceso proceso = colaProc.peek();
            assert proceso != null;
            System.out.println("Proceso activo: " + proceso.nombre);
            System.out.println("ID: " + proceso.id);
            System.out.println("Tamanio: " + proceso.tam + "MB");
            System.out.println("Instrucciones totales: " + proceso.instrucciones);
            System.out.println("Instrucciones ejecutadas: " + proceso.instruccionesEjecutadas);
            System.out.println("Cargando la tabla de paginas...");
            /**/imprimirArreglo(proceso);
        }
    }

    /**
     * Ejecuta el primer proceso que está en cola, ejecuta 5 instrucciones y se coloca al final de la cola, en caso de
     * que finalice se liberan sus espacios de memoria asignados y se ingresa a la cola de finalizados exitosamente.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     * @param finalizados  Contiene los procesos finalizados del programa exitosamente.
     * @param memory Sirve para saber el espacio en la memoria, que hay disponible para que un proceso lo use.
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     */
    public static int ejecutarProceso(Queue<Proceso> colaProc, List <Proceso> finalizados, int memory, List<String> listaProcesos){
        if(colaProc.isEmpty()){
            System.out.println("No hay nada que ver aqui papu");
        }else{
            Proceso proceso = colaProc.poll();
            assert proceso != null;
            proceso.ejecuciones += 1;
            if(proceso.instrucciones-(proceso.ejecuciones*5) <= 0){ //Entra en caso de que el proceso tenga menos de 5 instrucciones
                System.out.println("\nEl proceso " + proceso.nombre + " ha concluido su ejecucion ");
                proceso.instruccionesEjecutadas = proceso.instrucciones;
                finalizados.add(proceso);
                /**/memory = Utilidades.liberarListaLigada(proceso, memory, listaProcesos);
            }else{
                System.out.println("Ejecutando el proceso actual....");
                System.out.println("Modificando parametros...checando errores");
                System.out.println("Listo!");
                proceso.instruccionesEjecutadas = proceso.ejecuciones*5;
                colaProc.add(proceso);
            }
        }
        return memory;
    }

    /**
     * Se pasa al siguiente proceso sin ejecutar las instrucciones del que se encuentra activo.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     */
    public static void siguienteProceso(Queue<Proceso> colaProc) {
        if (colaProc.isEmpty()) {
            System.out.println("No hay nada que ver aqui papu");
        }else{
            Proceso proceso = colaProc.poll();
            System.out.println("Pasando al siguiente proceso sin ejecutar instrucciones...");
            colaProc.add(proceso);
        }
    }

    /**
     * Elimina el proceso actual haciendo que no ejecute las instrucciones que restan, se libera la memoria que ocupa.
     *
     * @param colaProc Contiene a los procesos que se están ejecutando, incluyendo al proceso activo.
     * @param eliminados Contiene procesos que no pudieron finalizar correctamente sus ejecuciones.
     * @param memory Sirve para saber el espacio en la memoria, que hay disponible para que un proceso lo use.
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     */
    public static int matarProceso(Queue<Proceso> colaProc, List<Proceso> eliminados, int memory, List<String> listaProcesos){
        if(colaProc.isEmpty()){
            System.out.println("No hay nada que ver aquí papu");
        }else{
            Proceso proceso = colaProc.poll();
            assert proceso != null;
            System.out.println("Matando proceso: " + proceso.nombre);
            eliminados.add(proceso);
            /**/memory = Utilidades.liberarListaLigada(proceso, memory, listaProcesos);
            System.out.println("\nInstrucciones pendientes: " + (proceso.instrucciones - proceso.instruccionesEjecutadas));
        }
        return memory;
    }


    /**
     * Este método crea la tabla de páginas y la asigna al atributo del objeto proceso, recibe la lista y el objeto
     * Itera la lista de procesos, si encuentra un índice de la lista con el mismo nombre del objeto, le asigna al
     * arreglo su tabla de páginas que contendrá el número de su página, aumenta en cada iteración, y el
     * valor de i, que contiene la localidad o índice en donde se encuentra esa página del proceso seleccionado.
     * @param listaProcesos Lista en donde se almacenan los nombres de los procesos en memoria.
     * @param proceso Objeto que contiene los atributos de un proceso.
     */
    /**/public static void tablaPaginas(List<String> listaProcesos, Proceso proceso){
        int contador = 0;
        for(int i = 0; i < 64; i++){
            if(Objects.equals(listaProcesos.get(i), proceso.getNombre())){
                proceso.tablaPaginas[contador] = i;
                contador++;
            }
        }
    }

    /**
     * Imprime el arreglo de la tabla de páginas de un proceso, cada página representa una localidad de la memoria.
     *
     * @param proceso Objeto que contiene los atributos de un proceso.
     */
    /**/public static void imprimirArreglo(Proceso proceso){
        int cont = 0;
        for (int imp:proceso.tablaPaginas) {
            System.out.println("Proceso " + proceso.nombre + " pagina " + (cont+1) + " - " + imp);
            cont++;
        }
    }

    /**
     *  Imprime todos los atributos del objeto proceso.
     * @return Devuelve una sola cadena concantenada con todos los atributos.
     */
    public String imprimir(){
        return "Nombre: " + nombre + " | " + " Identificador: " + id + " | " + " Instrucciones: " + instrucciones + " | " + " Tamanio: " + tam + "MB";
    }
}