import java.util.Scanner;


public class Main {
  /**
   * Método inicial del programa.
   * Se lee la expresión regular del usuario, realiza la conversión e imprime los resultados.
   */
  public static void main(String[] args) {
    try {
      Scanner teclado = new Scanner(System.in);
      System.out.println("Convertidor de Expresiones Regulares a Autómatas Finitos");
      System.out.print("Ingrese la expresión regular: ");
      String entrada = teclado.nextLine();

      Convertidor app = new Convertidor(entrada); //el parametro es la expresion regular que ya tiene guardada en archivo.
      app.convertir();
      app.imprimirAFD();
      
    } catch(IllegalArgumentException e) {
      System.err.println(e);
    }
  }
}