import java.util.ArrayList;

public class Expresion {
  /** La expresión regular */
  private String expresionRegular;

  /**
   * Instanciar una expresión regular
   * 
   * @param expresionRegular Expresión regular que representa
   */
  public Expresion(String expresionRegular) {
    this.expresionRegular = expresionRegular;
    this.quitarEspacios();
    this.quitarAgrupacion();
  }

  
  public boolean esValida() {
    return
    // Inválida si solo contiene un simbolo de unión o de Kleene
    !this.expresionRegular.equals("*") &&
        !this.expresionRegular.equals("+") &&
        !this.expresionRegular.equals(",") &&
        !this.expresionRegular.equals("|") &&
        // Inválida si contiene más de 1 simbolo de unión o de Kleene seguidos
        !this.expresionRegular.contains("**") &&
        !this.expresionRegular.contains("++") &&
        !this.expresionRegular.contains(",,") &&
        !this.expresionRegular.contains("||") &&
        // Inválida si contiene un simbolo de unión seguido de cerradura de Kleene
        !this.expresionRegular.contains("+*") &&
        !this.expresionRegular.contains(",*") &&
        !this.expresionRegular.contains("|*") &&
        // Válida si tiene () o [] balanceados
        estaBalanceada(this.expresionRegular);
  }


  static public boolean estaBalanceada(String expresionRegular) {
    Pila pila = new Pila();

    for (int i = 0; i < expresionRegular.length(); i++) {
      char c = expresionRegular.charAt(i);

      // Si hay un caracter de apertura, guardarlo en la pila
      if (c == '(' || c == '[')
        pila.insertar(c);

      // Si hay un caracter de cierre
      if (c == ')' || c == ']') {
        // si el primer caracter es de cierre, es incorrecto
        if (pila.estaVacia())
          return false;

        Nodo ultimo = pila.removerUltimo();
        // Si el ultimo caracter no corresponde al de cierre, es incorrecto
        if (c == ')' && (char) ultimo.getDato() != '(')
          return false;
        if (c == ']' && (char) ultimo.getDato() != '[')
          return false;
      }
    }

    // Si quedo algun caracter en la pila, es incorrecto
    return pila.estaVacia();
  }

 
  public void quitarAgrupacion() {
    String tmp = this.expresionRegular;
    if ((this.expresionRegular.startsWith("(") && this.expresionRegular.endsWith(")")) ||
        (this.expresionRegular.startsWith("[") && this.expresionRegular.endsWith("]")))
      tmp = this.expresionRegular.substring(1, this.expresionRegular.length() - 1);

    if (estaBalanceada(tmp))
      this.expresionRegular = tmp;
  }

  
  public boolean esExpresionMinima() {
    return this.expresionRegular.length() == 1;
  }

  /**
   * Verificar si la expresión regular se trata de una unión
   * 
   * @return True si al separar la expresión por uniones, se obtiene más de una
   *         subexpresión
   */
  public boolean esUnion() {
    return separarUniones(this).size() > 1;
  }

  /**
   * Verificar si la expresión regular se trata de una concatenación
   * 
   * @return True si al separar por concatenaciones se obtiene más de una
   *         subexpresión
   */
  public boolean esConcatenacion() {
    return separarConcatenaciones(this).size() > 1;
  }

  /**
   * Verificar si la expresion regular se trata de una cerradura de Kleene
   * 
   * @return si termina con *, o empieza y termina con ()* o []* y si a expresión
   *         no es concatenación
   */
  public boolean esCerraduraKleene() {
    return separarConcatenaciones(new Expresion(this.expresionRegular)).size() == 1 &&
        ((this.expresionRegular.endsWith("*") && this.expresionRegular.length() == 2) ||
            (this.expresionRegular.startsWith("(") && this.expresionRegular.endsWith(")*")) ||
            (this.expresionRegular.startsWith("[") && this.expresionRegular.endsWith("]*")));
  }

  /**
   * Separar la expresión regular por los metacaracteres de unión (+, | y ,)
   * Ejemplo: a|b+c,d = a, b, c, d
   * 
   * @param expresionRegular Expresión regular a separar
   * @return Arreglo de sub expresiones resultantes
   */
  static public ArrayList<Expresion> separarUniones(Expresion expresionRegular) {
    String acomulador = "";
    ArrayList<Expresion> subexpresiones = new ArrayList<>();

    for (int i = 0; i < expresionRegular.getExpresionRegular().length(); i++) {
      char c = expresionRegular.getExpresionRegular().charAt(i);

      // Si se encuentra un metacaracter de union que este fuera de () o de []
      // crear una nueva sub expresion con lo ya acomulado
      if ((c == '+' || c == ',' || c == '|') && estaBalanceada(acomulador)) {
        subexpresiones.add(new Expresion(acomulador));
        acomulador = "";
      }
      // sino, seguir acomulando
      else
        acomulador = acomulador + c;
    }

    // Agregar el ultimo acomulado
    // Si no se generaron subexpresiones, significa que solo se devuelve la
    // expresión completa
    subexpresiones.add(new Expresion(acomulador));
    return subexpresiones;
  }

