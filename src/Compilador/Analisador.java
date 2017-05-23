/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 *
 * @author Angelina Sales
 */
public class Analisador {
    
    static LinkedList<Tabela> tab;
    static LinkedList<Variaveis> pilhaVar = new LinkedList<Variaveis>(), pilhaProc = new LinkedList<Variaveis>();
    static LinkedList<String> pilhaTipo = new LinkedList<String>();
    static Tabela elem;
    static int indice = 0, escopo = 0;
    static boolean escopoFuncao = false;
    

    public static void main(String[] args) {
        String[] palavraReservada = {"program", "var", "integer", "real", "boolean","procedure", "begin", "end", "if", "then", "else", "while", "do", "not", "for", "to"};
        
        int linha = 1;
            //linhaAtual = -1,
            //iV = 0;
        
        String entrada = "";
        //String entrada1 = "bb";
        
        boolean isOperador = false,
                isFloat = false,
                isReservado = false,
                isComentario = false,
                isNewNumber = false;
                //isCComentario = false,
                //isNaoN = false;
        
        
        try {
            
            BufferedReader leitura = new BufferedReader(new FileReader("D:\\Downloads\\Analisadores_Compiladores\\src\\Compilador\\teste.txt"));
            String str = leitura.readLine();
            entrada += str + '\n';
            while(leitura.ready()){
                str = leitura.readLine();
                entrada += str + '\n';
            }
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erro na abertura do arquivo");
            System.exit(1);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Problema na leitura do arquivo");
            System.exit(1);
        }
        tab = new LinkedList<Tabela>();
        for(int i = 0; i < entrada.length(); i++){
            isFloat =  isOperador = isReservado =  isNewNumber = false;
            if(entrada.charAt(i) == '\n'){
                linha++;
                continue;
            }

            if(entrada.charAt(i) == '{'){
                isComentario = true;
                continue;
            }
            
            if(isComentario && entrada.charAt(i) != '}'){
                continue;
            }else if(entrada.charAt(i) == '}'){
                isComentario = false;
                continue;
            }
            
            //coment2
            /*if(entrada.charAt(i) == '/' && entrada.charAt(i+1) == '/') { 
                 isComentario = true;
                continue;
            }*/                      
            
            if(entrada.charAt(i) == ' ' || entrada.charAt(i) == '\t' || entrada.charAt(i) == '\r'){
                continue;
            }
            if(Character.isLetter(entrada.charAt(i))){
                String str = "" + entrada.charAt(i);
                i++;
                while(i<entrada.length()){
                    if(Character.isLetter(entrada.charAt(i)) || Character.isDigit(entrada.charAt(i)) || entrada.charAt(i) == '_'){
                        str+= entrada.charAt(i);
                        i++;
                    }else{
                        
                        //Testa se é um identificador ou palavra reservada ou operador de comparacao
                        for(String s : palavraReservada){
                            if(str.equals(s)){
                                isReservado = true;
                                tab.add(new Tabela(str, "Palavra Reservada", linha));
                                break;
                            }
                        }                     
                        if(str.equals("or")){
                            isOperador = true;
                            tab.add(new Tabela(str, "Operador Aditivo", linha));
                        }else if(str.equals("and")){
                            isOperador = true;
                            tab.add(new Tabela(str, "Operador Multiplicativo", linha));
                        }
                        
                        if(!isOperador && !isReservado){
                           tab.add(new Tabela(str,"Identificador\t",linha)); 
                        }
                        
                        break;
                    } 
                }
                i--;
                continue;
            }//if isLetter
            
            if(Character.isDigit(entrada.charAt(i))){
                String str = "" + entrada.charAt(i);
                i++;
                while(i<entrada.length()){
                    if(Character.isDigit(entrada.charAt(i)) || entrada.charAt(i) == '.' ){
                        str+= entrada.charAt(i);
                        if(entrada.charAt(i) == '.'){
                           // iV = i;
                            isFloat = true;
                        }
                        i++;

                    } 
                    //novo numero
                   /* if(entrada.charAt(i) == 'V' && entrada.charAt(i+1) == '+' || entrada.charAt(i+1) == '-' && Character.isDigit(entrada.charAt(i+2))) { 
                        isFloat = true;
                     }  
                    /else if(entrada.charAt(i) == '.' && isFloat == false){
                        isFloat = true;
                    }else if(entrada.charAt(i) == 'V') {
                        isNewNumber = true;
                    }*/
                    else{
                        //testa se é um identificador ou palavra reservada
                        if(isFloat){
                            tab.add(new Tabela(str,"float",linha));
                        }else{
                            tab.add(new Tabela(str,"Inteiro\t\t",linha));
                        }
                        break;
                    } 
                }
                i--;
                continue;
            }//if isDigit
            
            if(entrada.charAt(i) == ';' || entrada.charAt(i) == '.' || entrada.charAt(i) == '(' || entrada.charAt(i) == ')' || entrada.charAt(i) == ','){
                String str = "" + entrada.charAt(i);
                tab.add(new Tabela(str, "Delimitador\t", linha));
                continue;
            }//if delimitador
            
            if(entrada.charAt(i) == '<' || entrada.charAt(i) == '>'){
                String str = "" + entrada.charAt(i);
                
                if(entrada.charAt(i + 1) == '=' ){
                    str += entrada.charAt(i + 1);
                    tab.add(new Tabela(str, "Operador Relacional", linha));
                    i++;
                    continue;
                    
                }else if(entrada.charAt(i) == '<' && entrada.charAt(i + 1) == '>'){
                    str += entrada.charAt(i + 1);
                    tab.add(new Tabela(str, "Operador Relacional", linha));
                    i++;
                    continue;
                    
                }else{
                    tab.add(new Tabela(str, "Operador Relacional", linha));
                    continue;
                }
            }//if relacional 
            
            if(entrada.charAt(i) == ':'){
                String str = "" + entrada.charAt(i);
                if(entrada.charAt(i + 1) == '=' ){
                    str += entrada.charAt(i + 1);
                    tab.add(new Tabela(str, "Atribuicao\t", linha));
                    i++;
                    continue;
                }else{
                    tab.add(new Tabela(str, "Delimitador\t", linha));
                    continue;
                }
            }//if :
            
            if(entrada.charAt(i) == '+' || entrada.charAt(i) == '-'){
                String str = "" + entrada.charAt(i);
                tab.add(new Tabela(str, "Operador Aditivo", linha));
                continue;
            }//if Op Aditivo
            
            if(entrada.charAt(i) == '*' || entrada.charAt(i) == '/'){
                String str = "" + entrada.charAt(i);
                tab.add(new Tabela(str, "Operador Multiplicativo", linha));
                continue;                
            }//if op Multiplicativo
            
            if(!Character.isLetter(entrada.charAt(i)) || !Character.isDigit(entrada.charAt(i)) ||
               entrada.charAt(i) != '/' || entrada.charAt(i) != ':' || entrada.charAt(i) != '*'  ){
                String str = "" + entrada.charAt(i);
                tab.add(new Tabela(str,"Erro",linha));
            }//if erro
            
        }//for
        
        System.out.println("TOKEN\t\tTIPO\t\t\tLINHA\n");
        for(Tabela t : tab){
            System.out.println(t.toString());
        }
        Analisador_Sitatico();
    }//main
    
