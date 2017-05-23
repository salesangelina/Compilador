package Compilador;

/**
 *
 * @author Angelina Sales
 */
public class Tabela {
    String id, type;
    int linha;
    
    public Tabela(String id, String type, int linha){
        this.id = id;
        this.type = type;
        this.linha =linha;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getLinha() {
        return linha;
    }
    public String toString(){
        return "" + id + "\t\t" + type + "\t" + linha;
    }
}