  /**
   * Separar la expresión regular por concatenaciones
   * Ejemplo: a(bc)d* = a, (bc), d*
   * 
   * @param expresionRegular Expresión regular a separar
   * @return Arreglo de sub expresiones resultantes
   */
  static public ArrayList<Expresion> separarConcatenaciones(Expresion expresionRegular) {
    String acomulador = "";
    ArrayList<Expresion> subexpresiones = new ArrayList<>();

    // Si la expresion se trata de una union, no se tiene que separar por
    // concatenación
    if (expresionRegular.esUnion()) {
      subexpresiones.add(expresionRegular);
      return subexpresiones;
    }

    for (int i = 0; i < expresionRegular.getExpresionRegular().length(); i++) {
      char c = expresionRegular.getExpresionRegular().charAt(i);
      acomulador += c;

      // Si lo acomulado no tiene parentesis balanceados, saltar a la siguiente
      // iteración
      if (!Expresion.estaBalanceada(acomulador))
        continue;

      // Si ya esta balanceado y se ha llegado al ultimo caracter de la expresión
      if (i == expresionRegular.getExpresionRegular().length() - 1) {
        // Guardar la ultima acomulación
        subexpresiones.add(new Expresion(acomulador));
        break; // Terminar el loop for
      }

      // Si el siguiente caracter es cerradura de Kleene o un simbolo de union, saltar
      // a la siguiente iteración
      if (expresionRegular.getExpresionRegular().charAt(i + 1) == '*')
        continue;

      // En este punto el caracter o expresión está balanceada, es un * y no le sigue
      // un *, por lo que se puede separar
      subexpresiones.add(new Expresion(acomulador));
      acomulador = "";
    }

    return subexpresiones;
  }


  public void quitarCerraduraKleene() {
    if (esCerraduraKleene()) {
      this.expresionRegular = this.expresionRegular.substring(0, this.expresionRegular.length() - 1);
      this.quitarAgrupacion();
    }
  }

  
  public void quitarEspacios() {
    this.expresionRegular.replaceAll("\\s+", "");
  }


  public String getExpresionRegular() {
    return this.expresionRegular;
  }

  
  /**
   * Clases necesarias para que funcione Expresion
   */

  private static class Nodo {

    private Object dato;
    private Nodo siguiente;

   
    public Nodo(Object dato, Nodo siguiente) {
      this.dato = dato;
      this.siguiente = siguiente;
    }

    
    public Object getDato() {
      return this.dato;
    }

    
    public void setDato(Object dato) {
      this.dato = dato;
    }

  
    public Nodo getSiguiente() {
      return this.siguiente;
    }

    
    public void setSiguiente(Nodo siguiente) {
      this.siguiente = siguiente;
    }
  }


  private static class Pila {
    
    private Nodo ultimo;
    /** Número de elementos de la pila */
    private byte longitud;

    /**
     * Instanciar una pila vacía
     */
    public Pila() {
    }

   
    public Pila(Object dato) {
      ultimo = new Nodo(dato, null);
      longitud = 1;
    }

 
    public void insertar(Object dato) {
      ultimo = new Nodo(dato, ultimo);
      longitud++;
    }

  
    public Nodo removerUltimo() {
      if (estaVacia())
        return null;

      Nodo nodoEliminado = ultimo;
      ultimo = ultimo.getSiguiente();
      nodoEliminado.setSiguiente(null);

      longitud--;
      return nodoEliminado;
    }

    
    public Nodo obtenerUltimo() {
      return ultimo;
    }


    public boolean estaVacia() {
      return longitud == 0;
    }

   
    public String toString() {
      String longitudPila = "Longitud: " + longitud + " elementos\n";
      String texto = "";
      Nodo nodoActual = ultimo;

      while (nodoActual != null) {
        texto = (nodoActual.getSiguiente() == null ? "" : " <- ") + nodoActual.getDato() + texto;
        nodoActual = nodoActual.getSiguiente();
      }

      return longitudPila + texto;
    }

 
    public byte getLongitud() {
      return longitud;
    }
  }

}