    public static void Analisador_Sitatico(){
        //elem = tab.get(indice++);
        System.out.println("\nInicio do Analisador Sintatico");
        Program();
        System.out.println("\nFim do Analisador Sintatico\n");
    }
    
    public static void ConsomeID(String str){
        if(tab.get(indice).id.equals(str)){
            indice++;
        }else{
            System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ str);
        }
    }
    
    public static void ConsomeTipo(String str){
        if(tab.get(indice).type.equals(str)){
            indice++;
        }else{
            System.out.println(" 1 = Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ str);
        }
    }
    
    public static void PreencheTipo(String s){
        for(Variaveis v : pilhaVar){
            if(v.tipo.equals("")){
                v.tipo += s;
            }
        }
    }
    
    public static boolean VerificaExistencia(String s, int escopoAtual){
        for(Variaveis v : pilhaVar){
            if(v.nome.equals(s) && v.escopo == escopoAtual){
                System.out.println("Variavel jah declarada");
                return true;
            }
        }
        return false;
    }
    
    public static void RemoveEscopo(){
        for(int i = 0; i < pilhaVar.size();i++){
            if(pilhaVar.get(i).escopo == escopo){
                pilhaVar.remove(pilhaVar.get(i));
                i--;
            }
        }
        escopo--;
    }
    
    public static void Program(){
//        if(elem.id.equals("Program")){
//            elem = tab.get(indice++);
//        }
        if(tab.get(indice).id.equals("program")){
            indice++;
            if(!VerificaExistencia(tab.get(indice).id, escopo)){
                pilhaVar.push(new Variaveis(tab.get(indice).id, "program", escopo));
            }
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
                if(tab.get(indice).id.equals(";")){
                    indice++;
                    DecVar();
                    DecSubProgs();
                    ComComp();
                    if(tab.get(indice).id.equals(".")){
                        indice++;
                    }else{
                        System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ".");
                    }
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
        }else{
            System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "program");
        }
    }
    
    public static void DecVar(){
        if(tab.get(indice).id.equals("var")){
            if(tab.get(indice).id.equals("var")){
                indice++;
                ListaDecVar();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "var");
            }
        }
    }
    //Tirei recursividade a esquerda de lista_declarações_variáveis e criou ListaDecVar e ListaDecVar2
    public static void ListaDecVar(){
        Lista();
        if(tab.get(indice).id.equals(":")){
            indice++;
            Tipo();
            if(tab.get(indice).id.equals(";")){
                indice++;
                ListaDecVar2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
            }
        }else{
            System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ":");
        }
    }
   
    public static void ListaDecVar2(){
        if(tab.get(indice).type.equals("Identificador\t")){
            ListaDecVar();
        }
    }
  //Novamente tirei a recursividade do lista_de_identificadores e criou Lista e Lista2  
    public static void Lista(){
        if(tab.get(indice).type.equals("Identificador\t")){
            if (!VerificaExistencia(tab.get(indice).id, escopo)) {
                pilhaVar.push(new Variaveis(tab.get(indice).id, "", escopo));
            }
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
                Lista2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");

            }
        }
    }
    
    public static void Lista2(){
        if(tab.get(indice).id.equals(",")){
            if(tab.get(indice).id.equals(",")){
                indice++;
                if (!VerificaExistencia(tab.get(indice).id, escopo)) {
                    pilhaVar.push(new Variaveis(tab.get(indice).id, "", escopo));
                }
                if(tab.get(indice).type.equals("Identificador\t")){
                    indice++;
                    Lista2();
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ",");
            }
        }
    }
    /*if(elem.id.equals(",")){
         elem = tab.get(indice++);
         if(elem.type.equals("Identificador\t")){
            Lista2();
        }
    }*/
    
    public static void Tipo(){
        if(tab.get(indice).id.equals("integer")){
            PreencheTipo("integer");
            if(tab.get(indice).id.equals("integer")){
                indice++; 
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "tipo");
            }
            
        }else if(tab.get(indice).id.equals("real")){
            PreencheTipo("real");
            if(tab.get(indice).id.equals("real")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "tipo");
            }
            
        } else if(tab.get(indice).id.equals("boolean")){
            PreencheTipo("boolean");
            if(tab.get(indice).id.equals("boolean")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "tipo");
            }
            
        }else if(!tab.get(indice).id.equals("boolean") && !tab.get(indice).id.equals("real") && 
                 !tab.get(indice).id.equals("integer")){
            System.out.println("Erro tipo");
        }
    }
    
    public static void DecSubProgs(){
        DecSubProg();
    }
    
    public static void DecSubProg(){
        if(tab.get(indice).id.equals("procedure")){
            DecSubP();
        }
    }
    
    public static boolean VerificaProc(String s, int escopoAtual){
        if(!pilhaProc.isEmpty()){
            for(Variaveis v : pilhaProc){
                if(v.nome.equals(s) && v.escopo == escopoAtual){
                    System.out.println("Procedimento ja declarado");
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void DecSubP(){
        if(tab.get(indice).id.equals("procedure")){
            indice++;
            if(!VerificaProc(tab.get(indice).id, escopo)){
                pilhaProc.push(new Variaveis(tab.get(indice).id, "procedure", escopo));
            }

            /*if (!VerificaExistencia(tab.get(indice).id, escopo)) {
                pilhaVar.push(new Variaveis(tab.get(indice).id, "procedure", escopo));
            }*/
            
            if(tab.get(indice).type.equals("Identificador")){
                indice++;
                escopo++;
                escopoFuncao = true;
                Argumentos();
                if(tab.get(indice).id.equals(";")){
                    indice++;
                    DecVar();
                    DecSubProgs();
                    ComComp();
                    if(tab.get(indice).id.equals(";")){
                        indice++;
                    }else{
                        System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
                    }
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
        }
    }
    
    public static void Argumentos(){
        if(tab.get(indice).id.equals("(")){
            if(tab.get(indice).id.equals("(")){
                indice++;
                ListaPar();
                if(tab.get(indice).id.equals(")")){
                    indice++;
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ")");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "(");
            }
        }
    }
    //Tirei a recursividade de Lista_de_parametros e criou ListaPar e ListaPar2
    public static void ListaPar(){
        if(tab.get(indice).id.equals("var")){
            indice++;
            Lista();
            if(tab.get(indice).id.equals(":")){
                indice++;
                Tipo();
                ListaPar2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ":");
            }
        }else{
            System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "var");
        }
    }
    
    public static void ListaPar2(){
        if(tab.get(indice).id.equals(";")){
            if(tab.get(indice).id.equals(";")){
                indice++;
                Lista();
                if(tab.get(indice).id.equals(":")){
                    indice++;
                    Tipo();
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ":");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
            }
        }
    }
    
    //Comando composto
    public static void ComComp(){
        if(tab.get(indice).id.equals("begin")){
            if(tab.get(indice).id.equals("begin")){
                indice++;
                if(!escopoFuncao){
                    escopo++;
                }else{
                    escopoFuncao = false;
                }
                ComOp();//Comandos opcionais
                if(tab.get(indice).id.equals("end")){
                    indice++;
                    RemoveEscopo();
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "end");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "begin");
            }
        }
    }
    
    //Comandos opcionais
    public static void ComOp(){
        if(tab.get(indice).id.equals("if") || tab.get(indice).id.equals("while") ||
           tab.get(indice).id.equals("else") || tab.get(indice).type.equals("Identificador\t") ||
           tab.get(indice).id.equals("do") ){
            ListaComando();
        }
    }
    
    //Tirou a recursividade de Lista_de_comandos e criou ListaComando e ListaComando2
    public static void ListaComando(){
        Comando();
        ListaComando2();
    }
    
    public static void ListaComando2(){
        if(tab.get(indice).id.equals(";")){
            if(tab.get(indice).id.equals(";")){
                indice++;
                Comando();
                ListaComando2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ";");
            }
        }
    }
    
    public static void Comando(){
        String tipo = "";
        if(tab.get(indice).type.equals("Identificador\t") && tab.get(indice + 1).id.equals(":=")){
            tipo += TipoVar(tab.get(indice).id);   
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
                if(tab.get(indice).id.equals(":=")){
                    indice++;
                    Expressao();
                    VerificaAtrib(tipo);
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ":=");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
        }else if(tab.get(indice).id.equals("if")){
            if(tab.get(indice).id.equals("if")){
                indice++;
                Expressao();
                if(tab.get(indice).id.equals("then")){
                    indice++;
                    Comando();
                    ParteElse();
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "then");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "if");
            }
        }else if(tab.get(indice).id.equals("while")){
            if(tab.get(indice).id.equals("while")){
                indice++;
                Expressao();
                if(tab.get(indice).id.equals("do")){
                    indice++;
                    Comando();
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "do");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "while");
            }
        }else if(tab.get(indice).type.equals("Identificador\t") && !tab.get(indice + 1).id.equals(":=")){
            AtivProc();
        }else{
            ComComp();
        }
    }
    
    public static void ParteElse(){
        if(tab.get(indice).id.equals("else")){
            Comando();
        }
    }
    
    public static void AtivProc(){
        if(tab.get(indice).type.equals("Identificador\t") && tab.get(indice + 1).id.equals("(")){
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
                if(tab.get(indice).id.equals("(")){
                    indice++;
                    ListaExp();
                    if(tab.get(indice).id.equals(")")){
                        indice++;
                    }else{
                        System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ")");
                    }
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "(");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
        }
    }
    
    public static void ListaExp(){
        Expressao();
        ListaExp2();
    }
    
    public static void ListaExp2(){
        if(tab.get(indice).id.equals(",")){
            if(tab.get(indice).id.equals(",")){
                indice++;
                Expressao();
                ListaExp2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ",");
            }
        }
    }
    
    public static void Expressao(){
        ExpSimples();
        Expressao2();
    }
    
    public static void Expressao2(){
        if(tab.get(indice).type.equals("Operador Relacional")){
            if(tab.get(indice).type.equals("Operador Relacional")){
                indice++;
                ExpSimples();
                VerificaExpressao("Operador Relacional");
                Expressao2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Operador Relacional");
            }
        }
    }
    
    public static void ExpSimples(){
        if(tab.get(indice).id.equals("+")){
            if(tab.get(indice).id.equals("+")){
                indice++;
                Termo();
                VerificaExpressao("sUnario");
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "+");
            }
        }else if(tab.get(indice).id.equals("-")){
            if(tab.get(indice).id.equals("-")){
                indice++;
                Termo();
                VerificaExpressao("sUnario");
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "-");
            }
        }else{
            Termo();
            ExpSimples2();
        }
    }
    
    public static void ExpSimples2(){
        String op = "";
        if(tab.get(indice).type.equals("Op Add")){
            op += tab.get(indice).id;
            if(tab.get(indice).type.equals("Op Add")){
                indice++;
                Termo();
                VerificaExpressao(op);
                ExpSimples2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Op Add");
            }
        }
    }
    
    public static void Termo(){
        Fator();
        Termo2();
    }
    
    public static void Termo2(){
        String op = "";
        if(tab.get(indice).type.equals("Op Mult")){
            op += tab.get(indice).id;
            if(tab.get(indice).type.equals("Op Mult")){
                indice++;
                Fator();
                VerificaExpressao(op);
                Termo2();
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Op Mult");
            }
        }
    }
    
    public static void Fator(){
        if(tab.get(indice).type.equals("Identificador\t") && tab.get(indice + 1).id.equals("(")){
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
                if(tab.get(indice).id.equals("(")){
                    indice++;
                    ListaExp();
                    if(tab.get(indice).id.equals(")")){
                        indice++;
                    }else{
                        System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ")");
                    }
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "(");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
            
        }else if(tab.get(indice).type.equals("Identificador\t") && !tab.get(indice + 1).id.equals("(")){
            pilhaTipo.push(TipoVar(tab.get(indice).id));
            if(tab.get(indice).type.equals("Identificador\t")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Identificador\t");
            }
            
        }else if(tab.get(indice).type.equals("Inteiro\t\t")){
            pilhaTipo.push("integer");
            if(tab.get(indice).type.equals("Inteiro\t\t")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "Inteiro\t\t");
            }
            
        }else if(tab.get(indice).type.equals("float")){
            pilhaTipo.push("real");
            if(tab.get(indice).type.equals("float")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "float");
            }
            
        }else if(tab.get(indice).id.equals("true")){
            pilhaTipo.push("boolean");
            if(tab.get(indice).id.equals("true")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "valor boolean");
            }
            
        }else if(tab.get(indice).id.equals("false")){
            pilhaTipo.push("boolean");
            if(tab.get(indice).id.equals("false")){
                indice++;
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "valor boolean");
            }
            
        }else if(tab.get(indice).id.equals("(")){
            if(tab.get(indice).id.equals("(")){
                indice++;
                Expressao();
                if(tab.get(indice).id.equals(")")){
                    indice++;
                }else{
                    System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ ")");
                }
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "(");
            }
            
        }else if(tab.get(indice).id.equals("not")){
            if(tab.get(indice).id.equals("not")){
                indice++;
                Fator();
                VerificaExpressao("not");
            }else{
                System.out.println("Erro na linha "+ tab.get(indice).linha + " " + tab.get(indice).id + " esperado "+ "not");
            }
        }
    }
    
    public static String TipoVar(String s){
        String aux = "";
        for(Variaveis v : pilhaVar){
            if(v.nome.equals(s)){
                aux = "";
                aux += v.tipo;
            }
        }
        if(aux.equals("")){
            System.out.println("Variavel nao declarada.");
            return null;
        }
        return aux;
    }
    
    public static void VerificaExpressao(String op){
        String operando1 = "", operando2 = "";
        
        if(op.equals("not")){
            operando1 += pilhaTipo.pop();
            if(operando1.equals("boolean")){
                pilhaTipo.push(operando1);
            }else{
                System.out.println("Erro de tipo: operador " + op);
            }
            
        } else if(op.equals("*") || op.equals("/") || op.equals("+") || op.equals("-")){
            operando1 += pilhaTipo.pop();
            operando2 += pilhaTipo.pop();
            
            if(operando1.equals("integer") && operando2.equals("integer")){
                pilhaTipo.push("integer");
                
            }else if( (operando1.equals("real") && operando2.equals("integer")) || (operando1.equals("integer") && operando2.equals("real")) || (operando1.equals("real") && operando2.equals("real"))){
                
                pilhaTipo.push("real");
                
            }else if(!operando1.equals("integer") && !operando1.equals("real")){
                System.out.println("Erro de tipo: operador " + op);
                
            }else if(!operando2.equals("integer") && !operando2.equals("real")){
                System.out.println("Erro de tipo: operador " + op);
            }
        }else if(op.equals("and") || op.equals("or")){
            operando1 += pilhaTipo.pop();
            operando2 += pilhaTipo.pop();
            
            if(operando1.equals("boolean") && operando2.equals("boolean")){
                pilhaTipo.push("boolean");
            }else{
                System.out.println("Erro de tipo: operador " + op + ": " + operando1 + " " + operando2);
            }
            
        }else if(op.equals("Operador Relacional")){
            operando1 += pilhaTipo.pop();
            operando2 += pilhaTipo.pop();
            
            if( (operando1.equals("integer") || operando1.equals("real")) && (operando2.equals("integer") || operando2.equals("real")) ){
                
                pilhaTipo.push("boolean");
                
            }else{
                System.out.println("Erro de tipo: operador " + op);
            }
            
        }else if(op.equals("sUnario")){
            operando1 += pilhaTipo.pop();
            
            if(operando1.equals("integer") || operando1.equals("real")){
                pilhaTipo.push(operando1);
            }else{
                System.out.println("Erro de tipo: operador " + op);
            }
        }
        operando1 = "";
        operando2 = "";
    }
    
    public static void VerificaAtrib(String s){
        String op = pilhaTipo.pop();
        
        if( ((s.equals("integer")) && op.equals("real")) || (s.equals("boolean") && !op.equals("boolean"))  ){
            System.out.println("Erro de atribuicao");
        }
    }
}
