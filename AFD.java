import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AFD {
  /** Conjunto de simbolos del alfabeto */
  private final HashSet<Character> alfabeto;
  /** Conjunto de todos los estados */
  private final HashSet<Estado> estados;
  /** Estao incial del AFN */
  private final Estado estadoInicial;
  /** Estado final del AFN */
  private final Estado estadoFinal;
  /** Arreglo de transiciones */
  private final ArrayList<Transicion> transiciones;

  
  public AFD(Estado i, Estado f) {
    this.alfabeto = new HashSet<>();
    this.estadoInicial = i;
    this.estadoFinal = f;
    this.estados = new HashSet<>();
    this.estados.add(this.estadoInicial);
    this.estados.add(this.estadoFinal);
    this.transiciones = new ArrayList<>();
  }

 
  public Estado crearEstado() {
    // n es el subindice del nuevo estado q
    int n = this.estados.size();
    Estado q = new Estado("q" + n, Estado.TiposEstados.NOFINAL);
    // Estado q = new Estado(String.valueOf(n), Estado.TiposEstados.NOFINAL);
    this.estados.add(q);
    return q; 
  }

  
  public Transicion crearTransicion(Estado estadoOrigen, Expresion expresionRegular, Estado estadoDestino) {
    Transicion t = new Transicion(estadoOrigen, expresionRegular, estadoDestino);
    this.transiciones.add(t);
    return t;
  }


  public void calcularAlfabeto(String expresionRegular) {
    // Quitar todos los metacaracteres () [] + | , *
    expresionRegular = expresionRegular.replaceAll("[()\\[\\]+*,|]", "");

    // Recorrer los caracteres y agregarlos al conjunto de simbolos
    // Al ser un HashSet, los simbolos repetidos no se agregaran
    for (int i = 0; i < expresionRegular.length(); i++) {
      char simbolo = expresionRegular.charAt(i);
      this.alfabeto.add(simbolo);
    }
  }

  /**
   * Obtener el quíntuplo del AFD
   * 
   * @return El String del quíntuplo
   */
  @Override
  public String toString() {
    return this.estadosToString() + "\n" +
        this.alfabetoToString() + "\n" +
        "Estado Final = {" + this.estadoFinal.getNombre() + "}\n" +
        "Estado Inicial = " + this.estadoInicial.getNombre() + "\n" +
        this.transicionesTostring();
  }

  /**
   * Obtener el conjunto de estados ordenados entre {} y separados por comas.
   * Ejemplo: K = { q0, q1, q2 }
   * 
   * @return El String del conjunto de estados
   * @see Estado#compareTo(Estado)
   */
  private String estadosToString() {
    // Para ordenar los estados lexicográficamente
    TreeSet<Estado> estadosOrdenados = new TreeSet<>(this.estados);
    //contador de estados
    int contador = 0;

    String conjuntoK = "Estados = {";

    // Iterar los elementos
    Iterator<Estado> estados = estadosOrdenados.iterator();
    while (estados.hasNext()) {
      conjuntoK = conjuntoK.concat(estados.next().getNombre());
      // Si no es el ultimo estado, agregar una coma
      if (estados.hasNext())
        conjuntoK = conjuntoK.concat(", ");
      contador++;
    }
    conjuntoK = conjuntoK.concat("}");
    conjuntoK = conjuntoK.concat(" = " + String.valueOf(contador));
    return conjuntoK;
  }

  /**
   * Obtener el conjunto de simbolos del alfabeto entre {} y separados por comas.
   * Ejemplo: E = { a, b, c }
   * 
   * @return El String del conjunto de simbolos
   */
  private String alfabetoToString() {
    String conjuntoE = "Alfabeto = {";

    // Iterar los elementos
    Iterator<Character> simbolos = this.alfabeto.iterator();
    while (simbolos.hasNext()) {
      conjuntoE = conjuntoE.concat(simbolos.next().toString());
      // Si no es el ultimo estado, agregar una coma
      if (simbolos.hasNext())
        conjuntoE = conjuntoE.concat(", ");
    }
    conjuntoE = conjuntoE.concat("}");
    return conjuntoE;
  }

  /**
   * Obtener la tabla de transiciones ordenada.
   * Solo se incluyen las transiciones donde la expresión solo es un símbolo del
   * alfabeto o la palabra vacía
   * 
   * @return El String de la tabla de transiciones
   * @see Transicion#compareTo(Transicion)
   */
  private String transicionesTostring() {
    // String relacionTransicion = "D =\nq\to\tD(q,o)\n";
    String relacionTransicion ="\nTabla de transiciones\n";
    // Ordenar las transiciones lexicográficamente
    this.transiciones.sort(Comparator.naturalOrder());

    for (Transicion t : this.transiciones) {
      // Mostrar solo las transiciones con expresiones minimas
      if (!t.getExpresion().esExpresionMinima())
        continue;

      relacionTransicion = relacionTransicion.concat(
          t.getEstadoOrigen().getNombre() + "\t" +
              t.getExpresion().getExpresionRegular() + "\t" +
              t.getEstadoDestino().getNombre() + "\n");
    }
    return relacionTransicion;
  }

 
  public void imprimirArchivo(String nombreArchivo) throws IOException {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(nombreArchivo));
      writer.write(this.toString());
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

}
