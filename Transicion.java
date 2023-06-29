
public class Transicion implements Comparable<Transicion> {
  /** Estado de origen */
  private final Estado estadoOrigen;
  /** Estado de destino */
  private final Estado estadoDestino;
  /** Expresión que transiciona */
  private final Expresion expresion;

  /**
   * Instanciar una transición
   * @param estadoOrigen Estado de donde parte la transición
   * @param expresion Expresión que transiciona
   * @param estadoDestino Estado a dond ellega la transición
   */
  public Transicion(Estado estadoOrigen, Expresion expresion, Estado estadoDestino) {
    this.estadoOrigen = estadoOrigen;
    this.expresion = expresion;
    this.estadoDestino = estadoDestino;
  }


  @Override
  public String toString() {
    return estadoOrigen.toString() + " --- " + expresion.getExpresionRegular() + " --> " + estadoDestino.toString();
  }

  public Estado getEstadoOrigen() { return estadoOrigen; }

 
  public Estado getEstadoDestino() { return estadoDestino; }

  
  public Expresion getExpresion() { return expresion; }

  /**
   * Para que las transiciones sean ordenables en base al estado de origen.
   * Si los estados tienen el mismo estado de origen, ahora se ordena en base a la expresión.
   * @param o el estado con el que será comparado
   * @return Entero resultado de la comparación
   */
  @Override
  public int compareTo(Transicion o) {
    int comparacion = this.estadoOrigen.compareTo(o.estadoOrigen);
    if (comparacion == 0)
      return this.expresion.getExpresionRegular().compareTo(o.expresion.getExpresionRegular());
    return comparacion;
  }
}
